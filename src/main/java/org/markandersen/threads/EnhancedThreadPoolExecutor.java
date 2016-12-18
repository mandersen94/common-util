package org.markandersen.threads;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * DOCUMENT ME!
 */
public class EnhancedThreadPoolExecutor extends AbstractExecutorService {

	/**
	 * DOCUMENT ME!
	 * 
	 * @param timeout
	 * @param unit
	 * 
	 * @return
	 * 
	 * @throws InterruptedException
	 */
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public boolean isShutdown() {
		return false;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 */
	public void shutdown() {
		// TODO Auto-generated method stub
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public List<Runnable> shutdownNow() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param command
	 */
	public void execute(Runnable command) {
		// TODO Auto-generated method stub
	}

}