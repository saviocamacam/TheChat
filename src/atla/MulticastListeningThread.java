package atla;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MulticastListeningThread extends Thread {
	
	private MulticastSocket multicastSocket = null;
	private InetAddress group = null;
	private byte[] messageByte = null;
	private ChatManager chatManager;
	
	public MulticastListeningThread(MulticastSocket multicastSocket, ChatManager chatManager, InetAddress group) {
		this.multicastSocket = multicastSocket;
		this.messageByte = new byte[1000];
		this.chatManager = chatManager;
		this.group = group;
	}
	
	public void run() {
		try {
			this.multicastSocket.joinGroup(group);
			DatagramPacket messageIn = null;
			
			chatManager.setStatusChat(true);
			
			while(chatManager.getStatusMulticast()) {
				messageIn = new DatagramPacket(messageByte, messageByte.length);
				multicastSocket.receive(messageIn);
				
				String message = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
				
				
				if (message.matches("JOIN \\[[a-zA-Z1-9]+\\]")) {
					Pattern pattern = Pattern.compile("JOIN \\[([a-zA-Z1-9]+)\\]");
					Matcher matcher = pattern.matcher(message);
					matcher.find();
					
					String apelide = matcher.group(1);
					Peer peer = new Peer(messageIn.getAddress(), apelide);
					
					if(!chatManager.getPeers().contains(peer) && !peer.getApelido().equals(chatManager.getApelido())) {
						System.out.println(apelide + " entrou!");
						chatManager.getPeers().add(peer);
					}
					chatManager.setPrivateAdress(peer.getIp());
					chatManager.sendFormatedMessage(1, 2);
				}
				
				else if(message.matches("LEAVE \\[([a-zA-Z1-9]+)\\]")) {
					Pattern pattern = Pattern.compile("LEAVE \\[([a-zA-Z1-9]+)\\]");
					Matcher matcher = pattern.matcher(message);
					matcher.find();
					
					String apelide = matcher.group(1);
					System.out.println(apelide + " saiu!");
					Peer peer = new Peer(messageIn.getAddress(), apelide);
					chatManager.getPeers().remove(peer);
				}
				
				else if(message.matches("MSG \\[([a-zA-Z1-9]+)\\] (.*)")) {
					Pattern pattern = Pattern.compile("MSG \\[([a-zA-Z1-9]+)\\] (.*)");
					Matcher matcher = pattern.matcher(message);
					matcher.find();
					
					String apelide = matcher.group(1);
					String message2 = matcher.group(2);
					
					System.out.println(apelide + " diz: " + message2);
				}
				
				else {
					System.out.println("Mensagem recebida em formato inapropriado. Erro de protocolo");
					String replyString ="MSG [" + chatManager.getApelido() + "] Mensagem nao processada. Erro de protocolo.";
					byte[] replyBytes = replyString.getBytes();
					DatagramPacket reply = new DatagramPacket(replyBytes, replyBytes.length, messageIn.getAddress(), messageIn.getPort());
					this.multicastSocket.send(reply);
				}
				
			}
			multicastSocket.leaveGroup(group);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (multicastSocket != null) multicastSocket.close();
		}
	}

}
