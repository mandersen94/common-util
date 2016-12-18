package org.markandersen.threads;

import org.markandersen.test.BaseTestCase;
import org.markandersen.threads.ThreadLocalThreadPoolExecutor;


import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.Future;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class ThreadLocalThreadPoolExecutorTest extends BaseTestCase {

	public static ExampleThreadLocal<String> mylocal = new ExampleThreadLocal<String>();

	private String expectedValue = "magicstring.";

	public void testSomething() throws Exception {

		ThreadLocalThreadPoolExecutor executor = new ThreadLocalThreadPoolExecutor(
				2, 3, 60 * 1000, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue());
		mylocal.set(expectedValue);
		System.out.println("in thread " + Thread.currentThread().getName()
				+ ", thread local value is = " + mylocal.get());

		final Callable runnable = new Callable() {
			public String call() {
				// this will be run in a different thread.
				String result = mylocal.get();
				System.out.println("in thread "
						+ Thread.currentThread().getName()
						+ ", thread local value is = " + result);
				return result;
			}
		};
		Future future = executor.submit(runnable);
		String otherThreadLocal = (String) future.get();
		assertEquals(expectedValue, otherThreadLocal);
	}
}
