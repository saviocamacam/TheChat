package atla;

import java.util.Scanner;

public class Main {
	
	public static Scanner scanner = new Scanner(System.in);
	public static int option = 0;
	
	public static void main(String[] args) {
		
		//System.out.println("Informe seu apelido: ");
		//String apelide = scanner.nextLine();
		String apelide = "savio";
		System.out.println(apelide);
		
		ChatManager chatManager = new ChatManager(apelide, 5555, 5566);
		chatManager.initialize();
		
		do {
			scanner = new Scanner(System.in);
			
			System.out.println("(1) Entrar no chat em grupo");
			System.out.println("(2) Mensagem em grupo");
			System.out.println("(3) Mensagem individual");
			System.out.println("(4) Listar arquivos");
			System.out.println("(5) Baixar arquivo\n");
			System.out.println("(6) Sair do grupo de conversacao\n");
			
			option = scanner.nextInt();
			
			switch(option) {
				case 1:
					if(chatManager.getMulticastSocket().isClosed())
						chatManager.startMulticastSocket();
					
					chatManager.setStatusChat(true);
					chatManager.sendFormatedMessage(null, 0, 1);
					
				case 2:
					if(chatManager.getStatusMulticast())
						chatManager.sendFormatedMessage(null, 0, 3);
					else System.out.println("Você não está no chat");
					
				case 3:
					if(chatManager.getPeers().size() == 0) {
						System.out.println("Lista de endereços está vazia");
					}
					else{
						chatManager.printNameOfPeers();
						int option = scanner.nextInt();
						chatManager.requestMessage(0);
						if(option >= 0 && option < chatManager.getPeers().size())
							chatManager.sendMessageFor(option);
						else
							System.out.println("Erro de índice");
					}
					
				case 4:
					chatManager.sendFormatedMessage(null, 1, 5);
					
				case 5:
					chatManager.sendFormatedMessage(null, 1, 7);
					
				case 6:
					chatManager.setStatusChat(false);
					chatManager.sendFormatedMessage("", 0, 9);
					
				default:
					break;
			}
		} while(option != 7);
	}

}
