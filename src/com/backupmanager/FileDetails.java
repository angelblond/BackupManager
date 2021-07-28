package com.backupmanager;

import java.util.Calendar;
import java.util.Date;

public class FileDetails {

	private String name;
	private String path;
	private String hash;
	private long size;
	private Date lastModified;
	private boolean isDirectory;
	private boolean visited;
	
	public FileDetails(String name, String path, String hash, long size, Date lastModified, boolean isDirectory) {
		super();
		this.name = name;
		this.path = path;
		this.hash = hash;
		this.size = size;
		this.lastModified = lastModified;
		this.isDirectory = isDirectory;
		this.visited = false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public void setSHA1(String hash) {
		this.hash = hash;
	}
	
	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
	public Date getLastModified() {
		return this.lastModified;
	}
	
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public boolean isDirectory() {
		return this.isDirectory;
	}
	
	public boolean isVisited() {
		return this.visited;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	
	public void printDetails() {
		System.out.println("File: " + this.getName());
		System.out.println("Hash: " + this.getHash());
		System.out.println("Size: " + this.getSize());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.getLastModified());
		System.out.println("Modified: " +
				calendar.get(Calendar.YEAR) + "/" + 
				(calendar.get(Calendar.MONTH) +1) + "/" + 
				calendar.get(Calendar.DAY_OF_MONTH)+ " " +
				calendar.get(Calendar.HOUR_OF_DAY)+ ":" +
				calendar.get(Calendar.MINUTE) + ":" +
				calendar.get(Calendar.SECOND));
	}
}
