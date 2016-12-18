package org.markandersen.process;

import org.apache.log4j.Logger;

/**
 * 
 * @author Mark Andersen
 */
class ProcessWatcherThread extends Thread {

	private static final Logger logger = Logger
			.getLogger(ProcessWatcherThread.class);

	private Process process;

	private boolean processAlive = true;

	private int exitValue = -1;

	public ProcessWatcherThread(Process process) {
		this.process = process;
		setDaemon(true);
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			processAlive = true;
			// blocks until the process dies.
			logger.debug("starting process monitoring thread for process "
					+ process);
			exitValue = process.waitFor();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		processAlive = false;
	}

	public boolean isProcessAlive() {
		return processAlive;
	}

	public int exitValue() {
		return exitValue;
	}
}