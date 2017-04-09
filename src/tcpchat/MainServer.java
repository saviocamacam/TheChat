package tcpchat;


public class MainServer {

	public static void main(String[] args) {
		
		String address = args[0];
		
		Gerenciador gerenciador = new Gerenciador(7896, 7897);
		
		ServerListeningThread server = new ServerListeningThread(7897, gerenciador);
		server.start();
		
		String msg = null;
		
		do {
			msg = gerenciador.requestMessage();
			gerenciador.sendServerMessage(address, msg);
			
		} while(!msg.equals("sair"));
		System.out.println("Encerrou Main Server");
	}
}
