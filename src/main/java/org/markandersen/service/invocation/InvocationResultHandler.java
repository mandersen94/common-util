// %1144179518416:%
package org.markandersen.service.invocation;

/**
 * Used to determine if a particular return value or exception indicates an
 * error and to retry the invocation with another object or if the value is
 * "expected".
 */
public interface InvocationResultHandler {

	/**
	 * Returns true if the result is okay and not to retry the request.
	 * 
	 * @param result
	 * 
	 * @return
	 */
	boolean didInvocationSucceedReturnValue(Object result);

	/**
	 * Returns true if the exception is okay and to not retry the request.
	 * 
	 * @param ex
	 * 
	 * @return
	 */
	boolean didInvocationSucceedException(Throwable ex);
}