package org.markandersen.service.invocation.ha.sample;

import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import org.markandersen.invocation.Interceptor;
import org.markandersen.invocation.InterceptorFramework;
import org.markandersen.invocation.Invoker;
import org.markandersen.net.NetworkUtil;
import org.markandersen.rmi.RegistryLocator;
import org.markandersen.service.invocation.RoundRobinInvocationStrategyFactory;
import org.markandersen.service.invocation.ha.HATargetSelectorInterceptor;
import org.markandersen.service.invocation.ha.HAViewInterceptor;
import org.markandersen.service.invocation.ha.SimpleHAInvoker;
import org.markandersen.service.invocation.ha.rmi.RMIInvocationResultHandler;
import org.markandersen.service.invocation.ha.sample.Calculator;
import org.markandersen.service.invocation.ha.sample.CalculatorService;
import org.markandersen.service.invocation.ha.sample.DivideByZeroException;
import org.markandersen.test.BaseTestCase;

public class CalculatorTest extends BaseTestCase {

	private static final String CALCULATOR_REGISTRY_NAME1 = "calculator1";

	private static final String CALCULATOR_REGISTRY_NAME2 = "calculator2";

	private static final String CALCULATOR_REGISTRY_NAME3 = "calculator3";

	private static CalculatorService calculatorService1;

	private static CalculatorService calculatorService2;

	private static CalculatorService calculatorService3;

	private static Registry registry;

	private List<Calculator> referenceList;

	private Calculator calculatorRemote1;

	private Calculator calculatorRemote2;

	private Calculator calculatorRemote3;

	private int registryPort = 1098;

	public void setUp() throws Exception {
		super.setUp();
		registry = RegistryLocator.getRegistry(registryPort);

		// creating services and starting them.
		NetworkUtil.getFreePort();
		calculatorService1 = new CalculatorService(1201, registry, CALCULATOR_REGISTRY_NAME1);
		calculatorService2 = new CalculatorService(1202, registry, CALCULATOR_REGISTRY_NAME2);
		calculatorService3 = new CalculatorService(1203, registry, CALCULATOR_REGISTRY_NAME3);

		calculatorService1.startService();
		calculatorService2.startService();
		calculatorService3.startService();

		// looking up remote references
		calculatorRemote1 = (Calculator) registry.lookup(CALCULATOR_REGISTRY_NAME1);
		calculatorRemote2 = (Calculator) registry.lookup(CALCULATOR_REGISTRY_NAME2);
		calculatorRemote3 = (Calculator) registry.lookup(CALCULATOR_REGISTRY_NAME3);

		referenceList = new ArrayList<Calculator>();
		referenceList.add(calculatorRemote1);
		referenceList.add(calculatorRemote2);
		referenceList.add(calculatorRemote3);

	}

	/**
	 * 
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		calculatorService1.stopService();
		calculatorService2.stopService();
		calculatorService3.stopService();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testConnections() throws Exception {
		int result = calculatorRemote1.add(1, 2);
		assertEquals("wrong result.", 3, result);
		assertTrue("add method not called.", calculatorService1.isAddCalled());
		resetCounters();

		result = calculatorRemote2.add(1, 2);
		assertEquals("wrong result.", 3, result);
		assertTrue("add method not called.", calculatorService2.isAddCalled());
		resetCounters();

		result = calculatorRemote3.add(1, 2);
		assertEquals("wrong result.", 3, result);
		assertTrue("add method not called.", calculatorService3.isAddCalled());
		resetCounters();

	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testHAProxy() throws Exception {

		RoundRobinInvocationStrategyFactory<Calculator> invocationStrategyFactory = 
				new RoundRobinInvocationStrategyFactory<Calculator>(referenceList);

		Invoker invoker = new SimpleHAInvoker(new RMIInvocationResultHandler());
		List<Interceptor> interceptors = new ArrayList<Interceptor>();
		interceptors.add(new HAViewInterceptor());
		interceptors.add(new HATargetSelectorInterceptor<Calculator>(invocationStrategyFactory));
		Calculator calculatorProxy = (Calculator) InterceptorFramework.createInterceptorInvocation(invoker,
				Calculator.class.getClassLoader(), new Class[] { Calculator.class }, interceptors);
		int result = calculatorProxy.add(1, 2);
		assertEquals("wrong result.", 3, result);
		assertTrue("add method not called.", calculatorService1.isAddCalled());
		resetCounters();

		result = calculatorProxy.add(1, 2);
		assertEquals("wrong result.", 3, result);
		assertTrue("add method not called.", calculatorService2.isAddCalled());
		resetCounters();

		float divideResult = calculatorProxy.divide(4, 2);
		assertEquals("wrong result.", (float) 2, divideResult, 0.01);
		assertTrue("divide method not called.", calculatorService3.isDivideCalled());
		resetCounters();

		try {
			calculatorProxy.divide(4, 0);
			fail("should have throws DivideByZeroException.");
		} catch (DivideByZeroException ex) {
			assertTrue("divide method not called.", calculatorService1.isDivideCalled());

		}
		resetCounters();

	}

	/**
	 * Test the proxy while shutting down certain services.
	 * 
	 * @throws Exception
	 */
	public void testHAProxyWithFailover() throws Exception {

		RoundRobinInvocationStrategyFactory<Calculator> invocationStrategyFactory = 
				new RoundRobinInvocationStrategyFactory<Calculator>(referenceList);

		Invoker invoker = new SimpleHAInvoker(new RMIInvocationResultHandler());
		List<Interceptor> interceptors = new ArrayList<Interceptor>();
		interceptors.add(new HAViewInterceptor());
		interceptors.add(new HATargetSelectorInterceptor<Calculator>(invocationStrategyFactory));
		Calculator calculatorProxy = (Calculator) InterceptorFramework.createInterceptorInvocation(invoker,
				Calculator.class.getClassLoader(), new Class[] { Calculator.class }, interceptors);

		int result = calculatorProxy.add(1, 2);
		assertEquals("wrong result.", 3, result);
		assertTrue("add method not called.", calculatorService1.isAddCalled());
		resetCounters();

		// now stop #2 and make sure it failed over to #3
		calculatorService2.stopService();
		result = calculatorProxy.add(1, 2);
		assertEquals("wrong result.", 3, result);
		assertTrue("add method not called.", calculatorService3.isAddCalled());
		resetCounters();

		// call again and we should get #3.
		result = calculatorProxy.add(1, 2);
		assertEquals("wrong result.", 3, result);
		assertTrue("add method not called.", calculatorService3.isAddCalled());
		resetCounters();

		// call again and we should get #1.
		result = calculatorProxy.add(1, 2);
		assertEquals("wrong result.", 3, result);
		assertTrue("add method not called.", calculatorService1.isAddCalled());
		resetCounters();

		// call again and we should get #3.
		result = calculatorProxy.add(1, 2);
		assertEquals("wrong result.", 3, result);
		assertTrue("add method not called.", calculatorService3.isAddCalled());
		resetCounters();

	}

	/**
	 * 
	 * 
	 */
	private void resetCounters() {
		calculatorService1.setAddCalled(false);
		calculatorService2.setAddCalled(false);
		calculatorService3.setAddCalled(false);
	}
}
