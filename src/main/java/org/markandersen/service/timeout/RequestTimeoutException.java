package org.markandersen.service.timeout;

/**
 * DOCUMENT ME!
 */
public class RequestTimeoutException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 446585152768714338L;

	/**
	 * Creates a new RequestTimeoutException object.
	 */
	public RequestTimeoutException() {
		super();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param message
	 */
	public RequestTimeoutException(String message) {
		super(message);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param throwable
	 */
	public RequestTimeoutException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param message
	 * @param throwable
	 */
	public RequestTimeoutException(String message, Throwable throwable) {
		super(message, throwable);
	}
}