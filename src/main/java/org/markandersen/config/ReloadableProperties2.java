package org.markandersen.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.markandersen.io.IOUtils;
import org.markandersen.io.file.FileInterface;

/**
 * Properties object that can reload its backing file.
 * 
 * @author e63582
 */
public class ReloadableProperties2 {

	private Properties properties;
	private FileInterface file;
	private int scanInterval;
	private long lastReloadAt = 1L;
	private long lastScanAt = 1L;

	/**
	 * 
	 * @param file
	 * @param scanInterval
	 */
	public ReloadableProperties2(FileInterface file, int scanInterval) {
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
		InputStream is = null;
		try {

			is = file.getInputStream();
			Properties newProperties = new Properties();
			newProperties.load(is);
			properties = newProperties;
			lastReloadAt = System.currentTimeMillis();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * 
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("ReloadableProperties2: file = ").append(file.getName())
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
	public static ReloadableProperties2 getClasspathInstance(String resource,
			int scanInterval) {

		URL url = ReloadableProperties2.class.getClassLoader().getResource(
				resource);
		if (url == null) {
			throw new NullPointerException("Couldn't find resource " + resource
					+ " on classpath.");
		}

		String fileString = url.getFile();
		FileInterface file = new FileInterfaceImpl(fileString);
		return getFileInstance(file, scanInterval);
	}

	/**
	 * 
	 * @param resource
	 * @param scanInterval
	 * @return
	 */
	public static ReloadableProperties2 getFileInstance(FileInterface file,
			int scanInterval) {

		if (!file.exists()) {
			// bad.
			throw new NullPointerException("file doesn't exist. file = " + file.getName());
		} else {
			return new ReloadableProperties2(file, scanInterval);
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
