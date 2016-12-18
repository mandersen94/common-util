package org.markandersen.service.invocation.corba;

import org.markandersen.service.invocation.InvocationResultHandler;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.TRANSIENT;


/**
 * Invocation result handler that says invocations didn't succeed if there was a
 * CORBA exception indicating the object isn't up or not responing. includes
 * TRANSIENT, TIMEOUT, and COMM_FAILURE right now.
 */
public class CORBAInvocationResultHandler implements InvocationResultHandler {

	/**
	 * Creates a new CORBAInvocationResultHandler object.
	 */
	public CORBAInvocationResultHandler() {
	}

	/**
	 * if a corba transient exception was thrown, assume the server was down.
	 * 
	 * @param ex
	 * 
	 * @return
	 */
	public boolean didInvocationSucceedException(Throwable ex) {

		String className = ex.getClass().getName();
		String java15TimeoutExceptionClassName = "org.omg.CORBA.TIMEOUT";
		if (ex instanceof TRANSIENT) {
			return false;
		} else if (ex instanceof COMM_FAILURE) {
			return false;
		} else if (className.equals(java15TimeoutExceptionClassName)) {
			// this execption is jav 15 specific.
			return false;
		} else {
			return true;
		}

	}

	/**
	 * @see InvocationResultHandler#didInvocationSucceedReturnValue(Object)
	 */
	public boolean didInvocationSucceedReturnValue(Object result) {
		// if a result was returned, we succeeded.
		return true;
	}
}