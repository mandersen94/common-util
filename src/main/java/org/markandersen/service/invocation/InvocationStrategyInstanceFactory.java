package org.markandersen.service.invocation;

/**
 * 
 */
public interface InvocationStrategyInstanceFactory<T> {

	/**
	 * Gets a new InvocationStrategy for the method call. This is intended to be
	 * called once for every invocation.
	 * 
	 * @return
	 */
	public InvocationStrategyInstance<T> getInvocationStrategyInstance();

	/**
	 * Reports that an object invocation failed.
	 * 
	 * @param obj
	 */
	public void reportBadReference(T obj);
}