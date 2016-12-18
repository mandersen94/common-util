package org.markandersen.test;

import java.io.Serializable;

/**
 * DOCUMENT ME!
 */
public class SimpleOperationImpl implements SimpleOperation, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3261240655741976299L;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param arg1
	 * @param arg2
	 * 
	 * @return
	 */
	public String concatString(String arg1, String arg2) {
		System.out.println("objectid = " + hashCode()
				+ " : concatString called");
		return arg1 + arg2;
	}
}