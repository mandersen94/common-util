package org.markandersen.config;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertyUtil {

	static String DELIM_START = "${";

	static char DELIM_STOP = '}';

	static int DELIM_START_LEN = 2;

	static int DELIM_STOP_LEN = 1;

	/**
	 * Find the value corresponding to <code>key</code> in <code>props</code>.
	 * Then perform variable substitution on the found value.
	 * 
	 */
	public static String findAndSubst(String key, Properties props) {
		String value = props.getProperty(key);
		if (value == null)
			return null;

		try {
			return substVars(value, props);
		} catch (IllegalArgumentException e) {
			System.err.println("Bad option value [" + value + "]. ex = " + e);
			return value;
		}
	}

	/**
	 * Perform variable substitution in string <code>val</code> from the
	 * values of keys found in the system propeties.
	 * 
	 * <p>
	 * The variable substitution delimeters are <b>${</b> and <b>}</b>.
	 * 
	 * <p>
	 * For example, if the System properties contains "key=value", then the call
	 * 
	 * <pre>
	 * String s = OptionConverter.substituteVars(&quot;Value of key is ${key}.&quot;);
	 * </pre>
	 * 
	 * will set the variable <code>s</code> to "Value of key is value.".
	 * 
	 * <p>
	 * If no value could be found for the specified key, then the
	 * <code>props</code> parameter is searched, if the value could not be
	 * found there, then substitution defaults to the empty string.
	 * 
	 * <p>
	 * For example, if system propeties contains no value for the key
	 * "inexistentKey", then the call
	 * 
	 * <pre>
	 * String s = OptionConverter
	 * 		.subsVars(&quot;Value of inexistentKey is [${inexistentKey}]&quot;);
	 * </pre>
	 * 
	 * will set <code>s</code> to "Value of inexistentKey is []"
	 * 
	 * <p>
	 * An {@link java.lang.IllegalArgumentException} is thrown if
	 * <code>val</code> contains a start delimeter "${" which is not balanced
	 * by a stop delimeter "}".
	 * </p>
	 * 
	 * @param val
	 *            The string on which variable substitution is performed.
	 * @throws IllegalArgumentException
	 *             if <code>val</code> is malformed.
	 * 
	 */
	public static String substVars(String val, Properties props)
			throws IllegalArgumentException {

		StringBuffer sbuf = new StringBuffer();

		int i = 0;
		int j, k;

		while (true) {
			j = val.indexOf(DELIM_START, i);
			if (j == -1) {
				// no more variables
				if (i == 0) { // this is a simple string
					return val;
				} else { // add the tail string which contails no variables
					// and return the result.
					sbuf.append(val.substring(i, val.length()));
					return sbuf.toString();
				}
			} else {
				sbuf.append(val.substring(i, j));
				k = val.indexOf(DELIM_STOP, j);
				if (k == -1) {
					throw new IllegalArgumentException(
							'"'
									+ val
									+ "\" has no closing brace. Opening brace at position "
									+ j + '.');
				} else {
					j += DELIM_START_LEN;
					String key = val.substring(j, k);
					// first try in System properties
					String replacement = System.getProperty(key, null);
					// then try props parameter
					if (replacement == null && props != null) {
						replacement = props.getProperty(key);
					}

					if (replacement != null) {
						// Do variable substitution on the replacement string
						// such that we can solve "Hello ${x2}" as "Hello p1"
						// the where the properties are
						// x1=p1
						// x2=${x1}
						String recursiveReplacement = substVars(replacement,
								props);
						sbuf.append(recursiveReplacement);
					}
					i = k + DELIM_STOP_LEN;
				}
			}
		}
	}

	/**
	 * 
	 * @param val
	 * @param props
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String substVars(String val, AdvancedConfiguration props)
			throws IllegalArgumentException {

		StringBuffer sbuf = new StringBuffer();

		int i = 0;
		int j, k;

		while (true) {
			j = val.indexOf(DELIM_START, i);
			if (j == -1) {
				// no more variables
				if (i == 0) { // this is a simple string
					return val;
				} else { // add the tail string which contails no variables
					// and return the result.
					sbuf.append(val.substring(i, val.length()));
					return sbuf.toString();
				}
			} else {
				sbuf.append(val.substring(i, j));
				k = val.indexOf(DELIM_STOP, j);
				if (k == -1) {
					throw new IllegalArgumentException(
							'"'
									+ val
									+ "\" has no closing brace. Opening brace at position "
									+ j + '.');
				} else {
					j += DELIM_START_LEN;
					String key = val.substring(j, k);
					// first try in System properties
					String replacement = System.getProperty(key, null);
					// then try props parameter
					if (replacement == null && props != null) {
						replacement = props.getProperty(key);
					}

					if (replacement != null) {
						// Do variable substitution on the replacement string
						// such that we can solve "Hello ${x2}" as "Hello p1"
						// the where the properties are
						// x1=p1
						// x2=${x1}
						String recursiveReplacement = substVars(replacement,
								props);
						sbuf.append(recursiveReplacement);
					}
					i = k + DELIM_STOP_LEN;
				}
			}
		}
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static Properties getPropertiesOffClasspath(String filename) {
		Properties props = new Properties();
		URL resource = PropertyUtil.class.getClassLoader()
				.getResource(filename);
		if (resource == null) {
			throw new RuntimeException(
					"Couldn't initialize ldapService because there was no "
							+ filename + " on the classpath.");
		}
		InputStream inputStream = null;
		try {
			inputStream = resource.openStream();
			props.load(inputStream);
		} catch (Exception ex) {
			throw new RuntimeException("Couldn't load configuration file "
					+ filename + ". exception = " + ex);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
		}
		return props;
	}

}
