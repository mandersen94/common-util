package org.markandersen.service.invocation.ha;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.markandersen.service.invocation.InvocationResultHandler;
import org.markandersen.service.invocation.InvocationStrategyInstance;
import org.markandersen.service.invocation.InvocationStrategyInstanceFactory;
import org.markandersen.service.invocation.NoObjectsAvailableException;
import org.springframework.aop.framework.ProxyFactory;


/**
 * Invoker framework that provides invocation poilicies on invoking objects.
 * 
 */
public class HAProxy<T> implements InvocationHandler {

	/**
	 * 
	 */
	private Class[] proxyInterfaces;

	/**
	 * parent class if proxying a class and not an interface.
	 */
	private Class<?> parentClass;

	/**
	 * 
	 */
	private ClassLoader classLoader;

	/**
	 * 
	 */
	private InvocationStrategyInstanceFactory<T> invocationStrategyFactory;

	/**
	 * 
	 */
	private InvocationResultHandler resultHandler;

	/**
	 * 
	 * 
	 */
	public HAProxy() {

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param invocationStrategyFactory
	 * @param handler
	 * @param effectiveClasses
	 * @param classLoader
	 */
	public HAProxy(
			InvocationStrategyInstanceFactory<T> invocationStrategyFactory,
			InvocationResultHandler handler, Class<?>[] effectiveClasses,
			ClassLoader classLoader) {

		this.resultHandler = handler;
		this.invocationStrategyFactory = invocationStrategyFactory;
		this.classLoader = classLoader;
		this.proxyInterfaces = effectiveClasses;
	}

	/**
	 * 
	 * 
	 */
	public void init() {

		if (resultHandler == null) {
			throw new NullPointerException(
					"InvocationResultHandler cannot be null.");
		}

		List<Class<?>> effectiveClassList = new ArrayList<Class<?>>();
		for (int i = 0; i < proxyInterfaces.length; i++) {
			Class<?> clazz = proxyInterfaces[i];
			if (!clazz.isInterface()) {
				throw new IllegalArgumentException(
						"proxy interface list contains a class. class = "
								+ clazz);
			} else {
				effectiveClassList.add(clazz);
			}
		}
		this.proxyInterfaces = effectiveClassList
				.toArray(new Class[effectiveClassList.size()]);

		if (classLoader == null) {
			// if no classloader chosen, just choose the first one.
			classLoader = proxyInterfaces[0].getClass().getClassLoader();
		}
	}

	/**
	 * Creates an proxy
	 * 
	 * @return invocation manager.
	 */
	public Object createInvocationManager() {
		init();

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setInterfaces(proxyInterfaces);
		proxyFactory.setFrozen(false);
		proxyFactory.setTarget(this);
		MethodInterceptor haViewInterceptor = new HAViewInterceptor();
		proxyFactory.addAdvice(haViewInterceptor);
		return proxyFactory.getProxy();
	}

	/**
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 * 
	 * @throws Throwable
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		InvocationStrategyInstance<T> strategy = invocationStrategyFactory
				.getInvocationStrategyInstance();
		Throwable firstBadException = null;

		// we will either return a result here or throw an exception to get out
		// of the
		// loop.
		while (true) {

			T obj = strategy.getNextInvocationObject();

			if (obj == null) {
				// out of real objects to invoke on.
				// throw a runtime exception.
				throw new NoObjectsAvailableException(firstBadException);
			}

			try {
				checkTesting();

				Object result = method.invoke(obj, args);

				if (resultHandler.didInvocationSucceedReturnValue(result)) {
					// invocation was successful.
					return result;
				} else {
					// add it to the invocation history and try again.
					strategy.addBadInvocation(obj);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();

				RuntimeException ex = new RuntimeException(e);
				throw ex;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw e;
			} catch (InvocationTargetException e) {

				Throwable ex = e.getCause();

				// this should be an application exception of sorts.
				if (resultHandler.didInvocationSucceedException(ex)) {
					// rethrow and get out of while(true)
					throw ex;
				} else {

					// try again. save the exception if this is our
					// first time around.
					if (firstBadException == null) {
						firstBadException = ex;
					}
					// let the strategy know that the object was "bad".
					strategy.addBadInvocation(obj);
				}
			}
		}
	}

	/**
	 * this will only do something if system property
	 * testing.clientInvocation.exception is true.
	 * 
	 * @throws Throwable
	 */
	private void checkTesting() throws Throwable {

		if ("true".equalsIgnoreCase(System.getProperty(
				"testing.clientInvocation.exception", "false"))) {

			Object object = System.getProperties().get(
					"testing.clientInvocation.exception.object");
			Throwable throwable = (Throwable) object;
			throw throwable;
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public InvocationResultHandler getResultHandler() {
		return resultHandler;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param resultHandler
	 */
	public void setResultHandler(InvocationResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public Class[] getProxyInterfaces() {
		return proxyInterfaces;
	}

	public void setProxyInterfaces(Class[] effectiveClasses) {
		this.proxyInterfaces = effectiveClasses;
	}

	public InvocationStrategyInstanceFactory<T> getInvocationStrategyFactory() {
		return invocationStrategyFactory;
	}

	public void setInvocationStrategyFactory(
			InvocationStrategyInstanceFactory<T> invocationStrategyFactory) {
		this.invocationStrategyFactory = invocationStrategyFactory;
	}

	public Class<?> getParentClass() {
		return parentClass;
	}

	public void setParentClass(Class<?> parentClass) {
		this.parentClass = parentClass;
	}

}