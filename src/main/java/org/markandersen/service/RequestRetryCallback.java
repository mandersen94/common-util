package org.markandersen.service;

import java.lang.reflect.Method;

/**
 * 
 */
public interface RequestRetryCallback {

	/**
	 * DOCUMENT ME!
	 * 
	 * @param result
	 * 
	 * @return
	 */
	public Throwable getExhaustedRetriesResult(Object result);

	/**
	 * DOCUMENT ME!
	 * 
	 * @param result
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	public boolean shouldRetryOperation(Object result, Object proxy,
			Method method, Object[] args);
}