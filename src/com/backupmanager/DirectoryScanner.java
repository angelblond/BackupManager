package com.backupmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class DirectoryScanner {
	
	static {
		System.setProperty("sun.jnu.encoding", "UTF-8");
	}
	
	private String currentPath;
	private ArrayList<FileDetails> fileList;
	private ArrayList<FileDetails> directoriesList;
	private String scanningMode;
	private boolean debugMode;
	
	public DirectoryScanner(String currentPath, String scanningMode, boolean debugMode) {
		this.currentPath = currentPath;
		this.scanningMode = scanningMode;
		this.debugMode = debugMode;
		this.fileList = new ArrayList<FileDetails>();
		this.directoriesList = new ArrayList<FileDetails>();
	}
	
	public String getCurrentPath() {
		return this.currentPath;
	}
	
	public ArrayList<FileDetails> getFileList() {
		return this.fileList;
	}
	
	public ArrayList<FileDetails> getDirectoriesList() {
		return directoriesList;
	}

	public void setScanningMode(String scanningMode) {
		this.scanningMode = scanningMode;
	}
	
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	
	//TODO: check if there are duplicates inside the same folder first
	public void startScanning() {
		File directory = new File(currentPath);
		String[] fileList = directory.list();
		
		FileHasher hasher = new FileHasher("");
		
		for(String aFile : fileList) {
			
			File currentFile = new File(currentPath + "\\" + aFile);
			
			String name = aFile;
			String path = currentPath + "\\" + aFile;
			String hash = "";
			long size = 0;
			Date lastModified = new Date();
			boolean isDirectory = currentFile.isDirectory();
			
			
			
			if(isDirectory) {
				FileDetails oneDir = new FileDetails(name, path, hash, size, lastModified, isDirectory);
				this.directoriesList.add(oneDir);
			}
			else {
				
				if(this.scanningMode.equals("full")) {
					hasher.setFilePath(path);
					hash = hasher.getMD5Checksum();
				}
				
				size = currentFile.length();
				lastModified = new Date(currentFile.lastModified());
				
				FileDetails oneFile = new FileDetails(name, path, hash, size, lastModified, isDirectory);
				this.fileList.add(oneFile);
				
				if(debugMode) {
					oneFile.printDetails();
					System.out.println("");
				}
			}
			
		}
	}
}
