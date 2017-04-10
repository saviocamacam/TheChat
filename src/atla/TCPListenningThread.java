package atla;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListenningThread extends Thread {
	
	private ServerSocket listenSocket = null;
	private ChatManager chatManager;
	
	public TCPListenningThread(ServerSocket serverSocket, ChatManager chatManager) {
		this.chatManager = chatManager;
		this.listenSocket = serverSocket;
	}
	
	public void run() {
		
		
        try {
        	System.out.println ("Server waiting for connection... Here it lock...");
			Socket serverSocket = listenSocket.accept();
			System.out.println ("Client connected ... No longer locked...");
			
			File file = chatManager.getFileToUpload();
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			OutputStream os = serverSocket.getOutputStream();
			
			byte[] contents;
			long current = 0;
			long fileLength = file.length(); 
			while(current!=fileLength){ 
	            int size = 10000;
	            if(fileLength - current >= size)
	                current += size;
	            else{ 
	                size = (int)(fileLength - current); 
	                current = fileLength;
	            } 
	            contents = new byte[size]; 
	            bis.read(contents, 0, size); 
	            os.write(contents);
	        }
	        os.flush(); 
	        serverSocket.close();
	        listenSocket.close();
	        System.out.println("File sent succesfully!");
			
		
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
}
