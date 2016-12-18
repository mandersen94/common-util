package org.markandersen.service.invocation.ha;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.markandersen.invocation.Interceptor;
import org.markandersen.invocation.InterceptorFramework;
import org.markandersen.invocation.Invoker;
import org.markandersen.jmx.notification.GenericNotificationListener;
import org.markandersen.process.ProcessManager;
import org.markandersen.service.invocation.RoundRobinInvocationStrategyFactory;
import org.markandersen.service.invocation.ha.HATargetSelectorInterceptor;
import org.markandersen.service.invocation.ha.HAViewInterceptor;
import org.markandersen.service.invocation.ha.SimpleHAInvoker;
import org.markandersen.service.invocation.ha.rmi.RMIInvocationResultHandler;
import org.markandersen.service.invocation.ha.rmi.RMILookupProxy;
import org.markandersen.service.invocation.ha.sample.Calculator;
import org.markandersen.service.invocation.ha.sample.CalculatorCluster;
import org.markandersen.service.invocation.ha.sample.CalculatorRunner;
import org.markandersen.service.invocation.ha.sample.DivideByZeroException;


public class HAProxyTest {

	// common for all servers
	private static int registryPort = 1076;

	private static int jmxPortBase = 5001;

	// server 1 info
	private static int jmxPort1;

	private static String registryPath1;

	// server 2 info
	private static int jmxPort2;

	private static String registryPath2;

	// server 3 info
	private static int jmxPort3;

	private static String registryPath3;

	private static CalculatorRunner calculatorRunner1;

	private static CalculatorRunner calculatorRunner2;

	private static CalculatorRunner calculatorRunner3;

	private static CalculatorCluster cluster;

	private static GenericNotificationListener notificationListener1;

	private static GenericNotificationListener notificationListener2;

	private static GenericNotificationListener notificationListener3;

	private static Calculator calculator1;

	private static Calculator calculator2;

	private static Calculator calculator3;

	private static List<Calculator> references = new ArrayList<Calculator>();

	private Calculator calculatorServiceProxy;

	private long maxWaitTime = 5 * 1000;

	private long objectRetryDelay = 500; // 500 ms for testing

	@BeforeClass
	public static void setUpClass() throws Exception {

		cluster = new CalculatorCluster();
		cluster.setJmxPortBase(jmxPortBase);
		cluster.setRegistryPort(registryPort);
		cluster.start();
		jmxPort1 = cluster.getJmxPort1();
		jmxPort2 = cluster.getJmxPort2();
		jmxPort3 = cluster.getJmxPort3();

		registryPath1 = cluster.getRegistryPath1();
		registryPath2 = cluster.getRegistryPath2();
		registryPath3 = cluster.getRegistryPath3();

		notificationListener1 = cluster.getNotificationListener1();
		notificationListener2 = cluster.getNotificationListener2();
		notificationListener3 = cluster.getNotificationListener3();

		calculatorRunner1 = new CalculatorRunner();
		calculatorRunner1.setRegistryPort(registryPort);
		calculatorRunner1.setRegistryPath(registryPath1);

		calculatorRunner2 = new CalculatorRunner();
		calculatorRunner2.setRegistryPort(registryPort);
		calculatorRunner2.setRegistryPath(registryPath2);

		calculatorRunner3 = new CalculatorRunner();
		calculatorRunner3.setRegistryPort(registryPort);
		calculatorRunner3.setRegistryPath(registryPath3);

	}

	/**
	 * 
	 * 
	 */
	@AfterClass
	public static void tearDownClass() {
		try {
			cluster.stop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			ProcessManager.getDefaultProcessManager().killAllProcesses();
		} catch (Exception ex) {
		}

	}

	/**
	 * 
	 * 
	 */
	@Before
	public void setUp() throws Exception {

		ensureStarted();
		calculator1 = lookupReference(registryPath1);
		calculator2 = lookupReference(registryPath2);
		calculator3 = lookupReference(registryPath3);

		references.add(calculator1);
		references.add(calculator2);
		references.add(calculator3);

		RoundRobinInvocationStrategyFactory<Calculator> invocationStrategyFactory = new RoundRobinInvocationStrategyFactory<Calculator>(
				references);
		invocationStrategyFactory.setObjectRetryDelay(objectRetryDelay);

		Invoker invoker = new SimpleHAInvoker(new RMIInvocationResultHandler());
		List<Interceptor> interceptors = new ArrayList<Interceptor>();
		interceptors.add(new HAViewInterceptor());
		interceptors.add(new HATargetSelectorInterceptor<Calculator>(
				invocationStrategyFactory));
		calculatorServiceProxy = (Calculator) InterceptorFramework
				.createInterceptorInvocation(invoker, Calculator.class
						.getClassLoader(), new Class[] { Calculator.class },
						interceptors);

	}

