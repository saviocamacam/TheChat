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
				
				if (message.matches("JOINACK \\[(.+)\\]")) {
					Pattern pattern = Pattern.compile("JOINACK \\[(.+)\\]");
					Matcher matcher = pattern.matcher(message);
					matcher.find();
					
					String apelide = matcher.group(1);
					Peer peer = new Peer(request.getAddress(), apelide);
					
					if(!chatManager.getPeers().contains(peer)) {
						System.out.println(apelide + " entrou!");
						chatManager.getPeers().add(peer);
					}
				}
				
				else if(message.matches("MSG \\[(.+)\\] (.*)")) {
					Pattern pattern = Pattern.compile("MSG \\[(.+)\\] (.*)");
					Matcher matcher = pattern.matcher(message);
					matcher.find();
					
					String apelide = matcher.group(1);
					String message2 = matcher.group(2);
					
					System.out.println(apelide + " diz: " + message2);
				}
				
				else if(message.matches("MSGIDV FROM \\[(.+)\\] TO \\[(.+)\\] (.*)")) {
					Pattern pattern = Pattern.compile("MSGIDV FROM \\[(.+)\\] TO \\[(.+)\\] (.*)");
					Matcher matcher = pattern.matcher(message);
					matcher.find();
					
					String apelide = matcher.group(1);
					String message2 = matcher.group(3);
					
					System.out.println(apelide + " diz: " + message2);
				}
				else if(message.matches("LISTFILES \\[(.+)\\]")) {
					Pattern pattern = Pattern.compile("LISTFILES \\[(.+)\\]");
					Matcher matcher = pattern.matcher(message);
					matcher.find();
					
					String apelide = matcher.group(1);
					
					System.out.println(apelide + " pediu para baixar. Respondendo...: ");
					this.chatManager.setPrivateAddress(request.getAddress());
					this.chatManager.setPrivatePort(request.getPort());
					this.chatManager.sendFormatedMessage(1, 6);
				}
				else if(message.matches("FILES \\[(.+)\\]")) {
					String nameFiles = this.chatManager.extractLocaleInformation("FILES \\[(.+)\\]", message, 1);
					String[] nameFilesArray = nameFiles.split("(, )");
					int i;
					for(i = 0 ; i < nameFilesArray.length ; i++) {
						System.out.println("Arquivo " + i + ": " + nameFilesArray[i]);
					}
				}
				else if(message.matches("DOWNFILE \\[(.+)\\] ((.+)[.]([a-z1-9]+))")) {
					String apelide = this.chatManager.extractLocaleInformation("DOWNFILE \\[(.+)\\] ((.+)[.]([a-z1-9]+))", message, 1);
					String nameFile = this.chatManager.extractLocaleInformation("DOWNFILE \\[(.+)\\] ((.+)[.]([a-z1-9]+))", message, 2);
					
					this.chatManager.setPrivateAddress(request.getAddress());
					this.chatManager.setPrivatePort(request.getPort());
					
					if(chatManager.hasFile(nameFile)) {
						this.chatManager.getTcpThread().start();
						this.chatManager.uploadFile(nameFile);
						
						this.chatManager.sendFormatedMessage(1, 8);
						System.out.println("Enviando " + nameFile + " para " + apelide);
						
						this.chatManager.setMessageString("Upload do arquivo em andamento.");
						this.chatManager.sendFormatedMessage(1, 4);
					}
					else {
						System.out.println(apelide + " solicitou o download de um arquivo nao encontrado.");
						this.chatManager.setMessageString("Arquivo solicitado nao foi encontrado.");
						this.chatManager.sendFormatedMessage(1, 4);
					}
				}
				else if(message.matches("DOWNINFO \\[((.+)[.]([a-z1-9]+)), ([1-9]+), (.*), ([1-9]+)\\]")) {
					String nameFile = this.chatManager.extractLocaleInformation("DOWNFILE \\[(.+)\\] ((.+)[.]([a-z1-9]+))", message, 1);
					String fileSize = this.chatManager.extractLocaleInformation("DOWNFILE \\[(.+)\\] ((.+)[.]([a-z1-9]+))", message, 2);
					int fileSizeInt = Integer.valueOf(fileSize);
					String peerAddress = this.chatManager.extractLocaleInformation("DOWNFILE \\[(.+)\\] ((.+)[.]([a-z1-9]+))", message, 3);
					String peerPort = this.chatManager.extractLocaleInformation("DOWNFILE \\[(.+)\\] ((.+)[.]([a-z1-9]+))", message, 4);
					int peerPortInt = Integer.valueOf(peerPort);
					
					chatManager.downloadFile(nameFile, fileSizeInt, peerAddress, peerPortInt);
				}
				else{
					System.out.println(message);
					System.out.println("PRIV: Mensagem recebida em formato inapropriado. Erro de protocolo");
					String replyString ="MSG [" + chatManager.getApelido() + "] Mensagem nao processada. Erro de protocolo.";
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
