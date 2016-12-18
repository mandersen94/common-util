// %1144179518541:%
package org.markandersen.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Proxy for classes.
 */
public class JdkCompatibleProxy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4661996996839071266L;

	/**
	 * 
	 */
	private static final HandlerAdapter nullInterceptor = new HandlerAdapter(
			null);

	/**
	 * 
	 */
	private static Map<Class<?>, Object> generatedClasses = Collections
			.synchronizedMap(new WeakHashMap<Class<?>, Object>());

	/**
	 * Creates a new JdkCompatibleProxy object.
	 */
	protected JdkCompatibleProxy() {
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param cl
	 * 
	 * @return
	 */
	public static boolean isProxyClass(Class<?> cl) {
		if (generatedClasses.containsKey(cl)) {
			return true;
		} else if (Proxy.isProxyClass(cl)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Creates a proxy. specify null for the parent argument if you are proxy
	 * interfaces.
	 * 
	 * @param loader
	 * @param parent
	 * @param interfaces
	 * @param h
	 * 
	 * @return
	 */
	public static Object newProxyInstance(ClassLoader loader, Class<?> parent,
			Class<?>[] interfaces, InvocationHandler h) {
		if (h == null) {
			throw new NullPointerException("InvocationHandler cannot be null.");
		}

		if (parent == null) {
			// just use the jdk proxy class.
			Object obj = Proxy.newProxyInstance(loader, interfaces, h);
			return obj;
		} else {
			// use the cglib libraries.
			Callback callback = new HandlerAdapter(h);
			Enhancer e = new Enhancer();
			e.setSuperclass(parent);
			e.setInterfaces(interfaces);
			e.setCallback(callback);
			Object obj = e.create();
			generatedClasses.put(obj.getClass(), null);
			return obj;
		}

	}

	/**
	 * DOCUMENT ME!
	 */
	private static class HandlerAdapter implements MethodInterceptor {

		/**
		 * 
		 */
		private InvocationHandler handler;

		/**
		 * Creates a new HandlerAdapter object.
		 * 
		 * @param handler
		 *            DOCUMENT ME!
		 */
		public HandlerAdapter(InvocationHandler handler) {
			this.handler = handler;
		}

		/**
		 * DOCUMENT ME!
		 * 
		 * @param obj
		 * @param method
		 * @param args
		 * @param methodProxy
		 * 
		 * @return
		 * 
		 * @throws Throwable
		 */
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy methodProxy) throws Throwable {
			return handler.invoke(obj, method, args);
		}
	}
}