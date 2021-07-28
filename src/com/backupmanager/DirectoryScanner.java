package com.backupmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class DirectoryScanner {
	
	private String currentPath;
	private ArrayList<FileDetails> fileList;
	private ArrayList<FileDetails> directoriesList;
	private boolean debugMode;
	
	public DirectoryScanner(String currentPath, boolean debugMode) {
		this.currentPath = currentPath;
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

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	
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
				hasher.setFilePath(path);
				hash = hasher.getMD5Checksum();
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
