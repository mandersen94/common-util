package org.markandersen.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * 
 */
public class FileUtil {
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static String readInFileFromFileSystemAsString(String filename)
			throws IOException {

		FileReader fr = null;
		try {
			fr = new FileReader(filename);
			return readInStringFromReader(fr);
		} finally {
			try {
				if (fr != null)
					fr.close();
			} catch (Exception ex) {
			}
		}

	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static String readInFileFromClasspathAsString(String filename)
			throws IOException {

		InputStreamReader isr = null;
		try {
			URL url = FileUtil.class.getClassLoader().getResource(filename);
			isr = new InputStreamReader(url.openStream(), "UTF-8");
			return readInStringFromReader(isr);
		} finally {
			try {
				if (isr != null) {
					isr.close();
				}
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * 
	 * @param reader
	 * @return
	 */
	private static String readInStringFromReader(Reader reader)
			throws IOException {
		BufferedReader br = null;
		if (reader instanceof BufferedReader) {
			br = (BufferedReader) reader;
		} else {
			br = new BufferedReader(reader);
		}

		StringBuffer data = new StringBuffer();
		try {
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				data.append(line);
				data.append(System.getProperty("line.separator"));
			}
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception ex) {
			}
		}
		return data.toString();
	}

	/**
	 * 
	 * @param resourceName
	 * @return
	 */
	public static byte[] readInFileAsBytesFromClasspath(String resourceName)
			throws IOException {

		InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(
				resourceName);
		if (is == null) {
			throw new NullPointerException("Couldn't find resource "
					+ resourceName + ".");
		}

		try {
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] temp = new byte[1024 * 1024];
			int bytesRead = bis.read(temp);
			byte[] bytes = new byte[bytesRead];
			System.arraycopy(temp, 0, bytes, 0, bytesRead);

			return bytes;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * 
	 * @param is
	 * @return
	 */
	public static byte[] readInBytesFromInputStream(InputStream is) {

		if (is == null) {
			throw new NullPointerException("InputStream cannot be null.");
		}

		return null;
	}

}
