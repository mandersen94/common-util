package org.markandersen.test;

import java.util.Date;

import junit.framework.TestCase;

/**
 * 
 */
public abstract class BaseTestCase extends TestCase {

	private long startTime;

	/**
	 * 
	 */
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("/********************************************/");
		System.out.println("Starting test: " + getName());
		System.out.println("Date = " + new Date());
		System.out.println("/********************************************/");
		System.out.flush();
		startTime = System.currentTimeMillis();
	}

	/**
	 * 
	 */
	protected void tearDown() throws Exception {
		System.out.println("/********************************************/");
		System.out.println("Ending test: " + getName());
		System.out.println("Time elapsed = "
				+ (System.currentTimeMillis() - startTime) + " ms.");
		System.out.println("/********************************************/");
		System.out.flush();
		super.tearDown();
	}
}
