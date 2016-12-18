package org.markandersen.rmi;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.markandersen.process.JavaProcessBuilder;
import org.markandersen.process.ProcessManager;
import org.markandersen.process.ProcessOutputWatcherCallback;


/**
 * Class that starts the rmiRegistry
 * 
 * @author e63582
 * 
 */
public class RMIRegistryProcess {

	private int rmiRegistryPort;

	private String processName = "rmiRegistry";

	private static final String MAIN_CLASS = "org.markandersen.rmi.RMIRegistryService";

	private static final long PROCESS_START_WAIT_TIME = 10 * 1000;

	private static final Logger logger = Logger
			.getLogger(RMIRegistryProcess.class);

	private String jarLocation1 = "./target/common-util-1.0-SNAPSHOT.jar";
	
	private String jarLocation2 = "./dist/common-util.jar";
	
	/**
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {

		JavaProcessBuilder pb = new JavaProcessBuilder();
		pb.setBaseDir(new File("."));
		pb.setProcessName(processName);

		pb.addClasspath(jarLocation1);
		pb.addClasspath(jarLocation2);
		
		pb.setMainClass(MAIN_CLASS);
		pb.addJavaProgramArg("" + rmiRegistryPort);

		ProcessOutputWatcherCallback callback = new RMIRegistryProcessOutputWatcher();
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
							.info(processName + " started in "
									+ (System.currentTimeMillis() - startTime)
									+ " ms.");
					return;
				} else {
					logger.error(processName + " not started. tried waiting "
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
		processManager.killProcess(processName);
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public int getRmiRegistryPort() {
		return rmiRegistryPort;
	}

	public void setRmiRegistryPort(int rmiRegistryPort) {
		this.rmiRegistryPort = rmiRegistryPort;
	}

}
