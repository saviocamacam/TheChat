package atla;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DownloadManager {
	private String rootPath = Paths.get("").toAbsolutePath(). toString();
	private String subPathDownload = "/download/";
	private String subPathUpload = "/upload/";
	
	private File folderDownload = null;
	private File folderUpload = null;
	
	private File[] listOfFilesDownload = null;
	private File[] listOfFilesUpload = null;
	
	private List<String> listOfNameFilesDownload;
	private List<String> listOfNameFilesUpload;
	
	public DownloadManager() {
		this.folderDownload = new File(rootPath + subPathDownload);
		this.folderUpload = new File(rootPath + subPathUpload);
		System.out.println("Aqui " + rootPath + subPathUpload);
		this.listOfFilesUpload = folderUpload.listFiles();
		this.listOfNameFilesUpload = new ArrayList<>();
		loadFilesUpload();
	}
	
	public void loadFilesUpload() {
		for(File file : listOfFilesUpload) {
			if (file.isFile()) {
				//System.out.println(file.getName());
				listOfNameFilesUpload.add(file.getName());
			}
		}
	}

	public File[] getListOfFilesUpload() {
		return listOfFilesUpload;
	}

	public List<String> getListOfNameFilesUpload() {
		return listOfNameFilesUpload;
	}
	
	public boolean hasFile(String nameFile) {
		for(String file : listOfNameFilesUpload) {
			if(file.equals(nameFile)) return true;
		}
		return false;
	}

	public File getFileByName(String nameFile) {
		for(File file : listOfFilesUpload) {
			if(file.isFile() && file.getName().equals(nameFile)) return file;
		}
		return null;
	}
}
