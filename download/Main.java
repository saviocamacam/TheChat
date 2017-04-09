package ann;

import java.util.Scanner;

public class Main {
	
	public static int op = -1;
	public static Scanner scanner;
	public static String caminho;
	public static String path;
	
	public static void main(String[] args) {
			
		GerenciadorArquivos gerenciador = null;
		do{
			System.out.println("(1) Informar um programa para contagem\n(2) Realizar contagem");
			scanner = new Scanner(System.in);
			
			op = scanner.nextInt();
			
			if(op == 1) {
				System.out.println("Informe o caminho para a pasta\n");
				caminho = scanner.next();
				gerenciador = new GerenciadorArquivos(caminho);
			}
			
			else if(op == 2) {
				if (gerenciador == null)
					gerenciador = new GerenciadorArquivos();
				gerenciador.carregarArquivos();
				gerenciador.eliminaLinhasBranco();
				System.out.println("Total de Linhas: " + gerenciador.getTotalLinhas());
				
				for (Arquivo arquivo : gerenciador.getArquivos()) {
					System.out.println("Nome da parte: " + arquivo.getNome());
					System.out.println("Tamanho: " + arquivo.getQuantidadeLinhas());
					System.out.println("Itens: " + arquivo.getQuantidadeItens());
				}
			}
			
		} while(op != 7);
		
	}

}
