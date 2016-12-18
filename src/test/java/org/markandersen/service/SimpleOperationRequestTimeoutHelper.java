package org.markandersen.service;

import java.lang.reflect.Method;

import org.markandersen.service.timeout.RequestTimeoutHelper;


import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 * DOCUMENT ME!
 */
class SimpleOperationRequestTimeoutHelper implements RequestTimeoutHelper {

	/**
	 * 
	 */
	protected long waitTime = 60 * 1000;

	/**
	 * 
	 */
	protected boolean shouldInterruptOnCancel = false;

	/**
	 * 
	 */
	protected boolean shouldOverrideException = false;

	/**
	 * 
	 */
	protected TimeUnit timeUnit = TimeUnit.MILLISECONDS;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	public long getTimeToWait(Object proxy, Method method, Object[] args) {
		return waitTime;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	public TimeUnit getTimeUnit(Object proxy, Method method, Object[] args) {
		return TimeUnit.MILLISECONDS;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	public boolean getShouldInterruptOnCancel(Object proxy, Method method,
			Object[] args) {
		return shouldInterruptOnCancel;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	public boolean getShouldOverrideException(Object proxy, Method method,
			Object[] args) {
		return shouldOverrideException;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public long getWaitTime() {
		return waitTime;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param waitTime
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public boolean isShouldInterruptOnCancel() {
		return shouldInterruptOnCancel;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param shouldInterruptOnCancel
	 */
	public void setShouldInterruptOnCancel(boolean shouldInterruptOnCancel) {
		this.shouldInterruptOnCancel = shouldInterruptOnCancel;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public boolean isShouldOverrideException() {
		return shouldOverrideException;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param shouldOverrideException
	 */
	public void setShouldOverrideException(boolean shouldOverrideException) {
		this.shouldOverrideException = shouldOverrideException;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param timeUnit
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param originalException
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	public Object overrideException(Exception originalException, Object proxy,
			Method method, Object[] args) {
		throw new IllegalArgumentException("This shouldn't have been called.");
	}
}