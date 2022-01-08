package com.backupmanager;

public class Results {

	private int filesAdded;
	private int filesUpdated;
	private int filesRenamed;
	private int filesDeleted;
	private int problems;
	
	public Results(int filesAdded, int filesUpdated, int filesRenamed, int filesDeleted, int problems) {
		this.filesAdded = filesAdded;
		this.filesUpdated = filesUpdated;
		this.filesRenamed = filesRenamed;
		this.filesDeleted = filesDeleted;
		this.problems = problems;
	}

	public int getFilesAdded() {
		return filesAdded;
	}

	public void setFilesAdded(int filesAdded) {
		this.filesAdded = filesAdded;
	}

	public int getFilesUpdated() {
		return filesUpdated;
	}

	public void setFilesUpdated(int filesUpdated) {
		this.filesUpdated = filesUpdated;
	}

	public int getFilesRenamed() {
		return filesRenamed;
	}

	public void setFilesRenamed(int filesRenamed) {
		this.filesRenamed = filesRenamed;
	}

	public int getFilesDeleted() {
		return filesDeleted;
	}

	public void setFilesDeleted(int filesDeleted) {
		this.filesDeleted = filesDeleted;
	}

	public int getProblems() {
		return problems;
	}

	public void setProblems(int problems) {
		this.problems = problems;
	}
}
