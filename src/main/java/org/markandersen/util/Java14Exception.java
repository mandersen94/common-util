package org.markandersen.util;

/**
 * Provides java 1.4 style exceptions.
 * 
 * @author mandersen
 */
public class Java14Exception extends Exception {

	private Throwable cause;

	public Java14Exception() {
		super();
	}

	public Java14Exception(String s) {
		super(s);
	}

	public Java14Exception(Throwable cause) {
		super();
		this.cause = cause;
	}

	public Java14Exception(String s, Throwable cause) {
		super(s);
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

}
