package org.markandersen.service.invocation;

import java.util.ArrayList;
import java.util.List;

import org.markandersen.service.invocation.InvocationStrategyInstance;
import org.markandersen.service.invocation.PreferLocalInvocationStrategyFactory;
import org.markandersen.test.BaseTestCase;
import org.markandersen.test.ExampleClass;


/**
 * DOCUMENT ME!
 */
public class PreferLocalInvocationStrategyFactoryTest extends BaseTestCase {

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
	protected ExampleClass preferLocalObject;

	/**
	 * 
	 */
	protected List references;

	/**
	 * 
	 */
	protected PreferLocalInvocationStrategyFactory strategyFactory;

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
		preferLocalObject = object2;
		references = new ArrayList();
		references.add(object1);
		references.add(object2);
		references.add(object3);
		strategyFactory = new PreferLocalInvocationStrategyFactory(object2,
				references);
	}

	/**
	 * 
	 */
	public void testConstructor() {

		List allKnownReferences = strategyFactory.getAllKnownReferences();
		List goodReferences = strategyFactory.getGoodReferences();
		// make sure prefer local object is at the front.
		assertEquals(preferLocalObject, goodReferences.get(0));

		List badReferences = strategyFactory.getBadReferences();
		assertEquals("Shouldn't have any bad references.", 0, badReferences
				.size());
	}

	/**
	 * 
	 */
	public void testConstructorPreferLocalObjectNotInList() {

		List subList = new ArrayList();
		subList.add(object1);
		subList.add(object3);
		strategyFactory = new PreferLocalInvocationStrategyFactory(
				preferLocalObject, subList);

		List allKnownReferences = strategyFactory.getAllKnownReferences();
		List goodReferences = strategyFactory.getGoodReferences();
		// make sure prefer local object is at the front.
		assertTrue(allKnownReferences.contains(preferLocalObject));
		assertEquals(preferLocalObject, goodReferences.get(0));

		List badReferences = strategyFactory.getBadReferences();
		assertEquals("Shouldn't have any bad references.", 0, badReferences
				.size());
	}

	/**
	 * make sure the prefer local object is always first.
	 */
	public void testGetInvocationInstance() {

		InvocationStrategyInstance invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", preferLocalObject,
				invocationStrategyInstance.getNextInvocationObject());
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", preferLocalObject,
				invocationStrategyInstance.getNextInvocationObject());
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", preferLocalObject,
				invocationStrategyInstance.getNextInvocationObject());
	}

	/**
	 * have the prefer local object be bad, so next object is sticky.
	 */
	public void testGetInvocationInstanceBadReferenceNextObject() {

		InvocationStrategyInstance invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", preferLocalObject,
				invocationStrategyInstance.getNextInvocationObject());
		invocationStrategyInstance.addBadInvocation(preferLocalObject);
		// should be object 2 now.
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object1, invocationStrategyInstance
				.getNextInvocationObject());
		// make sure it is object 2 again.
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object1, invocationStrategyInstance
				.getNextInvocationObject());
		// make sure it is object 2 again.
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object1, invocationStrategyInstance
				.getNextInvocationObject());
	}

	/**
	 * have the first object be bad, wait for it to return to good status, but
	 * make sure it doesn't interfer with the good references.
	 * 
	 * @throws Exception
	 */
	public void testGetInvocationInstanceBadReferencePreferLocalEventuallyRenewed()
			throws Exception {

		long objectRetryDelay = 200; // 200 ms
		strategyFactory.setObjectRetryDelay(objectRetryDelay);

		InvocationStrategyInstance invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", preferLocalObject,
				invocationStrategyInstance.getNextInvocationObject());
		invocationStrategyInstance.addBadInvocation(preferLocalObject);
		// should be object 1 now.
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", object1, invocationStrategyInstance
				.getNextInvocationObject());
		assertTrue("prefer local object should be in bad list.",
				strategyFactory.getBadReferences().contains(preferLocalObject));
		// pause long enough to get perfer local object back into the good list.
		Thread.sleep(objectRetryDelay + 100);
		// make sure it is back to prefer local object again.
		invocationStrategyInstance = strategyFactory
				.getInvocationStrategyInstance();
		assertSame("wrong object first.", preferLocalObject,
				invocationStrategyInstance.getNextInvocationObject());
		assertEquals("prefer local object should be in good list", 0,
				strategyFactory.getGoodReferences().indexOf(preferLocalObject));
	}
}