package com.backupmanager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHasher {

	private String filePath;

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public FileHasher(String filePath) {
		this.filePath = filePath;
	}
	
	public byte[] createChecksum(String filename)  {
		byte[] result = null;
		try {
			InputStream fis =  new FileInputStream(filename);
	
			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
	
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			
			fis.close();
			return complete.digest();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		catch(NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}
		return result;
	}

	public String getMD5Checksum() {
		byte[] b = createChecksum(filePath);
		String result = "";
		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}
}
