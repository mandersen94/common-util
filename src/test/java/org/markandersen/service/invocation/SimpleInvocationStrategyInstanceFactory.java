package org.markandersen.service.invocation;

import org.markandersen.service.invocation.InvocationStrategyInstance;
import org.markandersen.service.invocation.InvocationStrategyInstanceFactory;

/**
 * DOCUMENT ME!
 */
public class SimpleInvocationStrategyInstanceFactory implements
		InvocationStrategyInstanceFactory {

	/**
	 * 
	 */
	private InvocationStrategyInstance realObject;

	/**
	 * Creates a new SimpleInvocationStrategyInstanceFactory object.
	 * 
	 * @param realObject
	 */
	public SimpleInvocationStrategyInstanceFactory(
			InvocationStrategyInstance realObject) {
		this.realObject = realObject;
	}

	/**
	 * 
	 * 
	 */
	public SimpleInvocationStrategyInstanceFactory() {

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public InvocationStrategyInstance getInvocationStrategyInstance() {
		return realObject;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param obj
	 */
	public void reportBadReference(Object obj) {
		// nop
	}

	public InvocationStrategyInstance getRealObject() {
		return realObject;
	}

	public void setRealObject(InvocationStrategyInstance realObject) {
		this.realObject = realObject;
	}

}