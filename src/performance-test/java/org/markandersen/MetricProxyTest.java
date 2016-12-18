package org.markandersen;

import org.markandersen.metric.MetricProxy;
import org.markandersen.test.BaseTestCase;


public class MetricProxyTest extends BaseTestCase {

	private long LOAD_ITERATIONS = 1 * 100000L; // 100 thousand iterations

	protected Class[] interfaces = new Class[] { Calculator.class };

	protected Calculator target = new CalculatorImpl();

	/**
	 * 
	 */
	public void testMetricRecord() {
		Calculator proxiedObject = (Calculator) MetricProxy.wrapInterface(this
				.getClass().getClassLoader(), interfaces, target);
		int result = proxiedObject.add(4, 5);
	}

	/**
	 * 
	 * 
	 */
	public void testMetricRecordWithoutProxy() {
		Calculator realObject = new CalculatorImpl();
		long startTime = System.currentTimeMillis();
		doIterations(realObject);
		long timeElapsed = System.currentTimeMillis() - startTime;
		System.out.println("Real object took " + timeElapsed + " ms.");
	}

	/**
	 * 
	 * 
	 */
	public void testMetricRecordWithProxy() {
		Calculator proxiedObject = (Calculator) MetricProxy.wrapInterface(this
				.getClass().getClassLoader(), interfaces, target);
		long startTime = System.currentTimeMillis();
		doIterations(proxiedObject);
		long timeElapsed = System.currentTimeMillis() - startTime;
		System.out.println("Real object took " + timeElapsed + " ms.");
	}

	/**
	 * 
	 * @param calculator
	 */
	private void doIterations(Calculator calculator) {
		for (int i = 0; i < LOAD_ITERATIONS; i++) {
			int result = calculator.add(4, 5);
		}
	}

}
