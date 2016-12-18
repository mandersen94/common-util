package org.markandersen.service.timeout;

import java.lang.reflect.Method;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 */
public interface RequestTimeoutHelper {

	/**
	 * DOCUMENT ME!
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	long getTimeToWait(Object proxy, Method method, Object[] args);

	/**
	 * DOCUMENT ME!
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	TimeUnit getTimeUnit(Object proxy, Method method, Object[] args);

	/**
	 * DOCUMENT ME!
	 * 
	 * @param ex
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	Object overrideException(Exception ex, Object proxy, Method method,
			Object[] args);

	/**
	 * Return true if Thread.interrupt() should be called when the task is going
	 * to be cancelled.
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	boolean getShouldInterruptOnCancel(Object proxy, Method method,
			Object[] args);

	/**
	 * 
	 * @param proxy
	 * @param method
	 * @param args
	 * 
	 * @return
	 */
	boolean getShouldOverrideException(Object proxy, Method method,
			Object[] args);
}