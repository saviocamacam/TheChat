package atla;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager {
	private DatagramSocket udpSocket = null;
	private MulticastSocket multicastSocket = null;
	private UDPListeningThread udpThread = null;
	private MulticastListeningThread multicastThread = null;
	private LinkedList<Peer> peers = null;
	private String apelide;
	private int privatePort;
	private int multicastPort;
	private Scanner scanner = null;
	private InetAddress privateAddress = null;
	private String messageString = null;
	private byte[] messageBytes = null;
	private boolean statusChat;
	private DatagramPacket request = null;
	
	private InetAddress multicastAddress;
	
	private String apelideDestination = null;
	
	private String filename = null;
	private String filesize;
	private List<String> listOfNameFiles;
	
	
	public ChatManager(String apelido, int privatePort, int multicastPort) {
		setPeers(new LinkedList<>());
		this.apelide = apelido;
		this.privatePort = privatePort;
		this.multicastPort = multicastPort;
		this.scanner = new Scanner(System.in);
		
		try {
			this.multicastAddress = InetAddress.getByName("225.1.2.3");
			udpSocket = new DatagramSocket(privatePort);
			multicastSocket = new MulticastSocket(multicastPort);
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void startMulticastSocket() {
		try {
			multicastSocket = new MulticastSocket(multicastPort);
			multicastThread = new MulticastListeningThread(multicastSocket, this, multicastAddress);
			multicastThread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public MulticastSocket getMulticastSocket() {
		return multicastSocket;
	}

	public void initialize() {
		udpThread = new UDPListeningThread(udpSocket, this);
		udpThread.start();
		
		multicastThread = new MulticastListeningThread(multicastSocket, this, multicastAddress);
		multicastThread.start();
	}

	public String getApelido() {
		return apelide;
	}

	public LinkedList<Peer> getPeers() {
		return peers;
	}

	public void setPeers(LinkedList<Peer> peers) {
		this.peers = peers;
	}

	public void printNameOfPeers() {
		int i = 0;
		for(Peer peer : peers) {
			System.out.println("(" + i++ + ")" + peer.getApelido() + ":" + peer.getIp());
		}
	}

	public boolean getStatusMulticast() {
		return statusChat;
	}

	public void setStatusChat(boolean b) {
		this.statusChat = b;
	}
	
	public void sendControlMessage(String string, int mode) {
		
		String formatedMessage = apelide + "|||" + string;
		messageBytes = formatedMessage.getBytes();
		int port = multicastPort;
		
		if(mode == 1) {
			port = privatePort;
		}
		request = new DatagramPacket(messageBytes, messageBytes.length, multicastAddress, port);
		try {
			multicastSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendFormatedMessage(int destinationMode, int typeMessage) {
		
		String formatedMessage = "";
		
		switch(typeMessage) {
			case 1: formatedMessage = "JOIN [" + apelide + "]";
				break;
			case 2: formatedMessage = "JOINACK [" + apelide + "]";
				break;
			case 3: formatedMessage = "MSG [" + apelide + "] " + messageString;
				break;
			case 4: formatedMessage = "MSGIDV FROM [" + apelide + "] TO [" + apelideDestination + "] " + messageString;
				break;	
			case 5: formatedMessage = "LISTFILES [" + apelide + "]";
				break;
			case 6: 
				formatedMessage = "FILES [";
				int i;
				for(i = 0; i < listOfNameFiles.size()-1 ; i++) {
					formatedMessage.concat(listOfNameFiles.get(i) + ", ");
				}
				formatedMessage.concat(listOfNameFiles.get(i));
				formatedMessage.concat("]");
				break;
			case 7: formatedMessage = "DOWNFILE [" + apelide + "] " + filename;
				break;
			case 8: formatedMessage = "DOWNINFO [" + filename + ", " + filesize + ", " + privateAddress + ", " + privatePort + "]";
				break;
			case 9: formatedMessage = "LEAVE [" + apelide + "]";
				break;
			default: formatedMessage = "Vish, deu merda aqui. Foi mal, aqui e o " + apelide;
		}
		
		int port = multicastPort;
		InetAddress localAddress = multicastAddress;
		
		messageBytes = formatedMessage.getBytes();
		
		switch(destinationMode) {
			case 1: 
				port = privatePort;
				localAddress = privateAddress;
				break;
			default: port = multicastPort;
		}
		
		request = new DatagramPacket(messageBytes, messageBytes.length, localAddress, port);
		try {
			multicastSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void requestMessage() {
		System.out.println("MSG: ");
		messageString = scanner.nextLine();
	}
	
	public void sendMessageFor(int option) {
		Peer privatePeer = peers.get(option);
		this.privateAddress = privatePeer.getIp();
		sendFormatedMessage(1, 4);
	}

	public void sendGroupMessage() {
		System.out.println("MSG:");
		messageString = scanner.nextLine();
		String formatedMessage = "MSG[" + apelide + "] " + messageString;
		messageBytes = formatedMessage.getBytes();
		request = new DatagramPacket(messageBytes, messageBytes.length, multicastAddress, multicastPort);
		try {
			multicastSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPrivateAdress(InetAddress address) {
		this.privateAddress = address;
	}
	
	public String extractLocaleInformation(String regex, String message, int group) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(message);
		matcher.find();
		return matcher.group(group);
	}

}
