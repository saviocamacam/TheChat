package atla;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager {
	private DatagramSocket udpSocket = null;
	private MulticastSocket multicastSocket = null;
	private ServerSocket serverSocket = null;
	private UDPListeningThread udpThread = null;
	private MulticastListeningThread multicastThread = null;
	private TCPListenningThread tcpThread = null;
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
	private DownloadManager downloadManager;
	private int tcpPort;
	private File fileToUpload;
	private InetAddress myIp;
	
	
	public ChatManager(String apelido, int privatePort, int multicastPort, int tcpPort) {
		setPeers(new LinkedList<>());
		this.apelide = apelido;
		this.privatePort = privatePort;
		this.multicastPort = multicastPort;
		this.tcpPort = tcpPort;
		this.downloadManager = new DownloadManager();
		this.scanner = new Scanner(System.in);
		
		try {
			this.multicastAddress = InetAddress.getByName("225.1.2.3");
			udpSocket = new DatagramSocket(privatePort);
			multicastSocket = new MulticastSocket(multicastPort);
			multicastSocket.setLoopbackMode(true);
			this.myIp = multicastSocket.getInterface();
			serverSocket = new ServerSocket(tcpPort);
			
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
		
		tcpThread = new TCPListenningThread(serverSocket, this);
		
	}

	public TCPListenningThread getTcpThread() {
		return tcpThread;
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
			case 5:
				downloadManager.loadFilesUpload();
				formatedMessage = "LISTFILES [" + apelide + "]";
				break;
			case 6: 
				formatedMessage = "FILES [";
				int i;
				downloadManager.loadFilesUpload();
				for(i = 0; i < downloadManager.getListOfNameFilesUpload().size()-1 ; i++) {
					formatedMessage = formatedMessage.concat(downloadManager.getListOfNameFilesUpload().get(i) + ", ");
				}
				formatedMessage = formatedMessage.concat(downloadManager.getListOfNameFilesUpload().get(i));
				formatedMessage = formatedMessage.concat("]");
				break;
			case 7: 
				filename = messageString;
				formatedMessage = "DOWNFILE [" + apelide + "] " + filename;
				break;
			case 8: 
				System.out.println("Envio do arquivo: " + filename+ " tam: " + fileToUpload.length() + " Meu ip " + myIp.getHostAddress() + " porta tcp: " + tcpPort);
				formatedMessage = "DOWNINFO [" + filename + ", " + fileToUpload.length() + ", " + myIp.getHostAddress() + ", " + tcpPort + "]";
				break;
			case 9: formatedMessage = "LEAVE [" + apelide + "]";
				break;
			default: formatedMessage = "Vish, deu merda aqui. Foi mal, aqui e o " + apelide;
		}
		
		messageBytes = formatedMessage.getBytes();
		try {
			switch(destinationMode) {
				case 1: 
					request = new DatagramPacket(messageBytes, messageBytes.length, privateAddress, privatePort);
					udpSocket.send(request);
					break;
				default:
					request = new DatagramPacket(messageBytes, messageBytes.length, multicastAddress, multicastPort);
					multicastSocket.send(request);
			}
		
			
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void requestMessage() {
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
	
	public String extractLocaleInformation(String regex, String message, int group) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(message);
		matcher.find();
		return matcher.group(group);
	}

	public void loadPeerOption(int option) {
		Peer privatePeer = peers.get(option);
		System.out.println("Peer selecionado: " + privatePeer.getApelido() + " port: " + privatePeer.getIp());
		this.privateAddress = privatePeer.getIp();
		this.apelideDestination = privatePeer.getApelido();
	}

	public void setPrivatePort(int privatePort) {
		this.privatePort = privatePort;
	}

	public void setPrivateAddress(InetAddress privateAddress) {
		this.privateAddress = privateAddress;
	}

	public boolean hasFile(String fileName) {
		if(downloadManager.hasFile(fileName)) {
			this.filename = fileName;
			this.fileToUpload = downloadManager.getFileByName(fileName);
			this.tcpThread.start();
			return true;
		}
		return false;
	}

	public File getFileToUpload() {
		return fileToUpload;
	}

	public void setMessageString(String string) {
		this.messageString = string;
	}

	public void downloadFile(String nameFile, int fileSizeInt, String peerAddress, int peerPortInt) {
		 try {
			System.out.println("Address da fonte: " + peerAddress);
			System.out.println("Port da fonte: " + peerPortInt);
			Socket socket = new Socket(InetAddress.getByName(peerAddress), peerPortInt);
			byte[] contents = new byte[fileSizeInt];
			
			FileOutputStream fos = new FileOutputStream(downloadManager.getFolderDownload() + "/"  + nameFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			InputStream is = socket.getInputStream();
			
			int bytesRead = 0; 
	        while((bytesRead=is.read(contents))!=-1)
            bos.write(contents, 0, bytesRead); 
		        
	        bos.flush(); 
	        socket.close();
	        is.close();
	        fos.close();
		        
	        System.out.println("File saved successfully!");
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	}

}
