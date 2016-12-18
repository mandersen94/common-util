package org.markandersen.process;

/**
 * 
 * @author mandersen
 */
public interface ProcessOutputWatcherCallback {

	/**
	 * 
	 * @param line
	 */
	public void outputWritten(String line);

	/**
	 * 
	 * @param maxWaitTime
	 * @return
	 * @throws InterruptedException
	 */
	public boolean waitUntilStarted(long maxWaitTime)
			throws InterruptedException;

	/**
	 * 
	 * @return
	 */
	public String getLogMessageMatch();

}
