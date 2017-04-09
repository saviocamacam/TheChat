package ann;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GerenciadorArquivos {
	private String rootPath = Paths.get("").toAbsolutePath(). toString();
	private String subPath = "/src/ann/";
    
	private File folder = null;
	private File[] listOfFiles = null;
	private int totalLinhas = 0;
	
	private LinkedList<Arquivo> arquivos;
	
	public GerenciadorArquivos(String path) {
		this.folder = new File(rootPath + path);
	}
	
	public GerenciadorArquivos() {
		this.folder = new File(rootPath + subPath);
	}
	
	public void carregarArquivos() {
		this.listOfFiles = folder.listFiles();
		List<String> linhas = new ArrayList<>();
		arquivos = new LinkedList<>();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	
		    	try {
		    		Arquivo arquivo = new Arquivo(file.getName());
		    		arquivo.setLinhas(Files.readAllLines(file.toPath()));
					arquivos.add(arquivo);
			    	linhas = Files.readAllLines(file.toPath());
		    	} catch (IOException e) {
					e.printStackTrace();
				}

		        System.out.println(">>>>>>>>" + file.getName());
		        for (String str : linhas) {
		    		System.out.println("(" + str.length() + "- "+ str);
		    	}
		    }
		}
	}
	
	public void eliminaLinhasBranco() {
		
		for (Arquivo arquivo : arquivos) {
			arquivo.recontaLinhas();
		}
	}

	public int getTotalLinhas() {
		if(totalLinhas == 0) {
			for (Arquivo arquivo : arquivos) {
				this.totalLinhas = totalLinhas + arquivo.getQuantidadeLinhas();
			}
		}
		return totalLinhas;
	}

	public LinkedList<Arquivo> getArquivos() {
		return arquivos;
	}

	public void setArquivos(LinkedList<Arquivo> arquivos) {
		this.arquivos = arquivos;
	}

}