	/**
	 * @throws IOException
	 * @throws InstanceNotFoundException
	 * @throws MalformedObjectNameException
	 */
	private void ensureStarted() throws IOException,
			MalformedObjectNameException, InstanceNotFoundException {
		cluster.getCalculatorServer1().start();
		notificationListener1.startListening();

		cluster.getCalculatorServer2().start();
		notificationListener2.startListening();

		cluster.getCalculatorServer3().start();
		notificationListener3.startListening();

	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		references.clear();
	}

	/**
	 * 
	 * @param registryPath
	 * @return
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private static Calculator lookupReference(String registryPath)
			throws RemoteException, NotBoundException {
		// Registry registry = RegistryLocator.getRegistry(registryPort);
		// return (Calculator) registry.lookup(registryPath);
		return (Calculator) RMILookupProxy.createProxy("localhost",
				registryPort, registryPath, new Class[] { Calculator.class },
				Calculator.class.getClassLoader());
	}

	/**
	 * 
	 * 
	 */
	@Test
	public void testRoundRobinInvocationStrategyAllServersUp() throws Exception {
		// test server 1
		invokeServer1();

		// test server 2
		invokeServer2();

		// test server 3
		invokeServer3();

		// make sure we went back to server 1.
		invokeServer1();
	}

	/**
	 * 
	 * 
	 */
	@Test
	public void testRoundRobinOneServerDown() throws Exception {

		// hit server 1
		invokeServer1();

		// shutdown server2
		notificationListener2.stopListening();
		cluster.getCalculatorServer2().stop();

		// should fail over to server 3
		invokeServer3();
		long serverFailedTime = System.currentTimeMillis();

		// should be server 3 because that was what the original order was.
		invokeServer3();
		clearAllNotifications();

		// should be server 1 now.
		invokeServer1();

		// start server 2 back up.
		// need to reset the binding. maybe add the lookup to a proxy reference.
		cluster.getCalculatorServer2().start();
		notificationListener2.startListening();

		// should be server 3 now cuz 2 is marked down even though it is up now.
		invokeServer3();

		// waiting for server 2 to get back in the good reference list.
		// it is added at the end though so the order will be 1,3, 2
		long timeSinceServerFailure = System.currentTimeMillis()
				- serverFailedTime;
		if (timeSinceServerFailure < objectRetryDelay) {
			System.out
					.println("sleeping to wait for server 2 to get back in the list.");
			Thread.sleep(objectRetryDelay - timeSinceServerFailure);
		}

		// should be server 1 now.
		invokeServer1();

		// should be server 2 as it should be back in the mix.
		invokeServer2();

		// should be server 3
		invokeServer3();

		// shutdown server1
		notificationListener1.stopListening();
		cluster.getCalculatorServer1().stop();

		// should be server 2
		invokeServer2();

		// should be server 2 again because it was next in the list.
		invokeServer2();

		// should be server 3
		invokeServer3();

		// should be server 2
		invokeServer2();

	}

	/**
	 * 
	 * 
	 */
	@Test
	public void testRoundRobinTwoServerDown() throws Exception {

		// hit server 1
		invokeServer1();

		// shutdown server2
		notificationListener2.stopListening();
		cluster.getCalculatorServer2().stop();

		// shutdown server3
		notificationListener3.stopListening();
		cluster.getCalculatorServer3().stop();

		// should fail over to server 1
		invokeServer1();

		invokeServer1();

		long serverFailedTime = System.currentTimeMillis();

		// should be server 1 now.
		invokeServer1();

		// start server 2 back up.
		// need to reset the binding. maybe add the lookup to a proxy reference.
		cluster.getCalculatorServer2().start();
		notificationListener2.startListening();

		// waiting for server 2 to get back in the good reference list.
		// it is added at the end though so the order will be 1,3, 2
		long timeSinceServerFailure = System.currentTimeMillis()
				- serverFailedTime;
		if (timeSinceServerFailure < (objectRetryDelay + 500)) {
			System.out
					.println("sleeping to wait for server 2 to get back in the list.");
			Thread.sleep(objectRetryDelay - timeSinceServerFailure + 1000);
		}

		// should be server 1 now.
		invokeServer1();

		// should be server 2 as it should be back in the mix.
		invokeServer2();

		// should be server 1.
		invokeServer1();

		// should be server 2 now.
		invokeServer1();

		// // should be server 3
		// invokeServer3();
		//
		// // shutdown server1
		// notificationListener1.stopListening();
		// cluster.getCalculatorServer1().stop();
		//
		// // should be server 2
		// invokeServer2();
		//
		// // should be server 2 again because it was next in the list.
		// invokeServer2();
		//
		// // should be server 3
		// invokeServer3();
		//
		// // should be server 2
		// invokeServer2();
		//
	}

