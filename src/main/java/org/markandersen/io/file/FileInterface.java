package org.markandersen.io.file;

import java.io.FileNotFoundException;

public interface FileInterface {

	public java.io.InputStream getInputStream() throws FileNotFoundException;
	
	public java.io.OutputStream getOutputStream() throws FileNotFoundException;

	public long lastModified();

	public String getName();

	public boolean exists();
	
}
