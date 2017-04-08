package atla;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UDPListeningThread extends Thread {
	
	private DatagramSocket udpSocket = null;
	private byte[] buffer = null;
	private DatagramPacket request = null;
	private ChatManager chatManager= null;
	
	public UDPListeningThread(DatagramSocket udpSocket, ChatManager chatManager) {
		this.udpSocket = udpSocket;
		this.setChatManager(chatManager);
		buffer = new byte[1000];
		request = new DatagramPacket(buffer, buffer.length);
	}
	
	public void run() {
		String message = null;
			
		try {
			while(true) {
				this.udpSocket.receive(request);
				message = new String(request.getData(), request.getOffset(), request.getLength());
				
				if (message.matches(".*\\|\\|\\|joinACK")) {
					Pattern pattern = Pattern.compile("([a-z1-9]*)");
					Matcher matcher = pattern.matcher(message);
					matcher.find();
					
					String apelide = matcher.group(1);
					Peer peer = new Peer(request.getAddress(), apelide);
					
					if(!chatManager.getPeers().contains(peer)) {
						System.out.println(apelide + " entrou!");
						chatManager.getPeers().add(peer);
					}
					
					chatManager.sendControlMessage(chatManager.getApelido() + "|||joinACK", 1);
				}
				
				else if(message.matches(".*\\|\\|\\|.*")) {
					System.out.println("Mensagem recebida: " + message);
				}
				else{
					System.out.println("Mensagem recebida em formato inapropriado. Erro de protocolo");
					String replyString = "Mensagem n�o processada. Erro de protocolo.";
					byte[] replyBytes = replyString.getBytes();
					DatagramPacket reply = new DatagramPacket(replyBytes, replyBytes.length, request.getAddress(), request.getPort());
					this.udpSocket.send(reply);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

	public void setChatManager(ChatManager chatManager) {
		this.chatManager = chatManager;
	}

}
