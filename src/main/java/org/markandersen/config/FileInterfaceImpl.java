package org.markandersen.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.markandersen.io.file.FileInterface;

public class FileInterfaceImpl implements FileInterface {

	private File file;

	public FileInterfaceImpl(String filename) {
		file = new File(filename);
	}
	
	public FileInterfaceImpl(File file){
		this.file = file;
	}

	public boolean exists() {
		return file.exists();
	}

	public InputStream getInputStream() throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public String getName() {
		return null;
	}

	public OutputStream getOutputStream() throws FileNotFoundException {
		return new FileOutputStream(file);
	}

	public long lastModified() {
		return file.lastModified();
	}

}
