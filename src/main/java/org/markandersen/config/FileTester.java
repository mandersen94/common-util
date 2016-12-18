package org.markandersen.config;

import java.io.File;

public class FileTester extends File {

	/**
	 * 
	 * @param pathname
	 */
	public FileTester() {
		super("./");
	}

	/**
	 * 
	 */
	@Override
	public boolean exists() {
		
		return true;
		
	}
	
	
}
