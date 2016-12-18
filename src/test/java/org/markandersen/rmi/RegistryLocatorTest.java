package org.markandersen.rmi;

import java.rmi.registry.Registry;

import org.markandersen.rmi.RegistryLocator;
import org.markandersen.test.BaseTestCase;


public class RegistryLocatorTest extends BaseTestCase {

	/**
	 * 
	 * @throws Exception
	 */
	public void testGetRegistryPort() throws Exception {
		int port = 3343;
		Registry registry1 = RegistryLocator.getRegistry(port);
		Registry registry2 = RegistryLocator.getRegistry(port);
		assertEquals(registry1, registry2);
	}
}
