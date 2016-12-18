package org.markandersen.service.invocation;

/**
 * Indicates that there are no more good objects available to invoke on.
 */
public class NoObjectsAvailableException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9152273594118343639L;

	/**
	 * Creates a new NoObjectsAvailableException object.
	 * 
	 * @param firstBadException
	 */
	public NoObjectsAvailableException(Throwable firstBadException) {
		super(firstBadException);
	}
}