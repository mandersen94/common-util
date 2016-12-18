package org.markandersen.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.helpers.LogLog;

public class RollingFileWriter {

	private File file;
	private FileWriter fw;
	private CountingWriter cw;
	private int maxFileSize = 1024;
	private int maxBackupIndex = 1;
	private String fileName;

	public RollingFileWriter(String filename) throws IOException {
		fileName = filename;
		file = new File(filename);
		fw = new FileWriter(file);
		cw = new CountingWriter(fw);
	}

	/**
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void write(String message) throws IOException {
		cw.write(message);
		if (cw.getCount() > maxFileSize) {
			rollFile();
		}
	}

	/**
	 * Copy every file over when then open a new one.
	 */
	private void rollFile() {
		try {
			File target;
			File file;

			// If maxBackups <= 0, then there is no file renaming to be done.
			if (maxBackupIndex > 0) {
				// Delete the oldest file, to keep Windows happy.
				file = new File(fileName + '.' + maxBackupIndex);
				if (file.exists()) {
					file.delete();
				}

				// Map {(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex,
				// ..., 3, 2}
				for (int i = maxBackupIndex - 1; i >= 1; i--) {
					file = new File(fileName + "." + i);
					if (file.exists()) {
						target = new File(fileName + '.' + (i + 1));
						file.renameTo(target);
					}
				}

				// Rename fileName to fileName.1
				target = new File(fileName + "." + 1);

				this.closeFile(); // keep windows happy.

				file = new File(fileName);
				LogLog.debug("Renaming file " + file + " to " + target);
				file.renameTo(target);
			}
		} catch (Exception ex) {

		}
	}

	/**
	 * 
	 */
	private void closeFile() {
		if (this.cw != null) {
			try {
				this.cw.close();
			} catch (IOException ex) {
			}
		}

	}

	public void close() throws IOException {
		cw.close();
	}
}
