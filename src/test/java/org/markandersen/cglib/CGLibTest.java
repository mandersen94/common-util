package org.markandersen.cglib;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.markandersen.proxy.JdkCompatibleProxy;
import org.markandersen.test.ExampleClass;

import junit.framework.TestCase;


/**
 * Marks comment
 */
public class CGLibTest extends TestCase {

	/**
	 * 
	 */
	private int iterations = 100;

	/**
	 * 
	 */
	public void testCGLibProxyWithClasses() {

		ExampleClass object1 = new ExampleClass(1);
		ExampleClass object2 = new ExampleClass(2);
		ExampleClass object3 = new ExampleClass(3);
		InvocationHandler policyHandler = new TestHandler(new Object[] {
				object1, object2, object3 });
		ExampleClass proxyObject = (ExampleClass) JdkCompatibleProxy
				.newProxyInstance(ExampleClass.class.getClassLoader(),
						ExampleClass.class, new Class[0], policyHandler);

		for (int i = 0; i < iterations; i++) {
			proxyObject.add(3, 4);
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	static class TestHandler implements InvocationHandler {

		/**
		 * 
		 */
		Object[] objects;

		/**
		 * 
		 */
		int index = 0;

		/**
		 * Creates a new TestHandler object.
		 * 
		 * @param objects .
		 */
		public TestHandler(Object[] objects) {
			this.objects = objects;
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
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			int tempIndex = ++index % objects.length;
			System.out.println("Temp index = " + tempIndex);

			Object effectiveObject = objects[tempIndex];
			return method.invoke(effectiveObject, args);
		}
	}
}