package org.markandersen.service.timeout;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.markandersen.proxy.JdkCompatibleProxy;


import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Future;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.TimeoutException;

/**
 * DOCUMENT ME!
 */
public class RequestTimeoutManager implements InvocationHandler {

	/**
	 * 
	 */
	protected ExecutorService executorService;

	/**
	 * 
	 */
	protected Object target;

	/**
	 * 
	 */
	protected RequestTimeoutHelper helper;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param classloader
	 * @param interfaces
	 * @param target
	 * @param executorService
	 * @param helper
	 * 
	 * @return
	 */
	public static Object getRequestTimeoutManager(ClassLoader classloader,
			Class<?> parent, Class[] interfaces, Object target,
			ExecutorService executorService, RequestTimeoutHelper helper) {

		RequestTimeoutManager handler = new RequestTimeoutManager();
		handler.executorService = executorService;
		handler.target = target;
		handler.helper = helper;

		if (handler.helper == null) {
			handler.helper = new DefaultRequestTimeoutHelper();
		}
		return JdkCompatibleProxy.newProxyInstance(classloader, parent,
				interfaces, handler);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param interfaces
	 * @param target
	 * @param executorService
	 * @param helper
	 * 
	 * @return
	 */
	public static Object getRequestTimeoutManager(Class<?> parentClass,
			Class[] interfaces, Object target, ExecutorService executorService,
			RequestTimeoutHelper helper) {
		return getRequestTimeoutManager(target.getClass().getClassLoader(),
				parentClass, interfaces, target, executorService, helper);
	}

	/**
	 * Invocation Handler
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return object
	 */
	public Object invoke(final Object proxy, final Method method,
			final Object[] args) throws Throwable {

		// callable to submit to executor.
		Callable callable = new Callable() {
			public Object call() throws Exception {
				return method.invoke(target, args);
			}
		};

		long maxWaitTime = helper.getTimeToWait(proxy, method, args);
		TimeUnit waitTimeUnit = helper.getTimeUnit(proxy, method, args);
		boolean shouldInterruptOnCancel = helper.getShouldInterruptOnCancel(
				proxy, method, args);
		boolean shouldOverrideException = helper.getShouldOverrideException(
				proxy, method, args);
		Future future = executorService.submit(callable);

		try {
			Object result = future.get(maxWaitTime, waitTimeUnit);
			return result;
		} catch (TimeoutException ex) {
			future.cancel(shouldInterruptOnCancel);
			Object obj = handleException(shouldOverrideException, ex, proxy,
					method, args);
			if (obj instanceof Throwable) {
				throw (Throwable) obj;
			} else {
				return obj;
			}
		} catch (ExecutionException ex) {

			// this block is for any exception that was thrown by the method.
			Throwable cause = ex.getCause();

			if (cause instanceof InvocationTargetException) {

				// since we call method.invoke(), the real exception is wrapped
				// in an InvocationTargetException
				InvocationTargetException invocationException = (InvocationTargetException) cause;
				Throwable targetException = invocationException
						.getTargetException();
				throw targetException;
			} else {
				// we should never get here.
				System.err
						.println("Unknown exception returned.  Expected InvocationTargetException.");
				throw cause;
			}
		} catch (Exception ex) {
			// unknown exception thrown.
			System.err.println("Unknown exception thrown." + ex);
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param shouldOverrideException
	 * @param ex
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	private Object handleException(boolean shouldOverrideException,
			Exception ex, Object proxy, Method method, Object[] args) {

		if (shouldOverrideException) {
			return helper.overrideException(ex, proxy, method, args);
		} else {
			return new RequestTimeoutException(ex.getMessage(), ex.getCause());
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	static class DefaultRequestTimeoutHelper implements RequestTimeoutHelper {

		/**
		 * DOCUMENT ME!
		 * 
		 * @param proxy
		 * @param method
		 * @param args
		 * 
		 * @return
		 */
		public long getTimeToWait(Object proxy, Method method, Object[] args) {
			return 60 * 1000;
		}

		/**
		 * DOCUMENT ME!
		 * 
		 * @param proxy
		 * @param method
		 * @param args
		 * 
		 * @return
		 */
		public TimeUnit getTimeUnit(Object proxy, Method method, Object[] args) {
			return TimeUnit.MILLISECONDS;
		}

		/**
		 * DOCUMENT ME!
		 * 
		 * @param proxy
		 * @param method
		 * @param args
		 * 
		 * @return
		 */
		public boolean getShouldInterruptOnCancel(Object proxy, Method method,
				Object[] args) {
			return false;
		}

		/**
		 * DOCUMENT ME!
		 * 
		 * @param proxy
		 * @param method
		 * @param args
		 * 
		 * @return
		 */
		public boolean getShouldOverrideException(Object proxy, Method method,
				Object[] args) {
			return false;
		}

		/**
		 * DOCUMENT ME!
		 * 
		 * @param ex
		 * @param proxy
		 * @param method
		 * @param args
		 * 
		 * @return
		 */
		public Object overrideException(Exception ex, Object proxy,
				Method method, Object[] args) {
			return null;
		}
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

}