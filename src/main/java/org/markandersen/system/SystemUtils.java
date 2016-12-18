package org.markandersen.system;

import java.net.URL;
import java.util.StringTokenizer;

public class SystemUtils {

	/**
	 * 
	 */
	public static String[] getClasspathPaths() {

		final String PATH_SEPARATOR = System.getProperty("path.separator");

		String classpath = System.getProperty("java.class.path");

		StringTokenizer tokenizer = new StringTokenizer(classpath,
				PATH_SEPARATOR);
		String[] paths = new String[tokenizer.countTokens()];
		for (int i = 0; tokenizer.hasMoreTokens(); i++) {
			String singleEntry = tokenizer.nextToken();
			paths[i] = singleEntry;
		}
		return paths;

	}

	/**
	 * 
	 */
	public static URL[] getURLs() {

		final String PATH_SEPARATOR = System.getProperty("path.separator");

		String classpath = System.getProperty("java.class.path");
		String currentDir = System.getProperty("user.dir");

		System.out.println("classpath = " + classpath);
		System.out.println("current dir = " + currentDir);

		StringTokenizer tokenizer = new StringTokenizer(classpath,
				PATH_SEPARATOR);
		URL[] urls = new URL[tokenizer.countTokens()];
		for (int i = 0; tokenizer.hasMoreTokens(); i++) {
			String singleEntry = tokenizer.nextToken();
			// System.out.println("entry = " + singleEntry);
			String fullURL = "file:///" + singleEntry;
			if (!((fullURL.endsWith(".jar")) || (fullURL.endsWith("/")) || (fullURL
					.endsWith(".rar")))) {
				fullURL = fullURL + "/";
			}

			try {
				urls[i] = new URL(fullURL);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex.toString());
			}
		}
		return urls;

	}
}
