package org.markandersen.service.invocation.ha;

import java.lang.reflect.InvocationTargetException;

import org.markandersen.invocation.Invocation;
import org.markandersen.invocation.Invoker;
import org.markandersen.service.invocation.InvocationResultHandler;
import org.markandersen.service.invocation.ha.rmi.RMIInvocationResultHandler;


/**
 * Does the method invocation and then checks to see if the request should be
 * retried or not.
 * 
 * @author mandersen
 */
public class SimpleHAInvoker implements Invoker {

	private InvocationResultHandler resultHandler;

	public SimpleHAInvoker(RMIInvocationResultHandler handler) {
		resultHandler = handler;
	}

	/**
	 * 
	 */
	public Object invoke(Invocation invocation) throws Throwable {
		Object obj = invocation.getValue(HAConstants.TARGET_OBJECT_KEY);

		try {

			// this executes the actual method on the real object. outcomes are
			// a result or a InvocationTargetException being thrown.
			Object result = invocation.performCall(obj);

			// check of the result was a "good" result. use the result handler
			// to determine what results are bad results and to retry.
			if (resultHandler.didInvocationSucceedReturnValue(result)) {
				// invocation was successful.
				return result;
			} else {
				throw new RequestFailedException(result);
			}
		} catch (IllegalAccessException e) {
			// not a normal exception. shouldn't happen.
			e.printStackTrace();
			RuntimeException ex = new RuntimeException(e);
			throw ex;
		} catch (IllegalArgumentException e) {
			// not a normal exception. shouldn't happen.
			e.printStackTrace();
			RuntimeException ex = new RuntimeException(e);
			throw ex;
		} catch (InvocationTargetException e) {
			// this is the catch block when an regular exception is thrown
			Throwable ex = e.getCause();

			// this should be an application exception of sorts.
			if (resultHandler.didInvocationSucceedException(ex)) {
				// rethrow and get out of while(true)
				throw ex;
			} else {
				throw new RequestFailedException(ex);
			}
		}
	}

}
