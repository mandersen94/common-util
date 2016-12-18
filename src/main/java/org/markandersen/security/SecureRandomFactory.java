package org.markandersen.security;

import java.security.SecureRandom;

public class SecureRandomFactory {

	public static final String TEST_FAST_RANDOM = "testing.use.FastRandom";

	/**
	 * 
	 */
	public static SecureRandom getSecureRandom() {
		if ("true".equalsIgnoreCase(System.getProperty(TEST_FAST_RANDOM))) {
			// this is for unittesting purposes to make it faster. NOT
			// SECURE.
			System.out
					.println("WARNING: using unsecure FastRandom.  only using for testing.");
			System.err
					.println("WARNING: using unsecure FastRandom.  only using for testing.");

			return new FastRandom();
		} else {
			return new SecureRandom();
		}

	}
}
