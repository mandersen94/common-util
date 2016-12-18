package org.markandersen.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.markandersen.io.IOUtils;


/**
 * 
 * 
 * @author mandersen
 */
public class ScopedProperties {

	private Properties props;

	private ScopedProperties nextProperties;

	/**
	 * 
	 * 
	 */
	public ScopedProperties() {
		props = new Properties();
	}

	/**
	 * 
	 * @param propertyFile
	 * @param mustExist
	 *            if true then the resource must exist or else a
	 *            NullPointerException is thrown.
	 * @throws IOException
	 */
	public ScopedProperties(String propertyFile, boolean mustExist)
			throws IOException {
		props = new Properties();
		URL url = this.getClass().getClassLoader().getResource(propertyFile);

		if ((url == null) && (!mustExist)) {
			return;
		}

		InputStream inputStream = null;
		try {
			inputStream = url.openStream();
			props.load(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

	}

	/**
	 * 
	 * @param propertyFile
	 * @throws IOException
	 */
	public ScopedProperties(String propertyFile) throws IOException {
		this(propertyFile, false);
	}

	/**
	 * 
	 * @param key
	 * @param properties
	 */
	public String getProperty(String key, AdvancedConfiguration properties) {

		String value = props.getProperty(key);
		if (value == null) {
			if (nextProperties == null) {
				return null;
			} else {
				// delegate to next properties object.
				return nextProperties.getProperty(key, properties);
			}
		} else {
			// now that we have a non-null value, see if we need to substitue.
			return PropertyUtil.substVars(value, properties);
		}
	}

	/**
	 * 
	 * @param is
	 * @throws IOException
	 */
	public void load(InputStream is) throws IOException {
		props.load(is);
	}

	public ScopedProperties getNextProperties() {
		return nextProperties;
	}

	public void setNextProperties(ScopedProperties nextProperties) {
		this.nextProperties = nextProperties;
	}

}
