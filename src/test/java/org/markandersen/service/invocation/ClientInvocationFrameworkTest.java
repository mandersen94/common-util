package org.markandersen.service.invocation;

import org.easymock.MockControl;
import org.markandersen.service.invocation.ClientInvocationFramework;
import org.markandersen.service.invocation.InvocationResultHandler;
import org.markandersen.service.invocation.InvocationStrategyInstance;
import org.markandersen.service.invocation.InvocationStrategyInstanceFactory;
import org.markandersen.service.invocation.NoObjectsAvailableException;
import org.markandersen.service.invocation.StubInvocationResultHandler;
import org.markandersen.test.BaseTestCase;
import org.markandersen.test.ExampleTestException;
import org.markandersen.test.ExampleTestInterface;


/**
 * DOCUMENT ME!
 */
public class ClientInvocationFrameworkTest extends BaseTestCase {

	/**
	 * 
	 */
	private InvocationStrategyInstance invocationStrategyMock;

	/**
	 * 
	 */
	private MockControl invocationStrategyInstanceMockControl;

	/**
	 * 
	 */
	private MockControl testInterfaceMockControl;

	/**
	 * 
	 */
	private ExampleTestInterface testInterfaceMock;

	/**
	 * 
	 */
	private ExampleTestInterface testObject;

	/**
	 * 
	 */
	private StubInvocationResultHandler invocationResultHandler;

	/**
	 * 
	 */
	private ClientInvocationFramework invocationFramework;

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		super.setUp();
		testInterfaceMockControl = MockControl
				.createStrictControl(ExampleTestInterface.class);
		testInterfaceMock = (ExampleTestInterface) testInterfaceMockControl
				.getMock();
		invocationStrategyInstanceMockControl = MockControl
				.createStrictControl(InvocationStrategyInstance.class);
		invocationStrategyMock = (InvocationStrategyInstance) invocationStrategyInstanceMockControl
				.getMock();

