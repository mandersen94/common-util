package org.markandersen.service.invocation;

import org.markandersen.service.invocation.InvocationResultHandler;
import org.markandersen.service.invocation.corba.CORBAInvocationResultHandler;
import org.markandersen.test.BaseTestCase;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.TIMEOUT;
import org.omg.CORBA.TRANSIENT;


/**
 * DOCUMENT ME!
 */
public class CORBAInvocationResultHandlerTest extends BaseTestCase {

	/**
	 * 
	 */
	private InvocationResultHandler handler;

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		super.setUp();
		handler = new CORBAInvocationResultHandler();
	}

	/**
	 * 
	 */
	public void testDidInvocationSucceedReturnValue() {
		// make sure it always returns true.
		assertTrue("handler should have returned true.", handler
				.didInvocationSucceedReturnValue("asdfa"));
		assertTrue("handler should have returned true.", handler
				.didInvocationSucceedReturnValue(null));
	}

	/**
	 * 
	 */
	public void testDidInvocationSucceedException() {
		// make sure it returns false for given exceptions.
		assertFalse("should have returned false", handler
				.didInvocationSucceedException(new TRANSIENT()));
		assertFalse("should have returned false", handler
				.didInvocationSucceedException(new TIMEOUT()));
		assertFalse("should have returned false", handler
				.didInvocationSucceedException(new COMM_FAILURE()));
		assertTrue("should have returned true", handler
				.didInvocationSucceedException(new BAD_PARAM()));
		assertTrue("should have returned true", handler
				.didInvocationSucceedException(new RuntimeException()));
	}
}