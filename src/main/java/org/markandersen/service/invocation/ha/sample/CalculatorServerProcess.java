package org.markandersen.service.invocation.ha.sample;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.markandersen.process.JavaProcessBuilder;
import org.markandersen.process.ProcessManager;
import org.markandersen.process.ProcessOutputWatcherCallback;


public class CalculatorServerProcess {

	private static final String LIB_COMMON_UTIL_JAR1 = "../../target/common-util-1.0-SNAPSHOT.jar";

	private int jmxPort;

	private int rmiRegistryPort;

	private String rmiObjectName = "test";

	private int rmiObjectPort = -1;

	private String processName;

	private static final String MAIN_CLASS = "org.markandersen.service.invocation.ha.sample.CalculatorService";

	private static final long PROCESS_START_WAIT_TIME = 10 * 1000;

	private static final Logger logger = Logger
			.getLogger(CalculatorServerProcess.class);

	/**
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {

		ProcessManager processManager = ProcessManager
				.getDefaultProcessManager();
		Process process = processManager.getProcess(processName);
		if (process != null) {
			// already started :)
			return;
		}

		JavaProcessBuilder pb = new JavaProcessBuilder();
		pb.setBaseDir(new File("./processes/rmiServer1"));
		pb.setProcessName(processName);
		pb.addJavaSystemArg("-Dcom.sun.management.jmxremote.port=" + jmxPort);
		pb
				.addJavaSystemArg("-Dcom.sun.management.jmxremote.authenticate=false");
		pb.addJavaSystemArg("-Dcom.sun.management.jmxremote.ssl=false");

		pb.addClasspath(LIB_COMMON_UTIL_JAR1);
		
		pb.setMainClass(MAIN_CLASS);
		pb.addJavaProgramArg("" + rmiRegistryPort);
		pb.addJavaProgramArg(rmiObjectName);
		if (rmiObjectPort != -1) {
			pb.addJavaProgramArg("" + rmiObjectPort);
		}

		ProcessOutputWatcherCallback callback = new CalculatorServerProcessOutputWatcher();
		pb.setCallback(callback);
		pb.start();
		try {
			synchronized (callback) {
				// wait until your notified
				long startTime = System.currentTimeMillis();
				boolean serverUp = callback
						.waitUntilStarted(PROCESS_START_WAIT_TIME);
				if (serverUp) {
					logger
							.info("Directory " + processName + " started in "
									+ (System.currentTimeMillis() - startTime)
									+ " ms.");
					return;
				} else {
					logger.error("Directory " + processName
							+ " not started. tried waiting "
							+ PROCESS_START_WAIT_TIME);
				}
			}
		} catch (InterruptedException ex) {
		}

	}

	/**
	 * 
	 * 
	 */
	public void stop() {

		ProcessManager processManager = ProcessManager
				.getDefaultProcessManager();
		Process process = processManager.getProcess(processName);
		if (process != null) {
			processManager.killProcess(processName);
		}
	}

	public int getJmxPort() {
		return jmxPort;
	}

	public void setJmxPort(int jmxPort) {
		this.jmxPort = jmxPort;
	}

	public String getRmiObjectName() {
		return rmiObjectName;
	}

	public void setRmiObjectName(String rmiObjectName) {
		this.rmiObjectName = rmiObjectName;
	}

	public int getRmiRegistryPort() {
		return rmiRegistryPort;
	}

	public void setRmiRegistryPort(int rmiRegistryPort) {
		this.rmiRegistryPort = rmiRegistryPort;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public int getRmiObjectPort() {
		return rmiObjectPort;
	}

	public void setRmiObjectPort(int rmiObjectPort) {
		this.rmiObjectPort = rmiObjectPort;
	}

}