		Class[] effectiveClasses = new Class[] { ExampleTestInterface.class };
		ClassLoader classLoader = effectiveClasses[0].getClassLoader();
		InvocationStrategyInstanceFactory invocationStrategyFactory = new SimpleInvocationStrategyInstanceFactory(
				invocationStrategyMock);
		invocationResultHandler = new StubInvocationResultHandler(true, true);
		invocationFramework = new ClientInvocationFramework(
				invocationStrategyFactory, invocationResultHandler,
				effectiveClasses, classLoader);
		testObject = (ExampleTestInterface) invocationFramework
				.createInvocationManager();
	}

	/**
	 * 
	 */
	public void testCreation() {

		Class[] effectiveClasses = new Class[] { ExampleTestInterface.class };
		Object[] realObjects = new Object[0];
		ClassLoader classLoader = effectiveClasses[0].getClassLoader();
		InvocationStrategyInstanceFactory invocationStrategyFactory = null;
		ClientInvocationFramework tempInvocationFramework = new ClientInvocationFramework(
				invocationStrategyFactory, new StubInvocationResultHandler(
						true, true), effectiveClasses, classLoader);
		Object objectProxy = tempInvocationFramework.createInvocationManager();
		// make sure it is an instance of.
		assertTrue(objectProxy instanceof ExampleTestInterface);
	}

	/**
	 * 
	 */
	public void testCreationNullInvocationResultHandler() {

		Class[] effectiveClasses = new Class[] { ExampleTestInterface.class };
		Object[] realObjects = new Object[0];
		ClassLoader classLoader = effectiveClasses[0].getClassLoader();
		InvocationStrategyInstanceFactory invocationStrategyFactory = null;

		try {

			ClientInvocationFramework tempInvocationFramework = new ClientInvocationFramework(
					invocationStrategyFactory, null, effectiveClasses,
					classLoader);
			tempInvocationFramework.createInvocationManager();
			fail("should have thrown null pointer exception.");
		} catch (NullPointerException ex) {
		}
	}

	/**
	 * invoke on the object and it works fine the first time.
	 * 
	 * @throws ExampleTestException
	 */
	public void testInvokeGoodResult() throws ExampleTestException {

		int arg1 = 1;
		int arg2 = 2;
		int expectedResult = 3;
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyInstanceMockControl.replay();
		testInterfaceMock.doAddition(arg1, arg2);
		testInterfaceMockControl.setReturnValue(expectedResult);
		testInterfaceMockControl.replay();

		int result = testObject.doAddition(1, 2);
		assertEquals(expectedResult, result);
		invocationStrategyInstanceMockControl.verify();
		testInterfaceMockControl.verify();
	}

	/**
	 * invoke on the object and it throws an exception but our handler treats it
	 * as the good case.
	 * 
	 * @throws ExampleTestException
	 */
	public void testInvokeThrowsApplicationExceptionNoFailover()
			throws Exception {

		int arg1 = 1;
		int arg2 = 2;
		ExampleTestException expectedException = new ExampleTestException();
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyInstanceMockControl.replay();
		testInterfaceMock.doAddition(arg1, arg2);
		testInterfaceMockControl.setThrowable(expectedException);
		testInterfaceMockControl.replay();

		try {
			testObject.doAddition(1, 2);
			fail("should have thrown exception.");
		} catch (Exception ex) {
			assertSame(expectedException, ex);
		}
		invocationStrategyInstanceMockControl.verify();
		testInterfaceMockControl.verify();
	}

	/**
	 * invoke on the object and it throws an exception but our handler treats it
	 * as the good case.
	 * 
	 * @throws ExampleTestException
	 */
	public void testInvokeBadResultGoodExecption() throws Exception {

		int arg1 = 1;
		int arg2 = 2;
		ExampleTestException expectedException = new ExampleTestException();
		invocationFramework.setResultHandler(new StubInvocationResultHandler(
				false, true));
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyMock.addBadInvocation(testInterfaceMock);
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyInstanceMockControl.replay();
		testInterfaceMock.doAddition(arg1, arg2);
		testInterfaceMockControl.setReturnValue(-1);
		testInterfaceMock.doAddition(arg1, arg2);
		testInterfaceMockControl.setThrowable(expectedException);
		testInterfaceMockControl.replay();

		try {
			testObject.doAddition(1, 2);
			fail("should have thrown exception.");
		} catch (Exception ex) {
			assertSame(expectedException, ex);
		}
		invocationStrategyInstanceMockControl.verify();
		testInterfaceMockControl.verify();
	}

	/**
	 * invoke on the object and it throws an exception but our handler treats it
	 * as the good case.
	 * 
	 * @throws ExampleTestException
	 */
	public void testInvokeBadResultGoodResult() throws Exception {

		int arg1 = 1;
		int arg2 = 2;
		int expectedResult = 3;
		invocationFramework
				.setResultHandler(new IntegerPositiveInvocationResultHandler());
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyMock.addBadInvocation(testInterfaceMock);
		// return the same mock again.
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyInstanceMockControl.replay();
		testInterfaceMock.doAddition(arg1, arg2);
		testInterfaceMockControl.setReturnValue(-1);
		testInterfaceMock.doAddition(arg1, arg2);
		testInterfaceMockControl.setReturnValue(expectedResult);
		testInterfaceMockControl.replay();

		int result = testObject.doAddition(1, 2);
		assertEquals(expectedResult, result);
		invocationStrategyInstanceMockControl.verify();
		testInterfaceMockControl.verify();
	}

	/**
	 * invocation strategy returns null for getNextObject.
	 */
	public void testInvokeNoObjectsAvailable() throws Exception {

		int arg1 = 1;
		int arg2 = 2;
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(null);
		invocationStrategyInstanceMockControl.replay();

		try {
			testObject.doAddition(1, 2);
			fail("should have thrown exception.");
		} catch (NoObjectsAvailableException ex) {
		}
		invocationStrategyInstanceMockControl.verify();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws ExampleTestException
	 */
	public void testInvokeThrowsExceptionThenNoObjectsAvailable()
			throws Exception {

		int arg1 = 1;
		int arg2 = 2;
		// set false to simulate failed invocation.
		invocationResultHandler.setExceptionGood(false);

		ExampleTestException expectedException = new ExampleTestException();
		// first time through
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyMock.addBadInvocation(testInterfaceMock);
		// second time return null for object.
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(null);
		invocationStrategyInstanceMockControl.replay();
		testInterfaceMock.doAddition(arg1, arg2);
		testInterfaceMockControl.setThrowable(expectedException);
		testInterfaceMockControl.replay();

		try {
			testObject.doAddition(1, 2);
			fail("should have thrown exception.");
		} catch (NoObjectsAvailableException ex) {
		}
		invocationStrategyInstanceMockControl.verify();
		testInterfaceMockControl.verify();
	}

	/**
	 * invoke on the object and it throws an exception. try again and the result
	 * is considered good. This is our most common business case.
	 */
	public void testInvokeThrowsExceptionThenGoodResult() throws Exception {

		int arg1 = 1;
		int arg2 = 2;
		int expectedResult = 3;
		// set false to simulate failed invocation.
		invocationFramework.getResultHandler();
		invocationFramework.setResultHandler(new StubInvocationResultHandler(
				true, false));

		ExampleTestException expectedException = new ExampleTestException();
		// first time through
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyMock.addBadInvocation(testInterfaceMock);
		// second time return another object. really same mock.
		// different result.
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyInstanceMockControl.replay();
		testInterfaceMock.doAddition(arg1, arg2);
		testInterfaceMockControl.setThrowable(expectedException);
		testInterfaceMock.doAddition(arg1, arg2);
		testInterfaceMockControl.setReturnValue(expectedResult);
		testInterfaceMockControl.replay();

		int result = testObject.doAddition(1, 2);
		assertEquals(expectedResult, result);
		invocationStrategyInstanceMockControl.verify();
		testInterfaceMockControl.verify();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws ExampleTestException
	 */
	public void testInvokeThrowsIllegalAccessException() throws Exception {

		int arg1 = 1;
		int arg2 = 2;
		System.setProperty("testing.clientInvocation.exception", "true");

		IllegalAccessException expectedException = new IllegalAccessException();
		System.getProperties().put("testing.clientInvocation.exception.object",
				expectedException);
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyInstanceMockControl.replay();
		testInterfaceMockControl.replay();

		try {
			testObject.doAddition(1, 2);
			fail("should have thrown exception.");
		} catch (RuntimeException ex) {
			assertSame(expectedException, ex.getCause());
		} finally {
			System.setProperty("testing.clientInvocation.exception", "false");
		}
		invocationStrategyInstanceMockControl.verify();
		testInterfaceMockControl.verify();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws ExampleTestException
	 */
	public void testInvokeThrowsIllegalArgumentException() throws Exception {

		int arg1 = 1;
		int arg2 = 2;
		System.setProperty("testing.clientInvocation.exception", "true");

		IllegalArgumentException expectedException = new IllegalArgumentException();
		System.getProperties().put("testing.clientInvocation.exception.object",
				expectedException);
		invocationStrategyMock.getNextInvocationObject();
		invocationStrategyInstanceMockControl.setReturnValue(testInterfaceMock);
		invocationStrategyInstanceMockControl.replay();
		testInterfaceMockControl.replay();

		try {
			testObject.doAddition(1, 2);
			fail("should have thrown exception.");
		} catch (RuntimeException ex) {
			assertSame(expectedException, ex.getCause());
		} finally {
			System.setProperty("testing.clientInvocation.exception", "false");
		}
		invocationStrategyInstanceMockControl.verify();
		testInterfaceMockControl.verify();
	}

	/**
	 * DOCUMENT ME!
	 */
	static class IntegerPositiveInvocationResultHandler implements
			InvocationResultHandler {

		/**
		 * DOCUMENT ME!
		 * 
		 * @param ex
		 * 
		 * @return
		 */
		public boolean didInvocationSucceedException(Throwable ex) {
			// always false
			return false;
		}

		/**
		 * DOCUMENT ME!
		 * 
		 * @param result
		 * 
		 * @return
		 */
		public boolean didInvocationSucceedReturnValue(Object result) {

			// assume integer. if < 0 return false. if >= 0 return true.
			Integer intResult = (Integer) result;

			if (intResult.intValue() < 0) {
				return false;
			} else {
				return true;
			}
		}
	}
}