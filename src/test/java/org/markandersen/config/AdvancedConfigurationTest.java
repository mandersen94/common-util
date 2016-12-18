package org.markandersen.config;

import java.io.IOException;

import org.markandersen.config.AdvancedConfiguration;
import org.markandersen.config.Configuration;
import org.markandersen.test.BaseTestCase;


/**
 * 
 * @author Mark Andersen
 */
public class AdvancedConfigurationTest extends BaseTestCase {

	private String goodKey = "testKey1";

	private String goodValue = "testValue1";

	private String goodScopedKey = "testScopedKey1";

	private String goodScopedValue = "testScopedValue1";

	private String badKey = "badKey";

	private String[] propertyFiles = new String[] {
			"org/markandersen/config/test_missing.properties",
			"org/markandersen/config/test1.properties",
			"org/markandersen/config/test2.properties" };

	private String simplePropertyFile = "org/markandersen/config/test1.properties";

	private String goodVariableKey = "testReplacedValue";

	private String goodVariableValue = "mytestVariableExtended";

	/**
	 * @throws Exception
	 */
	public void testAdvancedConfigurationSimpleProperty() throws Exception {

		Configuration config = new AdvancedConfiguration(simplePropertyFile);

		assertEquals("value is not equal.", goodValue, config
				.getProperty(goodKey));
		assertNull("shouldn't have found a value.", config.getProperty(badKey));
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testAdvancedConfigurationScopedProperty() throws Exception {

		Configuration config = new AdvancedConfiguration(propertyFiles);
		assertEquals(goodValue, config.getProperty(goodKey));
	}

	/**
	 * @throws IOException
	 * 
	 * 
	 */
	public void testAdvancedPropertiesVariableReplace() throws IOException {

		Configuration config = new AdvancedConfiguration(propertyFiles);
		assertEquals(goodVariableValue, config.getProperty(goodVariableKey));

	}
}
