package tcpchat;


public class MainClient {

	public static void main(String[] args) {
		
		String address = args[0];
		
		Gerenciador gerenciador = new Gerenciador(7896, 7897);
		
		ClientListeningThread client = new ClientListeningThread(7896, gerenciador);
		client.start();
		
		String msg = null;
		
		do {
			msg = gerenciador.requestMessage();
			gerenciador.sendClientMessage(address, msg);
			
		} while(!msg.equals("sair"));
	}
}