	/**
	 * 
	 * @param arg1
	 * @param arg2
	 * @throws DivideByZeroException
	 * @throws RemoteException
	 */
	private void invokeServer1() throws DivideByZeroException, RemoteException {
		int arg1 = 12;
		int arg2 = 3;
		System.out.println("/*****************/");
		System.out.println("InvokeServer1");
		clearAllNotifications();
		float result = calculatorServiceProxy.divide(arg1, arg2);
		assertEquals(arg1 / (float) arg2, result, 0.01f);
		Notification newNotification = notificationListener1
				.getNewNotification(maxWaitTime);
		if (newNotification == null) {
			if (!notificationListener2.getNotifications().isEmpty()) {
				fail("invoked on server 2 rather than server 1. ");
			} else if (!notificationListener3.getNotifications().isEmpty()) {
				fail("invoked on server 3 rather than server 1. ");
			} else {
				fail("invocation didn't hit any servers.");
			}
		}
		assertTrue(notificationListener2.getNotifications().isEmpty());
		assertTrue(notificationListener3.getNotifications().isEmpty());
		System.out.println("/*****************/");
	}

	/**
	 * 
	 * @param arg1
	 * @param arg2
	 * @throws DivideByZeroException
	 * @throws RemoteException
	 */
	private void invokeServer2() throws Exception {
		int arg1 = 12;
		int arg2 = 3;
		System.out.println("/*****************/");
		System.out.println("InvokeServer2");

		clearAllNotifications();
		float result = calculatorServiceProxy.divide(arg1, arg2);
		assertEquals(arg1 / (float) arg2, result, 0.01f);
		Notification newNotification = notificationListener2
				.getNewNotification(maxWaitTime);
		if (newNotification == null) {
			if (!notificationListener1.getNotifications().isEmpty()) {
				fail("invoked on server 1 rather than server 2. ");
			} else if (!notificationListener3.getNotifications().isEmpty()) {
				fail("invoked on server 3 rather than server 2. ");
			} else {
				fail("invocation didn't hit any servers.");
			}
		}

		assertTrue(notificationListener1.getNotifications().isEmpty());
		assertTrue(notificationListener3.getNotifications().isEmpty());
		System.out.println("/*****************/");
	}

	/**
	 * 
	 * @param arg1
	 * @param arg2
	 * @throws DivideByZeroException
	 * @throws RemoteException
	 */
	private void invokeServer3() throws Exception {
		int arg1 = 12;
		int arg2 = 3;
		System.out.println("/*****************/");
		System.out.println("InvokeServer3");

		clearAllNotifications();
		float result = calculatorServiceProxy.divide(arg1, arg2);
		assertEquals(arg1 / (float) arg2, result, 0.01f);
		Notification newNotification = notificationListener3
				.getNewNotification(maxWaitTime);
		if (newNotification == null) {
			if (!notificationListener1.getNotifications().isEmpty()) {
				fail("invoked on server 1 rather than server 3. ");
			} else if (!notificationListener2.getNotifications().isEmpty()) {
				fail("invoked on server 2 rather than server 3. ");
			} else {
				fail("invocation didn't hit any servers.");
			}
		}
		assertTrue(notificationListener1.getNotifications().isEmpty());
		assertTrue(notificationListener2.getNotifications().isEmpty());
		System.out.println("/*****************/");
	}

	/**
	 * 
	 * 
	 */
	private void clearAllNotifications() {
		notificationListener1.clearNotifications();
		notificationListener2.clearNotifications();
		notificationListener3.clearNotifications();
	}
}
