package org.markandersen;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.markandersen.test.BaseTestCase;


public class DynamicProxyTest extends BaseTestCase {

	private long LOAD_ITERATIONS = 10 * 100000L; // 1 million iterations

	protected Class[] interfaces = new Class[] { Calculator.class };

	private Calculator proxiedObject;

	private Calculator realObject;

	protected void setUp() throws Exception {
		super.setUp();
		proxiedObject = (Calculator) Proxy.newProxyInstance(this.getClass()
				.getClassLoader(), interfaces, new SimpleInvocationHandler(
				new CalculatorImpl()));
		realObject = new CalculatorImpl();
		// warm up.
		doIterations(realObject, 10);
		doIterations(proxiedObject, 10);

	}

	/**
	 * 
	 * 
	 */
	public void testProxyObjectCost() {

		long startTime = System.currentTimeMillis();
		doIterations(realObject, LOAD_ITERATIONS);
		long timeElapsed = System.currentTimeMillis() - startTime;
		float methodCost = timeElapsed / (float) LOAD_ITERATIONS;
		System.out.println("Real object took " + timeElapsed + " ms.");
		System.out.println("Real object method cost " + methodCost + " ms.");
		System.out.println("Real object method cost " + (methodCost / 1000)
				+ " seconds.");

		// now do the proxy
		startTime = System.currentTimeMillis();
		doIterations(proxiedObject, LOAD_ITERATIONS);
		timeElapsed = System.currentTimeMillis() - startTime;
		methodCost = timeElapsed / (float) LOAD_ITERATIONS;
		System.out.println("Proxy object took " + timeElapsed + " ms.");
		System.out.println("Proxy object method cost " + methodCost + " ms.");
		System.out.println("Proxy object method cost " + (methodCost / 1000)
				+ " seconds.");
	}

	/**
	 * 
	 * @param calculator
	 */
	private void doIterations(Calculator calculator, long iterations) {
		for (int i = 0; i < iterations; i++) {
			int result = calculator.add(4, 5);
		}
	}

	/**
	 * Invocation handler for proxy.
	 * 
	 * @author e63582
	 */
	static class SimpleInvocationHandler implements InvocationHandler {

		private Object target;

		public SimpleInvocationHandler(Calculator target) {
			this.target = target;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			return method.invoke(target, args);
		}

	}

}
