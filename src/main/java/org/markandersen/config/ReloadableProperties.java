package org.markandersen.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Properties object that can reload its backing file.
 * 
 * @author e63582
 */
public class ReloadableProperties {

	private Properties properties;
	private File file;
	private int scanInterval;
	private long lastReloadAt = 1L;
	private long lastScanAt = 1L;

	/**
	 * 
	 * @param file
	 * @param scanInterval
	 */
	public ReloadableProperties(File file, int scanInterval) {
		this.file = file;
		this.scanInterval = scanInterval;
		reloadPropertiesIfNecessary();
	}

	/**
	 * Scans the file for updates on the "scan interval".
	 * 
	 */
	private synchronized void reloadPropertiesIfNecessary() {
		long nextScanAt = lastScanAt + scanInterval;
		if (nextScanAt < System.currentTimeMillis()) {
			// see if the file has been updated.
			long lastModified = file.lastModified();
			if (lastReloadAt != lastModified) {
				// file has been updated. reload.
				reloadProperties();
			} else {
				// file not updated. do nothing.
			}
		} else {
			// do nothing
		}
	}

	/**
	 * Reload the properties object.
	 */
	private void reloadProperties() {
		try {
			FileInputStream fis = new FileInputStream(file);
			Properties newProperties = new Properties();
			newProperties.load(fis);
			properties = newProperties;
			lastReloadAt = System.currentTimeMillis();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {

		}
	}

	/**
	 * 
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("ReloadableProperties: file = ").append(file.getName())
				.append(", reloadInterval = ").append(scanInterval);
		buf.append(". properties = ").append(properties);
		return buf.toString();
	}

	/**
	 * 
	 * @param resource
	 * @param scanInterval
	 * @return
	 */
	public static ReloadableProperties getClasspathInstance(String resource,
			int scanInterval) {

		URL url = ReloadableProperties.class.getClassLoader().getResource(
				resource);
		if (url == null) {
			throw new NullPointerException("Couldn't find resource " + resource
					+ " on classpath.");
		}

		String fileString = url.getFile();
		File file = new File(fileString);
		return getFileInstance(file, scanInterval);
	}

	/**
	 * 
	 * @param resource
	 * @param scanInterval
	 * @return
	 */
	public static ReloadableProperties getFileInstance(File file,
			int scanInterval) {

		if (!file.exists()) {
			// bad.
			throw new NullPointerException("file doesn't exist. file = " + file);
		} else {
			return new ReloadableProperties(file, scanInterval);
		}

	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		reloadPropertiesIfNecessary();
		return properties.getProperty(key);
	}

}
