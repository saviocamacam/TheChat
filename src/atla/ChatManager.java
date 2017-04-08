package atla;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;

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
	private InetAddress destinationIp = null;
	private String messageString = null;
	private byte[] messageBytes = null;
	private boolean statusChat;
	private DatagramPacket request = null;
	private InetAddress group;
	
	
	public ChatManager(String apelido, int privatePort, int multicastPort) {
		setPeers(new LinkedList<>());
		this.apelide = apelido;
		this.privatePort = privatePort;
		this.multicastPort = multicastPort;
		this.scanner = new Scanner(System.in);
		
		try {
			this.group = InetAddress.getByName("225.1.2.3");
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
			multicastThread = new MulticastListeningThread(multicastSocket, this, group);
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
		
		multicastThread = new MulticastListeningThread(multicastSocket, this, group);
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

	public void sendPrivateMessage() {
		DatagramSocket socketPrivate = null;
		
		try {
			socketPrivate = new DatagramSocket();
			String formatedMessage = apelide + "|||" + messageString;
			messageBytes = formatedMessage.getBytes();
			request = new DatagramPacket(messageBytes, messageBytes.length, destinationIp, privatePort);
			socketPrivate.send(request);
			socketPrivate.close();
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void createPeersTest() {
		try {
			peers.add(new Peer(InetAddress.getByName("localhost"), "henrique"));
			peers.add(new Peer(InetAddress.getByName("localhost"), "vitorio"));
			peers.add(new Peer(InetAddress.getByName("localhost"), "otavio"));
			peers.add(new Peer(InetAddress.getByName("localhost"), "daniel"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printNameOfPeers() {
		int i = 0;
		for(Peer peer : peers) {
			System.out.println("(" + i++ + ")" + peer.getApelido() + ":" + peer.getIp());
		}
	}

	public void sendMessageFor(int option) {
		Peer privatePeer = peers.get(option);
		this.destinationIp = privatePeer.getIp();
		sendPrivateMessage();
	}

	public void requestMessage(int option) {
		if(option == 1) {
			System.out.println("IP Destino: ");
			String destinationIpString = scanner.nextLine();
			try {
				destinationIp = InetAddress.getByName(destinationIpString);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			//destinationVerify();
		}
		
		System.out.println("MSG: ");
		messageString = scanner.nextLine();
	}

	private void destinationVerify() {
		for(Peer peer : peers) {
			if(!peer.getIp().equals(destinationIp)) {
				peers.add(new Peer(destinationIp, ""));
			}
		}
	}

	public boolean getStatusMulticast() {
		return statusChat;
	}

	public void setStatusChat(boolean b) {
		this.statusChat = b;
	}

	public void sendGroupMessage() {
		System.out.println("MSG:");
		messageString = scanner.nextLine();
		String formatedMessage = apelide + "|||" + messageString;
		messageBytes = formatedMessage.getBytes();
		request = new DatagramPacket(messageBytes, messageBytes.length, group, multicastPort);
		try {
			multicastSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendControlMessage(String string, int mode) {
		
		String formatedMessage = apelide + "|||" + string;
		messageBytes = formatedMessage.getBytes();
		int port = multicastPort;
		
		if(mode == 1) {
			port = privatePort;
		}
		request = new DatagramPacket(messageBytes, messageBytes.length, group, port);
		try {
			multicastSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
