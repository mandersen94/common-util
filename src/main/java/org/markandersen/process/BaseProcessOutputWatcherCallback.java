package org.markandersen.process;

public abstract class BaseProcessOutputWatcherCallback implements
		ProcessOutputWatcherCallback {

	private Object lock = new Object();

	private volatile boolean serverStarted = false;

	/**
	 * called by the output reader thread.
	 */
	public void outputWritten(String line) {
		if (line.indexOf(getLogMessageMatch()) > -1) {
			// server is started.
			synchronized (lock) {
				serverStarted = true;
				lock.notifyAll();
			}
		}
	}

	/**
	 * 
	 * @param timeout
	 * @return
	 * @throws InterruptedException
	 */
	public boolean waitUntilStarted(long timeout) throws InterruptedException {
		synchronized (lock) {
			lock.wait(timeout);
			return serverStarted;
		}
	}

}
