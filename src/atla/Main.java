package atla;

import java.util.Scanner;

public class Main {
	
	public static Scanner scanner = new Scanner(System.in);
	public static int option = 0;
	
	public static void main(String[] args) {
		
		System.out.println("Informe seu apelido: ");
		String apelide = scanner.nextLine();
		//String apelide = "savio";
		System.out.println(apelide);
		
		ChatManager chatManager = new ChatManager(apelide, 6799, 6789, 7777);
		chatManager.initialize();
		
		do {
			scanner = new Scanner(System.in);
			
			System.out.println("(1) Entrar no chat em grupo");
			System.out.println("(2) Mensagem em grupo");
			System.out.println("(3) Mensagem individual");
			System.out.println("(4) Listar arquivos");
			System.out.println("(5) Baixar arquivo");
			System.out.println("(6) Sair do grupo de conversacao\n");
			
			option = scanner.nextInt();
			
			switch(option) {
				case 1:
					if(chatManager.getMulticastSocket().isClosed())
						chatManager.startMulticastSocket();
					
					chatManager.setStatusChat(true);
					chatManager.sendFormatedMessage(0, 1);
					break;
					
				case 2:
					if(chatManager.getStatusMulticast()) {
						System.out.println("Mensagem em grupo:");
						chatManager.requestMessage();
						chatManager.sendFormatedMessage(0, 3);
					}
					else System.out.println("Voce não esta no chat");
					break;
					
				case 3:
					if(chatManager.getPeers().size() == 0) {
						System.out.println("Lista de enderecos esta vazia");
					}
					else{
						chatManager.printNameOfPeers();
						int option = scanner.nextInt();
						
						if(option >= 0 && option < chatManager.getPeers().size()) {
							System.out.println("Mensagem individual:");
							chatManager.requestMessage();
							chatManager.sendMessageFor(option);
						}
						else
							System.out.println("Erro de indice");
					}
					break;
					
				case 4:
					if(chatManager.getPeers().size() == 0) {
						System.out.println("Lista de enderecos esta vazia");
					}
					else{
						chatManager.printNameOfPeers();
						int option = scanner.nextInt();
						
						if(option >= 0 && option < chatManager.getPeers().size()) {
							chatManager.loadPeerOption(option);
							chatManager.sendFormatedMessage(1, 5);
						}
						else
							System.out.println("Erro de indice");
					}
					break;
					
				case 5:
					if(chatManager.getPeers().size() == 0) {
						System.out.println("Lista de enderecos esta vazia");
					}
					else{
						chatManager.printNameOfPeers();
						int option = scanner.nextInt();
						
						if(option >= 0 && option < chatManager.getPeers().size()) {
							System.out.println("Nome do arquivo:");
							chatManager.requestMessage();
							chatManager.loadPeerOption(option);
							chatManager.sendFormatedMessage(1, 7);
						}
						else
							System.out.println("Erro de indice");
					}
					break;
					
				case 6:
					chatManager.setStatusChat(false);
					chatManager.sendFormatedMessage(0, 9);
					break;
					
				default:
					break;
			}
		} while(option != 7);
	}

}
