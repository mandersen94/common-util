package org.markandersen.config;

import java.io.IOException;

/**
 * 
 * @author mandersen
 */
public class AdvancedConfiguration implements Configuration {

	private ScopedProperties props;

	/**
	 * 
	 * @param simplePropertyFile
	 * @throws IOException
	 */
	public AdvancedConfiguration(String propertyFile) throws IOException {

		props = new ScopedProperties(propertyFile);
	}

	/**
	 * 
	 * @param propertyFiles
	 * @throws IOException
	 */
	public AdvancedConfiguration(String[] propertyFiles) throws IOException {

		if ((propertyFiles == null) || (propertyFiles.length == 0)) {
			props = new ScopedProperties();
			return;
		}

		if (propertyFiles.length == 1) {
			props = new ScopedProperties(propertyFiles[0]);
			return;
		}

		ScopedProperties lastProperties = null;
		for (int i = 0; i < propertyFiles.length; i++) {

			ScopedProperties temp = new ScopedProperties(propertyFiles[i]);

			if (i == 0) {
				// first iteration.
				props = temp;
				lastProperties = temp;
				continue;
			}
			lastProperties.setNextProperties(temp);
			lastProperties = temp;

		}
	}

	/**
	 * 
	 * 
	 * @see org.markandersen.config.Configuration#getProperty(java.lang.String)
	 */
	public String getProperty(String key) {

		return props.getProperty(key, this);

	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String key, String defaultValue) {
		String value = getProperty(key);
		return (value != null ? value : defaultValue);
	}

	/**
	 * 
	 * 
	 * @see org.markandersen.config.Configuration#getProperty(java.lang.String)
	 */
	public int getPropertyInt(String key) {

		return Integer.parseInt(props.getProperty(key, this));

	}

	/**
	 * 
	 * 
	 * @see org.markandersen.config.Configuration#getProperty(java.lang.String)
	 */
	public int getPropertyInt(String key, int defaultValue) {

		String value = props.getProperty(key, this);
		if (value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(props.getProperty(key, this));
		}

	}

}
