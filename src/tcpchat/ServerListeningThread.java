package tcpchat;



import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListeningThread extends Thread {
	
	private ServerSocket listenSocket = null;
	private String message = "";
	private Gerenciador gerenciador;
	private DataInputStream in;
	
	public ServerListeningThread(int serverPort, Gerenciador gerenciador) {
		this.gerenciador = gerenciador;
		try {
			this.listenSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			while(!message.equals("sair")) {
				System.out.println ("Server waiting for connection... Here it lock...");
				
	            Socket serverSocket = listenSocket.accept();
	            
	            System.out.println ("Client connected ... No longer locked...");
	            
	            in = new DataInputStream(serverSocket.getInputStream());
	            message = in.readUTF();
				System.out.println ("Client says: " + message);
				in.close();
				serverSocket.close();
	
	            //ServerConnectionThread c = new ServerConnectionThread(serverSocket, this);
	            //c.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Thread listening encerrada: Server");
		gerenciador.sendServerMessage(listenSocket.getInetAddress().getHostAddress(), "sair");
	}

	public void setMessage(String data) {
		this.message = data;
	}

}
