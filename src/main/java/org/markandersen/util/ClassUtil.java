package org.markandersen.util;

public class ClassUtil {

	/**
	 * Strips off some of the package name to make it more readable.
	 * 
	 * @param className
	 * @return
	 */
	public static String shortenClassName(String className) {
		int length = System.getProperty("jmx.class.prefix", "com.andersen.")
				.length();
		return className.substring(length);
	}

	/**
	 * Does nothing right now. It might be useful to shorten the class names
	 * that are method parameters. Right now we just return the original string.
	 * 
	 * @param metricMethodName
	 * @return
	 */
	public static String shortenMethodName(String metricMethodName) {

		return metricMethodName;
	}

}
