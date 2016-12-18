package org.markandersen.service.invocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.markandersen.service.invocation.InvocationObjectHistory;
import org.markandersen.service.invocation.InvocationStrategyInstance;
import org.markandersen.service.invocation.RoundRobinInvocationStrategyFactory;
import org.markandersen.test.ExampleClass;

import junit.framework.TestCase;


/**
 * DOCUMENT ME!
 */
public class RoundRobinInvocationStrategyFactoryTest extends TestCase {

	/**
	 * 
	 */
	protected ExampleClass object1;

	/**
	 * 
	 */
	protected ExampleClass object2;

	/**
	 * 
	 */
	protected ExampleClass object3;

	/**
	 * 
	 */
	protected List<ExampleClass> references;

	/**
	 * 
	 */
	protected RoundRobinInvocationStrategyFactory strategyFactory;

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		super.setUp();
		object1 = new ExampleClass();
		object1.setName("object1");
		object2 = new ExampleClass();
		object2.setName("object2");
		object3 = new ExampleClass();
		object3.setName("object3");
		references = new ArrayList<ExampleClass>();
		references.add(object1);
		references.add(object2);
		references.add(object3);
		strategyFactory = new RoundRobinInvocationStrategyFactory(references);
	}

	/**
	 * 
	 */
	public void testRoundRobinStrategyConstructor() {

		List allKnownReferences = strategyFactory.getAllKnownReferences();
		List goodReferences = strategyFactory.getGoodReferences();
		assertEquals(allKnownReferences, goodReferences);

		List badReferences = strategyFactory.getBadReferences();
		assertEquals("Shouldn't have any bad references.", 0, badReferences
				.size());
	}

	/**
	 * Test the reportBadReference method.
	 * 
	 * @throws Exception
	 */
	public void testRoundRobinStrategyReportBadReference() throws Exception {

		long beforeTime = System.currentTimeMillis();
		strategyFactory.reportBadReference(object1);

		long afterTime = System.currentTimeMillis();
		List allKnownReferences = strategyFactory.getAllKnownReferences();
		assertEquals("allKnownReferences shouldn't change.", 3,
				allKnownReferences.size());

		List goodReferences = strategyFactory.getGoodReferences();
		assertFalse(goodReferences.contains(object1));
		assertEquals(2, goodReferences.size());

		List badReferences = strategyFactory.getBadReferences();
		assertTrue(badReferences.contains(object1));
		assertEquals(1, badReferences.size());

		// make sure the history object was updated with time information.
		Map historyMap = strategyFactory.getObjectHistory();
		InvocationObjectHistory badHistory = (InvocationObjectHistory) historyMap
				.get(object1);
		assertFalse("history should have said the object was bad.", badHistory
				.isObjectGood());
		assertTrue(
				"bad invocation timestamp wasn't updated correctly.",
				((badHistory.getBadInvocationTime() >= beforeTime) && (badHistory
						.getBadInvocationTime() <= afterTime)));

		// make sure other histories are good.
		InvocationObjectHistory goodHistory2 = (InvocationObjectHistory) historyMap
				.get(object2);
		assertTrue("object should be good.", goodHistory2.isObjectGood());
		assertTrue("object history time bad.", goodHistory2
				.getBadInvocationTime() < 0);

		InvocationObjectHistory goodHistory3 = (InvocationObjectHistory) historyMap
				.get(object3);
		assertTrue("object should be good.", goodHistory3.isObjectGood());
		assertTrue("object history time bad.", goodHistory3
				.getBadInvocationTime() < 0);
		// pause a second for timing.
		Thread.sleep(200);
		// try removing the same object.
		strategyFactory.reportBadReference(object1);
		allKnownReferences = strategyFactory.getAllKnownReferences();
		assertEquals("allKnownReferences shouldn't change.", 3,
				allKnownReferences.size());
		goodReferences = strategyFactory.getGoodReferences();
		assertFalse(goodReferences.contains(object1));
		assertEquals(2, goodReferences.size());
		badReferences = strategyFactory.getBadReferences();
		assertTrue(badReferences.contains(object1));
		assertEquals(1, badReferences.size());
		assertFalse("object should be bad.", badHistory.isObjectGood());
		assertTrue(
				"object bad time update when it shouldn't have been.",
				((badHistory.getBadInvocationTime() >= beforeTime) && (badHistory
						.getBadInvocationTime() <= afterTime)));
		// try removing a different object.
		strategyFactory.reportBadReference(object2);
		allKnownReferences = strategyFactory.getAllKnownReferences();
		assertEquals("allKnownReferences shouldn't change.", 3,
				allKnownReferences.size());
		goodReferences = strategyFactory.getGoodReferences();
		assertFalse(goodReferences.contains(object1));
		assertFalse(goodReferences.contains(object2));
		assertEquals(1, goodReferences.size());
		badReferences = strategyFactory.getBadReferences();
		assertTrue(badReferences.contains(object1));
		assertTrue(badReferences.contains(object2));
		assertEquals(2, badReferences.size());
	}

	/**
	 * Make sure newInstance returns a list of objects that is different for
	 * every newInstance call.
	 */
	public void testGetInvocationInstanceGoodCase() {

		InvocationStrategyInstance strategy = strategyFactory
				.getInvocationStrategyInstance();

		for (int i = 0; i < references.size(); i++) {

			Object nextInvocationObject = strategy.getNextInvocationObject();
			assertNotNull("object shouldn't be null.", nextInvocationObject);
			assertSame("Object should have been the same.", references.get(i),
					nextInvocationObject);
		}
		assertNull("strategy should have no more object left.", strategy
				.getNextInvocationObject());
		// get a new strategy list and make sure that the
		// original first reference is in the back of the list.
		strategy = strategyFactory.getInvocationStrategyInstance();
		// rotate the reference list by one.
		Collections.rotate(references, -1);

		for (int i = 0; i < references.size(); i++) {

			Object nextInvocationObject = strategy.getNextInvocationObject();
			assertNotNull("object shouldn't be null.", nextInvocationObject);
			assertSame("Object should have been the same.", references.get(i),
					nextInvocationObject);
		}
		assertNull("strategy should have no more object left.", strategy
				.getNextInvocationObject());
	}

	/**
	 * Make sure newInstance returns a list of objects in the correct order if
	 * one of them is bad.
	 */
	public void testGetInvocationInstanceBadObject() {

		InvocationStrategyInstance strategy = strategyFactory
				.getInvocationStrategyInstance();
		// simulate object2 is bad.
		strategy.addBadInvocation(object2);
		// get a new one to get an updated list.
		strategy = strategyFactory.getInvocationStrategyInstance();

		List expectedList = new ArrayList();
		// object 3 is first because 2 is "dead" and the good ref
		// list has been rotated once.
		expectedList.add(object3);
		expectedList.add(object1);
		expectedList.add(object2);

		for (int i = 0; i < expectedList.size(); i++) {

			Object nextInvocationObject = strategy.getNextInvocationObject();
			assertNotNull("object shouldn't be null.", nextInvocationObject);
			assertSame("Object should have been the same.",
					expectedList.get(i), nextInvocationObject);
		}
		assertNull("strategy should have no more object left.", strategy
				.getNextInvocationObject());
		// get a new strategy to simulate a different invocation.
		strategy = strategyFactory.getInvocationStrategyInstance();
		expectedList = new ArrayList();
		// object 1 is first because 2 is "dead" and the good ref
		// list has been rotated twice.
		expectedList.add(object1);
		expectedList.add(object3);
		expectedList.add(object2);

		for (int i = 0; i < expectedList.size(); i++) {

			Object nextInvocationObject = strategy.getNextInvocationObject();
			assertNotNull("object shouldn't be null.", nextInvocationObject);
			assertSame("Object should have been the same.",
					expectedList.get(i), nextInvocationObject);
		}
		assertNull("strategy should have no more object left.", strategy
				.getNextInvocationObject());
	}

	/**
	 * 
	 */
	public void testGetInvocationInstanceBadObjectRenewed() {

		InvocationStrategyInstance strategy = strategyFactory
				.getInvocationStrategyInstance();
		// simulate objece 1 & object2 is bad.
		strategy.addBadInvocation(object1);
		strategy.addBadInvocation(object2);
		// get a new strategy instance to get an updated list.
		strategy = strategyFactory.getInvocationStrategyInstance();

		// reverify that the order is object3,1,2.
		List expectedList = new ArrayList();
		// object 3 is first because 1 & 2 are "dead" and at the back.
		expectedList.add(object3);
		expectedList.add(object1);
		expectedList.add(object2);

		// assert to make sure the order is correct.
		for (int i = 0; i < expectedList.size(); i++) {

			Object nextInvocationObject = strategy.getNextInvocationObject();
			assertNotNull("object shouldn't be null.", nextInvocationObject);
			assertSame("Object should have been the same.",
					expectedList.get(i), nextInvocationObject);
		}
		assertNull("strategy should have no more objects left.", strategy
				.getNextInvocationObject());
		// renew object2
		strategyFactory.renewObject(object2);
		strategy = strategyFactory.getInvocationStrategyInstance();
		// reverify that the order is object3,2,1.
		expectedList = new ArrayList();
		// object 3 is first because 1 is "dead" and #2 has just been re added
		// to the good list.
		expectedList.add(object3);
		expectedList.add(object2);
		expectedList.add(object1);

		for (int i = 0; i < expectedList.size(); i++) {

			Object nextInvocationObject = strategy.getNextInvocationObject();
			assertNotNull("object shouldn't be null.", nextInvocationObject);
			assertSame("Object should have been the same.",
					expectedList.get(i), nextInvocationObject);
		}
		assertNull("strategy should have no more object left.", strategy
				.getNextInvocationObject());
	}

	/**
	 * records some bad references, and then waits for the "add back to good
	 * list delay" and make sure they are readded as good references.
	 */
	public void testGetInvocationInstanceBadObjectRenewedBecauseOfTime()
			throws Exception {

		long objectRenewalWaitTime = 200; // 200 ms
		strategyFactory.setObjectRetryDelay(objectRenewalWaitTime);

		InvocationStrategyInstance strategy = strategyFactory
				.getInvocationStrategyInstance();
		// simulate object2 is bad.
		strategy.addBadInvocation(object2);
		// get a new one to get an updated list.
		strategy = strategyFactory.getInvocationStrategyInstance();

		List expectedList = new ArrayList();
		// object 3 is first because 2 is "dead" and the good ref
		// list has been rotated once.
		expectedList.add(object3);
		expectedList.add(object1);
		expectedList.add(object2);

		for (int i = 0; i < expectedList.size(); i++) {

			Object nextInvocationObject = strategy.getNextInvocationObject();
			assertNotNull("object shouldn't be null.", nextInvocationObject);
			assertSame("Object should have been the same.",
					expectedList.get(i), nextInvocationObject);
		}
		assertNull("strategy should have no more object left.", strategy
				.getNextInvocationObject());
		// now we know the list is setup correctly. We need to pause and wait
		// for object2 to be added back to the good list.
		Thread.sleep(strategyFactory.getObjectRetryDelay() + 100);
		// get a new strategy instace to make sure all bad objects are
		// moved to new.
		strategy = strategyFactory.getInvocationStrategyInstance();
		// check the list to make sure it happened.
		assertTrue("object2 should have been in the good list.",
				strategyFactory.getGoodReferences().contains(object2));
		assertFalse("object2 should have not been in the bad list.",
				strategyFactory.getBadReferences().contains(object2));
	}

	/**
	 * test adding an object that wasn't in the object history map. Make sure
	 * the map wasn't changed and no excpetion was thrown.
	 */
	public void testReportBadObjectObjectNotInMap() {

		String unknownObject = "help me.";
		Map objectHistory = strategyFactory.getObjectHistory();
		int mapSize = objectHistory.size();
		strategyFactory.reportBadReference(unknownObject);
		assertEquals("map shouldn't have been changed.", mapSize,
				strategyFactory.getObjectHistory().size());
		strategyFactory.reportBadReference(null);
		assertEquals("map shouldn't have been changed.", mapSize,
				strategyFactory.getObjectHistory().size());
	}
}