package org.markandersen.logger;

import java.io.File;

import org.markandersen.logger.RollingFileLogger;
import org.markandersen.test.BaseTestCase;

public class FileRollerTest extends BaseTestCase {

	public void testFileRolling() {

		String filename = "./rollingLogger.txt";
		clearFiles(filename);
		RollingFileLogger logger = new RollingFileLogger(filename, 10 * 1024, 3);

		int count = 1000;
		String message = " lskjweroiuqpweojas;ldkfjas;ld/zx.cvmz/.,xcmlsajkdfqopiweu";
		for (int i = 0; i < count; i++) {
			logger.log(i + " " + message);
		}
	}

	/**
	 * 
	 * @param filename
	 */
	private void clearFiles(String filename) {
		clearFile(filename);
		int count = 0;
		boolean result = true;
		while (result) {
			result = clearFile(filename + "." + count);
			count++;
		}

	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	private boolean clearFile(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			try {
				file.delete();
				return true;
			} catch (Exception ex) {
				return false;
			}
		} else {
			return false;
		}
	}
}
