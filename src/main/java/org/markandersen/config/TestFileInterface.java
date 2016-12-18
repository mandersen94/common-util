package org.markandersen.config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import org.markandersen.io.file.FileInterface;

public class TestFileInterface implements FileInterface {

	private boolean exists = false;

	private InputStream inputStream;

	private String name;

	private OutputStream outputStream;

	private long lastModified;

	public boolean exists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public InputStream getInputStream() throws FileNotFoundException {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getName() {

		return name;
	}

	public OutputStream getOutputStream() throws FileNotFoundException {
		return outputStream;
	}

	public void setOutputStream(OutputStream os) {
		outputStream = os;
	}

	public long lastModified() {

		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
}
