package org.markandersen.service.invocation;

import java.util.ArrayList;
import java.util.List;

import org.markandersen.service.invocation.InvocationStrategyInstance;
import org.markandersen.service.invocation.StickyInvocationStrategyFactory;
import org.markandersen.test.BaseTestCase;
import org.markandersen.test.ExampleClass;


/**
 * DOCUMENT ME!
 */
public class StickyInvocationStrategyFactoryTest extends BaseTestCase {

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
	protected List references;

	/**
	 * 
	 */
	protected StickyInvocationStrategyFactory strategyFactory;

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
		references = new ArrayList();
		references.add(object1);
		references.add(object2);
		references.add(object3);
		strategyFactory = new StickyInvocationStrategyFactory(references);
	}

	/**
	 * 
	 */
	public void testConstructor() {

		List allKnownReferences = strategyFactory.getAllKnownReferences();
		List goodReferences = strategyFactory.getGoodReferences();
		assertEquals(allKnownReferences, goodReferences);

		List badReferences = strategyFactory.getBadReferences();
		assertEquals("Shouldn't have any bad references.", 0, badReferences
				.size());
	}

	/**
	 * Make sure the first object in the intial list is correct. And that
	 * repeated requests for invocations instances have the same object at the
	 * front of the list.
	 */
	public void testGetInvocationInstance() {

		InvocationStrategyInstance invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object1, invocationStrategyInstance
				.getNextInvocationObject());
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object1, invocationStrategyInstance
				.getNextInvocationObject());
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object1, invocationStrategyInstance
				.getNextInvocationObject());
	}

	/**
	 * have the first object be bad, so next object is sticky.
	 */
	public void testGetInvocationInstanceBadReferenceStickyToNextObject() {

		InvocationStrategyInstance invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object1, invocationStrategyInstance
				.getNextInvocationObject());
		invocationStrategyInstance.addBadInvocation(object1);
		// should be object 2 now.
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object2, invocationStrategyInstance
				.getNextInvocationObject());
		// make sure it is object 2 again.
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object2, invocationStrategyInstance
				.getNextInvocationObject());
		// make sure it is object 2 again.
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object2, invocationStrategyInstance
				.getNextInvocationObject());
	}

	/**
	 * have the first object be bad, wait for it to return to good status, but
	 * make sure it doesn't interfer with the good references.
	 * 
	 * @throws Exception
	 */
	public void testGetInvocationInstanceBadReferenceEventuallyRenewed()
			throws Exception {

		long objectRetryDelay = 200; // 200 ms
		strategyFactory.setObjectRetryDelay(objectRetryDelay);

		InvocationStrategyInstance invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object1, invocationStrategyInstance
				.getNextInvocationObject());
		invocationStrategyInstance.addBadInvocation(object1);
		// should be object 2 now.
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object2, invocationStrategyInstance
				.getNextInvocationObject());
		// pause long enough to get object 1 back into the good list.
		Thread.sleep(objectRetryDelay + 100);
		// make sure it is object 2 again.
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object2, invocationStrategyInstance
				.getNextInvocationObject());
		assertTrue("object1 should be in good list", strategyFactory
				.getGoodReferences().contains(object1));
	}
}