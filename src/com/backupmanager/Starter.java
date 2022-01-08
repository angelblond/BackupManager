package com.backupmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Properties;

public class Starter {
	
	private static Properties loadPropertiesFile(){
		String sep = File.separator;
		String propertiesFileName = System.getProperty("user.dir") + sep + "misc" + sep + "settings.properties";
		Properties settings = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(propertiesFileName);
			settings.load(input);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if (input != null) {
				try {
					input.close();
				} 
				catch (IOException ioe2) {
					ioe2.printStackTrace();
				}
			}
		}
		return settings;
	}
	
	
	private static int copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
		int totalFiles = 0;
	    if (!destinationDirectory.exists()) {
	        destinationDirectory.mkdir();
	    }
	    for (String oneFile : sourceDirectory.list()) {
	        totalFiles += copyDirectoryMain(new File(sourceDirectory, oneFile), new File(destinationDirectory, oneFile));
	    }
	    return totalFiles;
	}
	
	private static int copyDirectoryMain(File source, File destination) {
		int fileCount = 0;
		if (source.isDirectory()) {
			try {
				fileCount += copyDirectory(source, destination);
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
			}
		} 
		else {
			try {
				Files.copy(source.toPath(), destination.toPath());
				fileCount++;
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return fileCount;
	}
	
	private static Results searchDirectory(String rootDirectoryModel, String rootDirectoryReceiver, Properties settings) {
		String scanningMode = settings.getProperty("scanningMode");
		String differencePolicy = settings.getProperty("differencePolicy");
		String oldNamesPolicy = settings.getProperty("oldNamesPolicy");
		String newFilesPolicy = settings.getProperty("newFilesPolicy");
		String oldFilesPolicy = settings.getProperty("oldFilesPolicy");
		String debugMode = settings.getProperty("debugMode");
		boolean debug = Boolean.parseBoolean(debugMode);
		
		//Statistics
		int filesAdded = 0;
		int filesUpdated = 0;
		int filesRenamed = 0;
		int filesDeleted = 0;
		int problems = 0;
		
		System.out.println("Analyzing directory: " + rootDirectoryModel);
		
		DirectoryScanner modelScanner = new DirectoryScanner(rootDirectoryModel, scanningMode, debug);
		modelScanner.startScanning();
		
		DirectoryScanner receiverScanner = new DirectoryScanner(rootDirectoryReceiver, scanningMode, debug);
		receiverScanner.startScanning();
		
		ArrayList<FileDetails> model = modelScanner.getFileList();
		
		ArrayList<FileDetails> receiver = receiverScanner.getFileList();
		
		for(FileDetails evalFile : model) {
			//TODO: find best way to O(n^2) using sort and binary search
			for(FileDetails evalFileRec : receiver) {
				if(evalFile.getName().equals(evalFileRec.getName())) {
					
					evalFile.setVisited(true);
					evalFileRec.setVisited(true);
					
					if(scanningMode.equals("full")) {
						//CASE: Same filename, different content
						if(!evalFile.getHash().equals(evalFileRec.getHash())) {
							
							String baseMessage = "\tFile " + evalFile.getName() + " is different in " + evalFileRec.getPath() + ". ";
							
							if(differencePolicy.equals("replace")) {
								File receptorFile = new File(evalFileRec.getPath());
								File newFile = new File(evalFile.getPath());
								try {
									Files.copy(newFile.toPath(), receptorFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
									filesUpdated++;
									System.out.println(baseMessage + "Replaced by newest version.");
								}
								catch(IOException ioe) {
									ioe.printStackTrace();
									System.out.println(baseMessage + "Problems replacing by newest version.");
									problems++;
								}
								
							}
							else if(differencePolicy.equals("both")) {
								String originalPath = evalFileRec.getPath();
								int lastIndex = originalPath.lastIndexOf(".");
								String theNewPath = originalPath.substring(0, lastIndex) + "_new" + originalPath.substring(lastIndex, originalPath.length());
								File receptorFile = new File(theNewPath);
								
								File newFile = new File(evalFile.getPath());
								try {
									Files.copy(newFile.toPath(), receptorFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
									filesAdded++;
									System.out.println(baseMessage + "Created new file version alongside with old one.");
								}
								catch(IOException ioe) {
									ioe.printStackTrace();
									System.out.println(baseMessage + "Problems creating new file version.");
									problems++;
								}
							}
							else if(differencePolicy.equals("avoid")) {
								System.out.println(baseMessage + "Old file preserved without changes.");
							}
						}
					}
					break;
				}
				else {
					if(scanningMode.equals("full")) {
						//CASE: different filename, same content
						if(evalFile.getHash().equals(evalFileRec.getHash())) {
							String baseMessage = "\tOld file name: " + evalFileRec.getName() + " for file " + evalFile.getName() + ". ";
							
							evalFile.setVisited(true);
							evalFileRec.setVisited(true);
							
							if(oldNamesPolicy.equals("rename")) {
								File newFile = new File(evalFileRec.getPath());
								String originalPath = evalFileRec.getPath();
								int lastIndex = originalPath.lastIndexOf("\\");
								String theNewPath = originalPath.substring(0,lastIndex) + "\\" + evalFile.getName();
								
								File receptorFile = new File(theNewPath);
								
								try {
									Files.move(newFile.toPath(), receptorFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
									filesRenamed++;
									System.out.println(baseMessage + "File renamed as original.");
								}
								catch(IOException ioe) {
									System.out.println(baseMessage + "Problems renaming file");
									problems++;
									ioe.printStackTrace();
								}
							}
							else if(oldNamesPolicy.equals("remain")) {
								System.out.println(baseMessage + "File remains as is.");
							}
						}
					}
				}
			}
			//CASE: new file found
			if(!evalFile.isVisited()) {
				String baseMessage = "\tFile " + evalFile.getName() + " is new. ";
				if(newFilesPolicy.equals("add")) {
					String theNewPath = rootDirectoryReceiver + "\\" + evalFile.getName();
					
					File receptorFile = new File(theNewPath);
					
					File newFile = new File(evalFile.getPath());
					
					try {
						Files.copy(newFile.toPath(), receptorFile.toPath());
						filesAdded++;
						System.out.println(baseMessage + "File has been added.");
					}
					catch(IOException ioe) {
						System.out.println(baseMessage + "Problems adding file.");
						problems++;
						ioe.printStackTrace();
					}
				}
				else if(newFilesPolicy.equals("avoid")) {
					System.out.println(baseMessage + "File has NOT been added.");
				}
			}
		}
		for(FileDetails evalFileRec : receiver) {
			
			//CASE: old file (possibly deleted) found
			if(!evalFileRec.isVisited()) {
				String baseMessage = "\tOld file " + evalFileRec.getName() + " found. ";
				
				if(oldFilesPolicy.equals("delete")) {
					File currentFile = new File(evalFileRec.getPath());
					boolean successfullyDeleted = currentFile.delete();
					if(successfullyDeleted) {
						System.out.println(baseMessage + "File has been deleted");
						filesDeleted++;
					}
					else {
						System.out.println(baseMessage + "Problems deleting file.");
						problems++;
					}
				}
				else if(oldFilesPolicy.equals("remain")) {
					System.out.println(baseMessage + "File remains");
				}
				
			}
		}
		
		ArrayList<FileDetails> modelDirectories = modelScanner.getDirectoriesList();
		
		for(FileDetails innerDirectory : modelDirectories) {
			
			String internalModel = rootDirectoryModel + "\\" + innerDirectory.getName();
			String internalReceiver = rootDirectoryReceiver + "\\" + innerDirectory.getName();
			
			File currentDirectory = new File(internalReceiver);
			if(currentDirectory.exists()) {
				//CASE: recursion
				Results innerResults = searchDirectory(internalModel, internalReceiver, settings);
				filesAdded += innerResults.getFilesAdded();
				filesUpdated += innerResults.getFilesUpdated();
				filesRenamed += innerResults.getFilesRenamed();
				filesDeleted += innerResults.getFilesDeleted();
				problems += innerResults.getProblems();
			}
			else {
				//CASE: new directory found 
				String baseMessage = "\tDirectory " + internalReceiver + " not found. ";
				
				if(newFilesPolicy.equals("add")) {
					File newDirectory = new File(innerDirectory.getPath());
					File receptorDirectory = new File(rootDirectoryReceiver + "\\" + innerDirectory.getName());
					try {
						filesAdded += copyDirectory(newDirectory, receptorDirectory);
						System.out.println(baseMessage + "Directory has been added.");
					}
					catch(IOException ioe) {
						ioe.printStackTrace();
						System.out.println(baseMessage + "Problem adding directory.");
						problems++;
					}
				}
				else if(newFilesPolicy.equals("avoid")) {
					System.out.println(baseMessage + "Directory has NOT been added.");
				}
			}
		}
		
		if(debug) {
			System.out.println("\nIn directory " + rootDirectoryReceiver + "\n");
			System.out.println("Files added: " + filesAdded);
			System.out.println("Files updated: " + filesUpdated);
			System.out.println("Files renamed: " + filesRenamed);
			System.out.println("Files deleted: " + filesDeleted);
			System.out.println("Problems with files or directories: " + problems + "\n");
		}
		
		Results levelResults = new Results(filesAdded, filesUpdated, filesRenamed, filesDeleted, problems);
		
		return levelResults;
	}

	public static void main(String[] args) {
		Properties settings = loadPropertiesFile();
		String rootDirectoryModel = settings.getProperty("rootDirectoryModel");
		String rootDirectoryReceiver = settings.getProperty("rootDirectoryReceiver");
		Results finalResults = searchDirectory(rootDirectoryModel, rootDirectoryReceiver, settings);
		System.out.println("\n*********************************************");
		System.out.println("Synchronization Summary");
		System.out.println("In directory " + rootDirectoryReceiver + "\n");
		System.out.println("Files added: " + finalResults.getFilesAdded());
		System.out.println("Files updated: " + finalResults.getFilesUpdated());
		System.out.println("Files renamed: " + finalResults.getFilesRenamed());
		System.out.println("Files deleted: " + finalResults.getFilesDeleted());
		System.out.println("Problems with files or directories: " + finalResults.getProblems() + "\n");
		System.out.println("*********************************************");
		System.out.println("\n\nALL TASKS FINISHED");
	}
}
