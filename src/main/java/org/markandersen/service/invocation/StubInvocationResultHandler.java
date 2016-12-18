package org.markandersen.service.invocation;

/**
 * 
 */
public class StubInvocationResultHandler implements InvocationResultHandler {

	/**
	 * 
	 */
	private boolean resultGood = true;

	/**
	 * 
	 */
	private boolean exceptionGood = true;

	/**
	 * Creates a new StubInvocationResultHandler object.
	 * 
	 * @param resultGood
	 * @param exceptionGood
	 */
	public StubInvocationResultHandler(boolean resultGood, boolean exceptionGood) {
		this.resultGood = resultGood;
		this.exceptionGood = exceptionGood;
	}

	/**
	 * 
	 * @param ex
	 * @return
	 */
	public boolean didInvocationSucceedException(Throwable ex) {
		return exceptionGood;
	}

	/**
	 * 
	 * @param result
	 * @return
	 */
	public boolean didInvocationSucceedReturnValue(Object result) {
		return resultGood;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isExceptionGood() {
		return exceptionGood;
	}

	/**
	 * 
	 * @param exceptionGood
	 */
	public void setExceptionGood(boolean exceptionGood) {
		this.exceptionGood = exceptionGood;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public boolean isResultGood() {
		return resultGood;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param resultGood
	 */
	public void setResultGood(boolean resultGood) {
		this.resultGood = resultGood;
	}
}