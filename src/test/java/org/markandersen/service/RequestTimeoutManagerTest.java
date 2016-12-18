package org.markandersen.service;

import java.io.IOException;

import org.easymock.MockControl;
import org.markandersen.service.timeout.RequestTimeoutException;
import org.markandersen.service.timeout.RequestTimeoutHelper;
import org.markandersen.service.timeout.RequestTimeoutManager;
import org.markandersen.test.BaseTestCase;
import org.markandersen.test.SimpleOperation;
import org.markandersen.test.SimpleOperationImpl;


import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 * 
 */
public class RequestTimeoutManagerTest extends BaseTestCase {

	/**
	 * 
	 */
	protected ExecutorService executorService;

	/**
	 * 
	 */
	protected String arg1 = "hi mom!";

	/**
	 * 
	 */
	protected String arg2 = "hi dad!";

	/**
	 * 
	 */
	protected RequestTimeoutHelper helper;

	/**
	 * 
	 */
	private MockControl<RequestTimeoutHelper> helperMockControl;

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		super.setUp();
		executorService = Executors.newFixedThreadPool(2);
		helper = new SimpleOperationRequestTimeoutHelper();
		helperMockControl = MockControl
				.createControl(RequestTimeoutHelper.class);
		helper = helperMockControl.getMock();
		helperMockControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		executorService.shutdownNow();
	}

	/**
	 * 
	 * 
	 * @throws Exception
	 */
	public void testRequestSuccessful() throws Exception {
		helper.getTimeToWait(null, null, null);
		helperMockControl.setReturnValue(5000L);
		helper.getTimeUnit(null, null, null);
		helperMockControl.setReturnValue(TimeUnit.MILLISECONDS);
		helper.getShouldInterruptOnCancel(null, null, null);
		helperMockControl.setReturnValue(false);
		helper.getShouldOverrideException(null, null, null);
		helperMockControl.setReturnValue(false);
		helperMockControl.replay();

		SimpleOperation requestOperationImpl = new SimpleOperationImpl();
		SimpleOperation service = (SimpleOperation) RequestTimeoutManager
				.getRequestTimeoutManager(null,
						new Class[] { SimpleOperation.class },
						requestOperationImpl, executorService, helper);
		String result = service.concatString(arg1, arg2);
		assertEquals("wrong result returned.", arg1 + arg2, result);
		helperMockControl.verify();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRequestTimeoutNoHandleException() throws Exception {
		helper.getTimeToWait(null, null, null);
		helperMockControl.setReturnValue(1000L);
		helper.getTimeUnit(null, null, null);
		helperMockControl.setReturnValue(TimeUnit.MILLISECONDS);
		helper.getShouldInterruptOnCancel(null, null, null);
		helperMockControl.setReturnValue(false);
		helper.getShouldOverrideException(null, null, null);
		helperMockControl.setReturnValue(false);
		helperMockControl.replay();

		SimpleOperation requestOperation = new SimpleOperation() {
			public String concatString(String arg1, String arg2) {

				try {
					Thread.sleep(20 * 1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				return arg1 + arg2;
			}
		};

		SimpleOperation service = (SimpleOperation) RequestTimeoutManager
				.getRequestTimeoutManager(null,
						new Class[] { SimpleOperation.class },
						requestOperation, executorService, helper);
		String string1 = "hi mom!";
		String string2 = "hi dad!";

		try {

			String result = service.concatString(string1, string2);
			fail("Should have thrown RequestTimeoutException.");
		} catch (RequestTimeoutException ex) {
			ex.printStackTrace();
		}
		helperMockControl.verify();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	public void testRequestTimeoutHandleExceptionReturnException()
			throws Exception {

		NullPointerException nullPointer = new NullPointerException("test");
		helper.getTimeToWait(null, null, null);
		helperMockControl.setReturnValue(1000L);
		helper.getTimeUnit(null, null, null);
		helperMockControl.setReturnValue(TimeUnit.MILLISECONDS);
		helper.getShouldInterruptOnCancel(null, null, null);
		helperMockControl.setReturnValue(false);
		helper.getShouldOverrideException(null, null, null);
		helperMockControl.setReturnValue(true);
		helper.overrideException(null, null, null, null);
		helperMockControl.setReturnValue(nullPointer);
		helperMockControl.replay();

		SimpleOperation requestOperation = new SimpleOperation() {
			public String concatString(String arg1, String arg2) {

				try {
					Thread.sleep(20 * 1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				return arg1 + arg2;
			}
		};

		SimpleOperation service = (SimpleOperation) RequestTimeoutManager
				.getRequestTimeoutManager(null,
						new Class[] { SimpleOperation.class },
						requestOperation, executorService, helper);
		String arg1 = "hi mom!";
		String arg2 = "hi dad!";

		try {

			String result = service.concatString(arg1, arg2);
			fail("Should have thrown RequestTimeoutException.");
		} catch (NullPointerException ex) {
			assertSame(nullPointer, ex);
		}
		helperMockControl.verify();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	public void testRequestTimeoutHandleExceptionReturnObject()
			throws Exception {

		String updatedString = "sdfsdlkjwerwe.rm";
		helper.getTimeToWait(null, null, null);
		helperMockControl.setReturnValue(1000L);
		helper.getTimeUnit(null, null, null);
		helperMockControl.setReturnValue(TimeUnit.MILLISECONDS);
		helper.getShouldInterruptOnCancel(null, null, null);
		helperMockControl.setReturnValue(false);
		helper.getShouldOverrideException(null, null, null);
		helperMockControl.setReturnValue(true);
		helper.overrideException(null, null, null, null);
		helperMockControl.setReturnValue(updatedString);
		helperMockControl.replay();

		SimpleOperation requestOperation = new SimpleOperation() {
			public String concatString(String arg1, String arg2) {

				try {
					Thread.sleep(20 * 1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				return arg1 + arg2;
			}
		};

		SimpleOperation service = (SimpleOperation) RequestTimeoutManager
				.getRequestTimeoutManager(null,
						new Class[] { SimpleOperation.class },
						requestOperation, executorService, helper);
		String arg1 = "hi mom!";
		String arg2 = "hi dad!";
		String result = service.concatString(arg1, arg2);
		assertEquals("Wrong result", updatedString, result);
		helperMockControl.verify();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	public void testRequestExecutionThrowsCheckedException() throws Exception {

		final IOException expectedException = new IOException("test exception");
		helper.getTimeToWait(null, null, null);
		helperMockControl.setReturnValue(1000L);
		helper.getTimeUnit(null, null, null);
		helperMockControl.setReturnValue(TimeUnit.MILLISECONDS);
		helper.getShouldInterruptOnCancel(null, null, null);
		helperMockControl.setReturnValue(false);
		helper.getShouldOverrideException(null, null, null);
		helperMockControl.setReturnValue(false);
		helperMockControl.replay();

		SimpleOperation requestOperation = new SimpleOperation() {
			public String concatString(String arg1, String arg2)
					throws IOException {
				throw expectedException;
			}
		};

		SimpleOperation service = (SimpleOperation) RequestTimeoutManager
				.getRequestTimeoutManager(null,
						new Class[] { SimpleOperation.class },
						requestOperation, executorService, helper);
		String arg1 = "hi mom!";
		String arg2 = "hi dad!";

		try {

			String result = service.concatString(arg1, arg2);
			fail("Should have thrown IOException.");
		} catch (IOException ex) {
			assertSame(expectedException, ex);
		}
		helperMockControl.verify();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	public void testRequestExecutionThrowsUncheckedException() throws Exception {

		final NullPointerException expectedException = new NullPointerException(
				"test null pointer");
		helper.getTimeToWait(null, null, null);
		helperMockControl.setReturnValue(1000L);
		helper.getTimeUnit(null, null, null);
		helperMockControl.setReturnValue(TimeUnit.MILLISECONDS);
		helper.getShouldInterruptOnCancel(null, null, null);
		helperMockControl.setReturnValue(false);
		helper.getShouldOverrideException(null, null, null);
		helperMockControl.setReturnValue(false);
		helperMockControl.replay();

		SimpleOperation requestOperation = new SimpleOperation() {
			public String concatString(String arg1, String arg2)
					throws IOException {
				throw expectedException;
			}
		};

		SimpleOperation service = (SimpleOperation) RequestTimeoutManager
				.getRequestTimeoutManager(null,
						new Class[] { SimpleOperation.class },
						requestOperation, executorService, helper);
		String arg1 = "hi mom!";
		String arg2 = "hi dad!";

		try {

			String result = service.concatString(arg1, arg2);
			fail("Should have thrown NullPointerException.");
		} catch (NullPointerException ex) {
			assertSame(expectedException, ex);
		}
		helperMockControl.verify();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	public void testRequestExecutionThrowsError() throws Exception {

		final NoClassDefFoundError error = new NoClassDefFoundError(
				"test error");
		helper.getTimeToWait(null, null, null);
		helperMockControl.setReturnValue(1000L);
		helper.getTimeUnit(null, null, null);
		helperMockControl.setReturnValue(TimeUnit.MILLISECONDS);
		helper.getShouldInterruptOnCancel(null, null, null);
		helperMockControl.setReturnValue(false);
		helper.getShouldOverrideException(null, null, null);
		helperMockControl.setReturnValue(false);
		helperMockControl.replay();

		SimpleOperation requestOperation = new SimpleOperation() {
			public String concatString(String arg1, String arg2)
					throws IOException {
				throw error;
			}
		};

		SimpleOperation service = (SimpleOperation) RequestTimeoutManager
				.getRequestTimeoutManager(null,
						new Class[] { SimpleOperation.class },
						requestOperation, executorService, helper);
		String arg1 = "hi mom!";
		String arg2 = "hi dad!";

		try {

			String result = service.concatString(arg1, arg2);
			fail("Should have thrown NoClassDefFoundError.");
		} catch (NoClassDefFoundError ex) {
			// correct outcome
			assertSame(error, ex);
		}
		helperMockControl.verify();
	}
}