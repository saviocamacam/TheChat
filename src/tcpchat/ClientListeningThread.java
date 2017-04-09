package tcpchat;



import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListeningThread extends Thread {
	
	private ServerSocket listenSocket = null;
	private String message = "";
	private Gerenciador gerenciador;
	private DataInputStream in;
	
	public ClientListeningThread(int clientPort, Gerenciador gerenciador) {
		this.gerenciador = gerenciador;
		try {
			this.listenSocket = new ServerSocket(clientPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			while(!message.equals("sair")) {
				System.out.println ("Client waiting for connection... Here it lock...");
				
	            Socket clientSocket = listenSocket.accept();
	            
	            System.out.println ("Server connected... No longer locked...");
	            
	            in = new DataInputStream(clientSocket.getInputStream());
	            message = in.readUTF();
	            System.out.println ("Client says: " + message);
				in.close();
				clientSocket.close();
	
	            //ClientConnectionThread c = new ClientConnectionThread(clientSocket, this, gerenciador);
	            //c.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Thread listening encerrada: Client");
		gerenciador.sendClientMessage(listenSocket.getInetAddress().getHostAddress(), "sair");
	}

	public void setMessage(String data) {
		this.message = data;
	}

}
