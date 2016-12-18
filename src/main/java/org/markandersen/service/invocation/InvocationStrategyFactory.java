package org.markandersen.service.invocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class InvocationStrategyFactory {

	/**
	 * DOCUMENT ME!
	 * 
	 * @param primary
	 * @param secondary
	 * 
	 * @return
	 */
	public static <T> InvocationStrategyInstanceFactory<T> createRoundRobinInstance(
			List<T> primary, List<T> secondary) {

		List<EnhancedInvocationStrategyFactory<T>> factories = new ArrayList<EnhancedInvocationStrategyFactory<T>>();

		if (secondary == null) {
			factories.add(new RoundRobinInvocationStrategyFactory<T>(primary));
		} else {
			factories.add(new RoundRobinInvocationStrategyFactory<T>(primary));
			factories
					.add(new RoundRobinInvocationStrategyFactory<T>(secondary));
		}

		InvocationStrategyInstanceFactory<T> invocationStrategy = new AggregateInvocationStrategyInstanceFactory<T>(
				factories);

		return invocationStrategy;
	}

	/**
	 * Creates a strategy that stays stuck to a particular instance.
	 * 
	 * @param primary
	 * @param secondary
	 * 
	 * @return
	 */
	public static <T> InvocationStrategyInstanceFactory<T> createStickyInstance(
			List<T> primary, List<T> secondary) {

		List<EnhancedInvocationStrategyFactory<T>> factories = new ArrayList<EnhancedInvocationStrategyFactory<T>>();

		if (secondary == null) {
			factories.add(new StickyInvocationStrategyFactory<T>(primary));
		} else {
			factories.add(new StickyInvocationStrategyFactory<T>(primary));
			factories.add(new StickyInvocationStrategyFactory<T>(secondary));
		}

		InvocationStrategyInstanceFactory<T> invocationStrategy = new AggregateInvocationStrategyInstanceFactory<T>(
				factories);
		return invocationStrategy;
	}

	/**
	 * Creates a strategy that uses a local reference unless it is not
	 * available. If there is a secondary list, then the sticky invocation
	 * strategy is used.
	 * 
	 * @param localInstance
	 * @param primary
	 * @param secondary
	 * 
	 * @return
	 */
	public static <T> InvocationStrategyInstanceFactory<T> createPreferLocalInstance(
			T localInstance, List<T> primary, List<T> secondary) {

		List<EnhancedInvocationStrategyFactory<T>> factories = new ArrayList<EnhancedInvocationStrategyFactory<T>>();

		if (secondary == null) {

			factories.add(new PreferLocalInvocationStrategyFactory<T>(
					localInstance, primary));
		} else {

			factories.add(new PreferLocalInvocationStrategyFactory<T>(
					localInstance, primary));
			factories.add(new StickyInvocationStrategyFactory<T>(secondary));
		}

		InvocationStrategyInstanceFactory<T> invocationStrategy = new AggregateInvocationStrategyInstanceFactory<T>(
				factories);
		return invocationStrategy;
	}
}