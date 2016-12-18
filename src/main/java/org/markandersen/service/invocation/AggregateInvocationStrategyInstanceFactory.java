package org.markandersen.service.invocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregates invocation strategies.
 */
public class AggregateInvocationStrategyInstanceFactory<T> implements
		InvocationStrategyInstanceFactory<T> {

	/**
	 * 
	 */
	private List<EnhancedInvocationStrategyFactory<T>> invocationStrategyInstanceFactories;

	/**
	 * Creates a new AggregateInvocationStrategyInstanceFactory object.
	 * 
	 * @param invocationStrategyInstanceFactories
	 */
	public AggregateInvocationStrategyInstanceFactory(
			List<EnhancedInvocationStrategyFactory<T>> invocationStrategyInstanceFactories) {
		this.invocationStrategyInstanceFactories = invocationStrategyInstanceFactories;

		if (invocationStrategyInstanceFactories == null) {
			throw new NullPointerException(
					"invocationStrategyInstanceFactory is null.");
		}
	}

	/**
	 * gets list of good references from each of the factories first and makes
	 * one aggregate good list. Then gets the list of all the bad ones and adds
	 * those after the good ones.
	 * 
	 * @return
	 */
	public InvocationStrategyInstance<T> getInvocationStrategyInstance() {

		List<T> goodList = new ArrayList<T>();
		List<T> badList = new ArrayList<T>();

		for (EnhancedInvocationStrategyFactory<T> temp : invocationStrategyInstanceFactories) {
			List<T> goodReferences = temp.calculateGoodReferences();
			goodList.addAll(goodReferences);

			List<T> badReferences = temp.calculateBadReferences();
			badList.addAll(badReferences);
		}
		goodList.addAll(badList);
		InvocationStrategyInstanceImpl<T> impl = new InvocationStrategyInstanceImpl<T>(
				this, goodList);
		return impl;

	}

	/**
	 * We have to go through and notify each of the invocation factories because
	 * we don't know which one this object came from.
	 * 
	 * @param obj
	 */
	public void reportBadReference(T obj) {
		for (EnhancedInvocationStrategyFactory<T> strategyFactory : invocationStrategyInstanceFactories) {
			strategyFactory.reportBadReference(obj);
		}
	}
}