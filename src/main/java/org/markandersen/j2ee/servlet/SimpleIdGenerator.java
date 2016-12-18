package org.markandersen.j2ee.servlet;

/**
 * 
 * @author mark@markandersen.org
 */
public class SimpleIdGenerator implements IdGenerator {

	private int counter = 1001;
	
	/**
	 * 
	 */
	public synchronized String getNextId() {
		
		return Integer.toString(counter++);
	}

}
