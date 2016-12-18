package org.markandersen.security;

import java.security.SecureRandom;

import javax.net.ssl.SSLContext;

public class SecurityUtil {

	private static final String DEFAULT_SECURE_RANDOM_ALGO = "SSLv3";

	/**
	 * Initializes the SecureRandom now rather than doing it lazily later.
	 */
	public static void initializeSSLContext() {

		try {
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextInt();
			SSLContext instance = SSLContext
					.getInstance(DEFAULT_SECURE_RANDOM_ALGO);
			instance.init(null, null, secureRandom);

		} catch (Exception ex) {
			System.out.println("problems initializing SSLContext.");
			ex.printStackTrace();
		}
	}
}
