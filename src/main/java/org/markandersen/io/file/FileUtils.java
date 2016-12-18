package org.markandersen.io.file;

import java.io.File;
import java.io.FileNotFoundException;

public class FileUtils {

	FileUtils() {

	}

	/**
	 * Copies file a to file b.
	 * 
	 * @throws FileNotFoundException
	 */
	public void copyFile(File fromFile, File toFrile)
			throws FileNotFoundException {
		if (!fromFile.exists()) {
			throw new FileNotFoundException("file " + fromFile.getName()
					+ " does not exist.");
		}
		
		
	}
}
