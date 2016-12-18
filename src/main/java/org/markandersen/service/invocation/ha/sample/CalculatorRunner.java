package org.markandersen.service.invocation.ha.sample;

import java.rmi.registry.Registry;

import org.markandersen.rmi.RegistryLocator;


public class CalculatorRunner {

	private int registryPort;

	private String registryPath;

	private long waitTime = 5 * 1000;

	private Calculator calculator;

	/**
	 * 
	 * 
	 */
	public void runOne() throws Exception {
		lookupObject();
		int addArg1 = 34;
		int addArg2 = 14;
		System.out.println("executing add.");
		int addResult = calculator.add(addArg1, addArg2);
		System.out.println("add returned.");
	}

	/**
	 * 
	 * 
	 */
	public void runLoop() throws Exception {
		lookupObject();
		while (true) {
			int addArg1 = 5;
			int addArg2 = 6;
			System.out.println("executing add.");
			int addResult = calculator.add(addArg1, addArg2);
			System.out.println("add returned.");
			Thread.sleep(waitTime);
			int multiplyResult = calculator.multiply(addArg1, addArg2);
			System.out.println("multiple returned.");
			Thread.sleep(waitTime);
			float divideResult = calculator.divide(addArg1, addArg2);
			System.out.println("divide returned.");
			Thread.sleep(waitTime);
		}

	}

	/**
	 * @throws Exception
	 * 
	 * 
	 */
	public void lookupObject() throws Exception {
		Registry registry = RegistryLocator.getRegistry(registryPort);
		calculator = (Calculator) registry.lookup(registryPath);

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			System.out
					.println("usage: CalculatorRunner registryPort registryPath");
			System.exit(1);
		}

		try {
			int registryPort = Integer.parseInt(args[0]);
			String registryPath = args[1];

			CalculatorRunner runner = new CalculatorRunner();
			runner.setRegistryPort(registryPort);
			runner.setRegistryPath(registryPath);
			runner.runLoop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Calculator getCalculator() {
		return calculator;
	}

	public void setCalculator(Calculator calculator) {
		this.calculator = calculator;
	}

	public String getRegistryPath() {
		return registryPath;
	}

	public void setRegistryPath(String registryPath) {
		this.registryPath = registryPath;
	}

	public int getRegistryPort() {
		return registryPort;
	}

	public void setRegistryPort(int registryPort) {
		this.registryPort = registryPort;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

}
