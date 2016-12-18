package org.markandersen.service.invocation.ha;

import java.rmi.RemoteException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.markandersen.process.ProcessManager;
import org.markandersen.service.invocation.ha.sample.CalculatorCluster;
import org.markandersen.service.invocation.ha.sample.CalculatorRunner;


/**
 * 
 * @author Mark Andersen
 */
public class CalculatorClusterTest {

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

	@BeforeClass
	public static void setUp() throws Exception {

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
	public static void tearDown() {
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
	 * @throws Exception
	 */
	@Test
	public void testStopRestartEachServer() throws Exception {
		// verify they are all up.
		calculatorRunner1.runOne();
		calculatorRunner2.runOne();
		calculatorRunner3.runOne();

		// take down server 1
		cluster.getCalculatorServer1().stop();
		Thread.sleep(300);
		try {
			calculatorRunner1.runOne();
			Assert.fail("should have thrown excpetion");
		} catch (RemoteException ex) {
			// expected outcome.
		}
		// restart server 1 and verify it is up.
		cluster.getCalculatorServer1().start();
		calculatorRunner1.runOne();

		// take down server 2
		cluster.getCalculatorServer2().stop();
		Thread.sleep(300);
		try {
			calculatorRunner2.runOne();
			Assert.fail("should have thrown excpetion");
		} catch (RemoteException ex) {
			// expected outcome.
		}
		// restart server 2 and verify it is up.
		cluster.getCalculatorServer2().start();
		calculatorRunner2.runOne();

		// take down server 3
		cluster.getCalculatorServer3().stop();
		Thread.sleep(300);
		try {
			calculatorRunner3.runOne();
			Assert.fail("should have thrown excpetion");
		} catch (RemoteException ex) {
			// expected outcome.
		}
		// restart server 3 and verify it is up.
		cluster.getCalculatorServer3().start();
		calculatorRunner1.runOne();

	}
}
