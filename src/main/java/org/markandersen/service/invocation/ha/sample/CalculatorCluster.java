package org.markandersen.service.invocation.ha.sample;

import java.io.IOException;

import javax.management.JMException;

import org.markandersen.jmx.notification.GenericNotificationListener;
import org.markandersen.rmi.RMIRegistryProcess;


public class CalculatorCluster {

	private CalculatorServerProcess calculatorServer1;

	private CalculatorServerProcess calculatorServer2;

	private CalculatorServerProcess calculatorServer3;

	private GenericNotificationListener notificationListener1;

	private GenericNotificationListener notificationListener2;

	private GenericNotificationListener notificationListener3;

	private int jmxPortBase;

	private int jmxPort1;

	private int jmxPort2;

	private int jmxPort3;

	private String registryPath1 = "calculator1";

	private String registryPath2 = "calculator2";

	private String registryPath3 = "calculator3";

	private int registryPort;

	private RMIRegistryProcess rmiRegistry;

	public void start() throws IOException, JMException {
		System.out.println("Starting RMI registry.");
		rmiRegistry = new RMIRegistryProcess();
		rmiRegistry.setRmiRegistryPort(registryPort);
		rmiRegistry.start();

		System.out.println("Starting server 1.");
		calculatorServer1 = new CalculatorServerProcess();
		calculatorServer1.setProcessName("CalculatorService-1");
		calculatorServer1.setJmxPort(jmxPort1);
		calculatorServer1.setRmiRegistryPort(registryPort);
		calculatorServer1.setRmiObjectName(registryPath1);
		calculatorServer1.start();

		System.out.println("Starting server 2.");
		calculatorServer2 = new CalculatorServerProcess();
		calculatorServer2.setProcessName("CalculatorService-2");
		calculatorServer2.setJmxPort(jmxPort2);
		calculatorServer2.setRmiRegistryPort(registryPort);
		calculatorServer2.setRmiObjectName(registryPath2);
		calculatorServer2.start();

		System.out.println("Starting server 3.");
		calculatorServer3 = new CalculatorServerProcess();
		calculatorServer3.setProcessName("CalculatorService-3");
		calculatorServer3.setJmxPort(jmxPort3);
		calculatorServer3.setRmiRegistryPort(registryPort);
		calculatorServer3.setRmiObjectName(registryPath3);
		calculatorServer3.start();

		// setup notification listeners.
		notificationListener1 = new GenericNotificationListener();
		notificationListener1.setJmxPort(jmxPort1);
		notificationListener1.startListening();

		notificationListener2 = new GenericNotificationListener();
		notificationListener2.setJmxPort(jmxPort2);
		notificationListener2.startListening();

		notificationListener3 = new GenericNotificationListener();
		notificationListener3.setJmxPort(jmxPort3);
		notificationListener3.startListening();

	}

	/**
	 * 
	 * @throws JMException
	 * @throws IOException
	 */
	public void stop() throws JMException, IOException {
		notificationListener1.stopListening();
		notificationListener2.stopListening();
		notificationListener3.stopListening();

		calculatorServer1.stop();
		calculatorServer2.stop();
		calculatorServer3.stop();
	}

	public CalculatorServerProcess getCalculatorServer1() {
		return calculatorServer1;
	}

	public void setCalculatorServer1(CalculatorServerProcess calculatorServer1) {
		this.calculatorServer1 = calculatorServer1;
	}

	public CalculatorServerProcess getCalculatorServer2() {
		return calculatorServer2;
	}

	public void setCalculatorServer2(CalculatorServerProcess calculatorServer2) {
		this.calculatorServer2 = calculatorServer2;
	}

	public CalculatorServerProcess getCalculatorServer3() {
		return calculatorServer3;
	}

	public void setCalculatorServer3(CalculatorServerProcess calculatorServer3) {
		this.calculatorServer3 = calculatorServer3;
	}

	public int getJmxPort1() {
		return jmxPort1;
	}

	public void setJmxPort1(int jmxPort1) {
		this.jmxPort1 = jmxPort1;
	}

	public int getJmxPort2() {
		return jmxPort2;
	}

	public void setJmxPort2(int jmxPort2) {
		this.jmxPort2 = jmxPort2;
	}

	public int getJmxPort3() {
		return jmxPort3;
	}

	public void setJmxPort3(int jmxPort3) {
		this.jmxPort3 = jmxPort3;
	}

	public int getJmxPortBase() {
		return jmxPortBase;
	}

	public void setJmxPortBase(int jmxPortBase) {
		this.jmxPortBase = jmxPortBase;
		jmxPort1 = jmxPortBase;
		jmxPort2 = jmxPortBase + 1;
		jmxPort3 = jmxPortBase + 2;
	}

	public String getRegistryPath1() {
		return registryPath1;
	}

	public void setRegistryPath1(String registryPath1) {
		this.registryPath1 = registryPath1;
	}

	public String getRegistryPath2() {
		return registryPath2;
	}

	public void setRegistryPath2(String registryPath2) {
		this.registryPath2 = registryPath2;
	}

	public String getRegistryPath3() {
		return registryPath3;
	}

	public void setRegistryPath3(String registryPath3) {
		this.registryPath3 = registryPath3;
	}

	public int getRegistryPort() {
		return registryPort;
	}

	public void setRegistryPort(int registryPort) {
		this.registryPort = registryPort;
	}

	public GenericNotificationListener getNotificationListener1() {
		return notificationListener1;
	}

	public void setNotificationListener1(
			GenericNotificationListener notificationListener1) {
		this.notificationListener1 = notificationListener1;
	}

	public GenericNotificationListener getNotificationListener2() {
		return notificationListener2;
	}

	public void setNotificationListener2(
			GenericNotificationListener notificationListener2) {
		this.notificationListener2 = notificationListener2;
	}

	public GenericNotificationListener getNotificationListener3() {
		return notificationListener3;
	}

	public void setNotificationListener3(
			GenericNotificationListener notificationListener3) {
		this.notificationListener3 = notificationListener3;
	}

	public RMIRegistryProcess getRmiRegistry() {
		return rmiRegistry;
	}

	public void setRmiRegistry(RMIRegistryProcess rmiRegistry) {
		this.rmiRegistry = rmiRegistry;
	}

}
