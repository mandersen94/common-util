package org.markandersen.jmx.notification;

import java.io.IOException;

import javax.management.InstanceNotFoundException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.markandersen.process.ProcessManager;
import org.markandersen.service.invocation.ha.sample.CalculatorCluster;
import org.markandersen.service.invocation.ha.sample.CalculatorRunner;
import org.markandersen.test.BaseTestCase;


/**
 * 
 * @author e63582
 */
public class GenericNotificationServiceTest {

	// common for all servers
	private int registryPort = 1076;

	private int jmxPortBase = 5001;

	// server 1 info
	private int jmxPort1;

	private String registryPath1;

	// server 2 info
	private int jmxPort2;

	private String registryPath2;

	// server 3 info
	private int jmxPort3;

	private String registryPath3;

	private CalculatorRunner calculatorRunner1;

	private CalculatorRunner calculatorRunner2;

	private CalculatorRunner calculatorRunner3;

	private CalculatorCluster cluster;

	@Before
	public void setUp() throws Exception {
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

	@After
	public void tearDown() {
		try {
			cluster.stop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			ProcessManager.getDefaultProcessManager().killAllProcesses();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * @throws IOException
	 * @throws InstanceNotFoundException
	 * @throws Exception
	 * 
	 * 
	 */
	@Test
	public void testJMXNotifications() throws Exception {

		calculatorRunner1.runOne();
		Thread.sleep(100);
		assertEquals("didn't receive notification.", 1, cluster
				.getNotificationListener1().getNotifications().size());

		calculatorRunner2.runOne();
		Thread.sleep(100);
		assertEquals("didn't receive notification from server 2.", 1, cluster
				.getNotificationListener2().getNotifications().size());

		calculatorRunner3.runOne();
		Thread.sleep(100);
		assertEquals("didn't receive notification from server 3.", 1, cluster
				.getNotificationListener3().getNotifications().size());

	}
}
