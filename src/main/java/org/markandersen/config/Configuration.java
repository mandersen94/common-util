package org.markandersen.config;

/**
 * 
 * @author Mark Andersen
 */
public interface Configuration {

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key);

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String key, String defaultValue);

	/**
	 * Gets the property value and converts it to an int.
	 * 
	 * @param key
	 * @return
	 */
	public int getPropertyInt(String key);

	/**
	 * Gets the property value and converts it to an int. If no property is
	 * found for the given key, use the default value.
	 * 
	 * @param key
	 * @param defaultValue
	 *            to return if the given key isn't found.
	 * @return
	 */
	public int getPropertyInt(String key, int defaultValue);

}
