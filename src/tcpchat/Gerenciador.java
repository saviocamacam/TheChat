package tcpchat;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Gerenciador {
	
	private int serverPortListening;
	private int clientPortListening;
	private String message;
	private Scanner scanner;
	
	public Gerenciador(int clientPortListening, int serverPortListening) {
		this.serverPortListening = clientPortListening;
		this.clientPortListening = serverPortListening;
		this.scanner = new Scanner(System.in);
	}

	public String requestMessage() {
		System.out.println("Informe mensagem para enviar");
		message = scanner.nextLine();
		return message;
	}

	public void sendServerMessage(String address, String msg) {
		Socket s = null;
		
		try {
			s = new Socket(address, serverPortListening);
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.writeUTF(msg);
			System.out.println ("Informação enviada.");
			out.close(); 
			s.close();
			
		} catch (UnknownHostException e){
			System.out.print("Aqui?");
	    	System.out.println("Socket:"+e.getMessage());
        } catch (EOFException e){
    		System.out.println("EOF:"+e.getMessage());
        } catch (IOException e){
        	System.out.println("leitura:"+e.getMessage());
        }
	}
	
	public void sendClientMessage(String address, String msg) {
		Socket s = null;
		
		try {
			s = new Socket(address, clientPortListening);
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.writeUTF(msg);
			System.out.println ("Informação enviada.");
			out.close(); 
			s.close();
			
		} catch (UnknownHostException e){
	    	System.out.println("Socket:"+e.getMessage());
        } catch (EOFException e){
    		System.out.println("EOF:"+e.getMessage());
        } catch (IOException e){
        	System.out.println("leitura:"+e.getMessage());
        }
	}

}
