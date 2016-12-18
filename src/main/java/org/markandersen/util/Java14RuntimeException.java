package org.markandersen.util;

/**
 * Provides java 1.4 style exceptions.
 * 
 * @author mandersen
 */
public class Java14RuntimeException extends RuntimeException {

	private Throwable cause;

	public Java14RuntimeException() {
		super();
	}

	public Java14RuntimeException(String s) {
		super(s);
	}

	public Java14RuntimeException(Throwable cause) {
		super();
		this.cause = cause;
	}

	public Java14RuntimeException(String s, Throwable cause) {
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
