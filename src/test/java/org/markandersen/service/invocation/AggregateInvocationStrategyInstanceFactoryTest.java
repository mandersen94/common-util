package org.markandersen.service.invocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.markandersen.service.invocation.AggregateInvocationStrategyInstanceFactory;
import org.markandersen.service.invocation.EnhancedInvocationStrategyFactory;
import org.markandersen.service.invocation.InvocationStrategyInstanceImpl;
import org.markandersen.test.BaseTestCase;


/**
 * DOCUMENT ME!
 */
public class AggregateInvocationStrategyInstanceFactoryTest extends
		BaseTestCase {

	/**
	 * 
	 */
	private IMocksControl factory1MockControl;

	/**
	 * 
	 */
	private EnhancedInvocationStrategyFactory<Object> factory1Mock;

	/**
	 * 
	 */

	private IMocksControl factory2MockControl;

	/**
	 * 
	 */
	private EnhancedInvocationStrategyFactory<Object> factory2Mock;

	/**
	 * 
	 */

	private IMocksControl factory3MockControl;

	/**
	 * 
	 */
	private EnhancedInvocationStrategyFactory<Object> factory3Mock;

	/**
	 * 
	 */
	private AggregateInvocationStrategyInstanceFactory<Object> strategyInstanceFactory;

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
		super.setUp();
		factory1MockControl = EasyMock.createStrictControl();
		factory1Mock = factory1MockControl
				.createMock(EnhancedInvocationStrategyFactory.class);

		factory2MockControl = EasyMock.createStrictControl();
		factory2Mock = factory2MockControl
				.createMock(EnhancedInvocationStrategyFactory.class);

		factory3MockControl = EasyMock.createStrictControl();
		factory3Mock = factory3MockControl
				.createMock(EnhancedInvocationStrategyFactory.class);

		List<EnhancedInvocationStrategyFactory<Object>> enhancedFactories = new ArrayList<EnhancedInvocationStrategyFactory<Object>>();
		enhancedFactories.add(factory1Mock);
		enhancedFactories.add(factory2Mock);
		enhancedFactories.add(factory3Mock);

		strategyInstanceFactory = new AggregateInvocationStrategyInstanceFactory<Object>(
				enhancedFactories);
	}

	/**
	 * 
	 */
	public void testInvocationStrategyInstance() {

		List<String> factory1Good = Arrays.asList(new String[] { "one", "two",
				"three" });
		List<String> factory1Bad = Arrays.asList(new String[] { "five", "size",
				"seven" });
		List<String> factory2Good = Arrays.asList(new String[] { "eight",
				"nine", "ten" });
		List<String> factory2Bad = Arrays.asList(new String[] { "eleven",
				"twelve", "thriteen" });
		List<String> factory3Good = Arrays.asList(new String[] { "fourteen",
				"fifteen", "sixteen" });
		List<String> factory3Bad = Arrays.asList(new String[] { "seventeen",
				"eightteen", "nightteen" });
		factory1Mock.calculateGoodReferences();
		factory1MockControl.andStubReturn(factory1Good);
		factory1Mock.calculateBadReferences();
		factory1MockControl.andStubReturn(factory1Bad);

		factory2Mock.calculateGoodReferences();
		factory2MockControl.andStubReturn(factory2Good);
		factory2Mock.calculateBadReferences();
		factory2MockControl.andStubReturn(factory2Bad);

		factory3Mock.calculateGoodReferences();
		factory3MockControl.andStubReturn(factory3Good);
		factory3Mock.calculateBadReferences();
		factory3MockControl.andStubReturn(factory3Bad);
		factory1MockControl.replay();
		factory2MockControl.replay();
		factory3MockControl.replay();

		InvocationStrategyInstanceImpl<Object> invocationStrategyInstance = (InvocationStrategyInstanceImpl<Object>) strategyInstanceFactory
				.getInvocationStrategyInstance();
		List<Object> invocationList = invocationStrategyInstance
				.getReferences();
		List<String> expectedList = new ArrayList<String>();
		expectedList.addAll(factory1Good);
		expectedList.addAll(factory2Good);
		expectedList.addAll(factory3Good);
		expectedList.addAll(factory1Bad);
		expectedList.addAll(factory2Bad);
		expectedList.addAll(factory3Bad);
		assertEquals("lists are not equals.", expectedList, invocationList);
		factory1MockControl.verify();
		factory2MockControl.verify();
		factory3MockControl.verify();
	}

	/**
	 * 
	 */
	public void testReportBadReference() {

		Object obj = new Object();
		factory1Mock.reportBadReference(obj);
		factory2Mock.reportBadReference(obj);
		factory3Mock.reportBadReference(obj);
		factory1MockControl.replay();
		factory2MockControl.replay();
		factory3MockControl.replay();
		strategyInstanceFactory.reportBadReference(obj);
		factory1MockControl.verify();
		factory2MockControl.verify();
		factory3MockControl.verify();
	}
}