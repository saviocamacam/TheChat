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
			System.out.println("(2) Sair do chat em grupo");
			System.out.println("(3) Manda mensagem em grupo");
			System.out.println("(4) Mensagem para Novo endereço");
			System.out.println("(5) Mensagem para alguém da lista");
			System.out.println("(6) Criar Lista de Teste");
			System.out.println("(7) Encerrar Lista de Teste\n");
			//System.out.print("Opcao: ");
			
			option = scanner.nextInt();
			
			if(option == 1) {
				if(chatManager.getMulticastSocket().isClosed())
					chatManager.startMulticastSocket();
				
				chatManager.setStatusChat(true);
				chatManager.sendControlMessage("join", 0);
			}
			else if(option == 2) {
				chatManager.setStatusChat(false);
				chatManager.sendControlMessage("leave", 0);
			}
			else if(option == 3) {
				if(chatManager.getStatusMulticast())
					chatManager.sendGroupMessage();
				else System.out.println("Você não está no chat");
			}
			else if(option == 4) {
				chatManager.requestMessage(1);
				chatManager.sendPrivateMessage();
			}
			else if(option == 5) {
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
			}
			else if(option == 6) {
				chatManager.createPeersTest();
			}
			else if(option == 7) {
				chatManager.setPeers(null);;
			}
		} while(option != 8);
	}

}
