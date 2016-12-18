package org.markandersen.service.invocation;

/**
 * Represents if an object is in "good" status and if not when was the time when
 * it had its failure.
 */
public class InvocationObjectHistory<T> {

	/**
	 * 
	 */
	private T obj;

	/**
	 * 
	 */
	private long badInvocationTime = -1;

	/**
	 * 
	 */
	private boolean objectGood = true;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param object
	 *            to remember the history of.
	 */
	public InvocationObjectHistory(T object) {
		obj = object;
	}

	/**
	 * call to record a bad invocation. This method will set the status of the
	 * object to "bad" if it isn't already set and if set the invocation time.
	 */
	public void badInvocation() {

		if (objectGood) {
			// only set the bad invocation time if the object was originally
			// good.
			badInvocationTime = System.currentTimeMillis();
			objectGood = false;
		}
	}

	/**
	 * set the status to good and reset the timestamp.
	 */
	public void enableGoodStatus() {
		objectGood = true;
		badInvocationTime = -1;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public long getBadInvocationTime() {
		return badInvocationTime;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public T getObj() {
		return obj;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public boolean isObjectGood() {
		return objectGood;
	}
}