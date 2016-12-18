package org.markandersen.service.invocation.ha.rmi;

import java.rmi.RemoteException;

import org.markandersen.service.invocation.InvocationResultHandler;


/**
 * 
 * @author mandersen
 */
public class RMIInvocationResultHandler implements InvocationResultHandler {

	/**
	 * 
	 */
	public boolean didInvocationSucceedException(Throwable ex) {

		if (ex instanceof RemoteException) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 */
	public boolean didInvocationSucceedReturnValue(Object result) {
		return true;
	}

}
