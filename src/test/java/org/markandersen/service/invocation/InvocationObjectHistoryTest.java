package org.markandersen.service.invocation;

import org.markandersen.service.invocation.InvocationObjectHistory;
import org.markandersen.test.BaseTestCase;


/**
 * DOCUMENT ME!
 */
public class InvocationObjectHistoryTest extends BaseTestCase {

	/**
	 * 
	 */
	private String dummyObject = "lasjdfwoeiur";

	/**
	 * 
	 */
	private InvocationObjectHistory objectHistory;

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		super.setUp();
		objectHistory = new InvocationObjectHistory(dummyObject);
	}

	/**
	 * 
	 */
	public void testBadInvocation() {
		// make sure it is intialized correctly.
		assertTrue(objectHistory.isObjectGood());
		assertEquals(-1, objectHistory.getBadInvocationTime());

		long now = System.currentTimeMillis();
		objectHistory.badInvocation();
		// verfiy the object is bad and the bad invoation time is set.
		assertFalse(objectHistory.isObjectGood());
		assertTrue(objectHistory.getBadInvocationTime() >= now);

		long badInvocationTime = objectHistory.getBadInvocationTime();
		objectHistory.badInvocation();
		assertEquals(
				"badInvocationTime was updated and it shouldn't have been.",
				badInvocationTime, objectHistory.getBadInvocationTime());
	}

	/**
	 * 
	 */
	public void testEnableGoodStatus() {

		// set the bad status
		long now = System.currentTimeMillis();
		objectHistory.badInvocation();
		// verfiy the object is bad and the bad invoation time is set.
		assertFalse(objectHistory.isObjectGood());
		assertTrue(objectHistory.getBadInvocationTime() >= now);
		objectHistory.enableGoodStatus();
		assertTrue("object status should be good.", objectHistory
				.isObjectGood());
		assertEquals("time value should have been -1.", -1, objectHistory
				.getBadInvocationTime());
	}
}