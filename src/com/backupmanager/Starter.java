package com.backupmanager;

import java.io.File;
import java.util.ArrayList;

public class Starter {
	
	private static void searchDirectory(String rootDirectoryModel, String rootDirectoryReceiver) {
		
		System.out.println("Analyzing directory: " + rootDirectoryModel);
		
		DirectoryScanner modelScanner = new DirectoryScanner(rootDirectoryModel, false);
		modelScanner.startScanning();
		
		DirectoryScanner receiverScanner = new DirectoryScanner(rootDirectoryReceiver, false);
		receiverScanner.startScanning();
		
		ArrayList<FileDetails> model = modelScanner.getFileList();
		
		ArrayList<FileDetails> receiver = receiverScanner.getFileList();
		
		for(FileDetails evalFile : model) {
			//TODO: find best way to O(n^2) using sort and binary search
			for(FileDetails evalFileRec : receiver) {
				if(evalFile.getName().equals(evalFileRec.getName())) {
					
					evalFile.setVisited(true);
					evalFileRec.setVisited(true);
					//System.out.println("Hash1: " + evalFile.getHash() + "\nHash2: " + evalFileRec.getHash());
					
					if(evalFile.getHash().equals(evalFileRec.getHash())) {
						//System.out.println("File " + evalFile.getName() + " is equal in " + evalFileRec.getPath());
					}
					else {
						System.out.println("\tFile " + evalFile.getName() + " is different in " + evalFileRec.getPath());
					}
					break;
				}
				else {
					if(evalFile.getHash().equals(evalFileRec.getHash())) {
						System.out.println("Old file name: " + evalFileRec.getName() + " for file " + evalFile.getName());
					}
				}
			}
			if(!evalFile.isVisited()) {
				System.out.println("\tFile " + evalFile.getName() + " is new");
			}
		}
		for(FileDetails evalFileRec : receiver) {
			if(!evalFileRec.isVisited()) {
				System.out.println("\tOld file " + evalFileRec.getName() + " remains");
			}
		}
		
		ArrayList<FileDetails> modelDirectories = modelScanner.getDirectoriesList();
		
		for(FileDetails innerDirectory : modelDirectories) {
			
			String internalModel = rootDirectoryModel + "\\" + innerDirectory.getName();
			String internalReceiver = rootDirectoryReceiver + "\\" + innerDirectory.getName();
			
			File currentDirectory = new File(internalReceiver);
			if(currentDirectory.exists()) {
				searchDirectory(internalModel, internalReceiver);
			}
			else {
				System.out.println("\tDirectory " + internalReceiver + " not found");
			}
		}
	}

	public static void main(String[] args) {
		String rootDirectoryModel = "F:\\";
		String rootDirectoryReceiver = "G:\\";
		searchDirectory(rootDirectoryModel, rootDirectoryReceiver);
	}
}
