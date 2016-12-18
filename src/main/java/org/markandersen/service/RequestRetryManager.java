package org.markandersen.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * DOCUMENT ME!
 */
public class RequestRetryManager implements InvocationHandler {

	/**
	 * 
	 */
	protected Object target;

	/**
	 * 
	 */
	protected RequestRetryCallback callback;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param classloader
	 * @param interfaces
	 * @param target
	 * @param callback
	 * 
	 * @return
	 */
	public static Object getRequestRetryManager(ClassLoader classloader,
			Class[] interfaces, Object target, RequestRetryCallback callback) {

		RequestRetryManager handler = new RequestRetryManager();
		handler.target = target;
		handler.callback = callback;

		return Proxy.newProxyInstance(classloader, interfaces, handler);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 * 
	 * @throws Throwable
	 */
	public Object invoke(final Object proxy, final Method method,
			final Object[] args) throws Throwable {

		Object result = null;

		do {

			try {
				result = method.invoke(target, args);
			} catch (InvocationTargetException ex) {
				result = ex.getCause();
			}
		} while (callback.shouldRetryOperation(result, proxy, method, args));

		if (result instanceof Throwable) {
			throw (Throwable) result;
		} else {
			return result;
		}
	}
}