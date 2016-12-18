package org.markandersen.threads;

/**
 * Wraps a Runnable and pulls the thread locals on construction. When the
 * runnable is run the thread local is set before the run method is called.
 * 
 * @author Mark Andersen
 */
public class ThreadLocalPropagatorRunnableWrapper implements Runnable {

	private Runnable runnable;

	public Object threadLocals;

	public Object inheritableThreadLocals;

	/**
	 * Saves the Runnable and pulls the thread locals and inheritable thread
	 * local and saves them.
	 * 
	 * @param runnable
	 *            to run with thread locals propagated.
	 */
	public ThreadLocalPropagatorRunnableWrapper(Runnable runnable) {
		this.runnable = runnable;
		threadLocals = ThreadLocalUtil.getThreadLocals();
		inheritableThreadLocals = ThreadLocalUtil.getInheritableThreadLocals();
	}

	/**
	 * Adds saved thread locals and inheritable thread locals to thread, then
	 * run the runnable.
	 */
	public void run() {
		// set thread locals first. then run.
		ThreadLocalUtil.setThreadLocals(threadLocals);
		ThreadLocalUtil.setInheritableThreadLocals(inheritableThreadLocals);
		runnable.run();

	}

}
