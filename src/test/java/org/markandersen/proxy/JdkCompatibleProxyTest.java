package org.markandersen.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.markandersen.proxy.JdkCompatibleProxy;
import org.markandersen.test.BaseTestCase;
import org.markandersen.test.ExampleTestInterface;
import org.markandersen.test.ExampleTestInterfaceImpl;


/**
 * DOCUMENT ME!
 */
public class JdkCompatibleProxyTest extends BaseTestCase {

	/**
	 * 
	 */
	private InvocationHandler handler = new NoInvocationHandler();

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	public void testIsProxyClass() throws Exception {

		Class[] interfaces = new Class[] { ExampleTestInterface.class };
		ExampleTestInterface proxy = (ExampleTestInterface) JdkCompatibleProxy
				.newProxyInstance(this.getClass().getClassLoader(), null,
						interfaces, handler);
		assertTrue(JdkCompatibleProxy.isProxyClass(proxy.getClass()));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	public void testIsProxyClassConcreteClass() throws Exception {

		Class[] interfaces = new Class[] { ExampleTestInterface.class };
		ExampleTestInterfaceImpl proxy = (ExampleTestInterfaceImpl) JdkCompatibleProxy
				.newProxyInstance(this.getClass().getClassLoader(),
						ExampleTestInterfaceImpl.class, interfaces, handler);
		assertTrue(JdkCompatibleProxy.isProxyClass(proxy.getClass()));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	public void testIsProxyClassConcreteClassFails() throws Exception {

		assertFalse(JdkCompatibleProxy.isProxyClass(String.class));
	}

	/**
	 * 
	 */
	public void testNullHandler() {

		Class[] interfaces = new Class[] { ExampleTestInterface.class };

		try {
			JdkCompatibleProxy.newProxyInstance(this.getClass()
					.getClassLoader(), null, interfaces, null);
			fail("should have thrown null pointer exception.");
		} catch (NullPointerException e) {
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	public class NoInvocationHandler implements InvocationHandler {

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
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			return null;
		}
	}
}