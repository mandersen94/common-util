package org.markandersen.service.invocation.ha;

import org.markandersen.invocation.Interceptor;
import org.markandersen.invocation.Invocation;
import org.markandersen.service.invocation.InvocationStrategyInstance;
import org.markandersen.service.invocation.InvocationStrategyInstanceFactory;
import org.markandersen.service.invocation.NoObjectsAvailableException;

public class HATargetSelectorInterceptor<T> extends Interceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4937477702334129813L;

	private InvocationStrategyInstanceFactory<T> invocationStrategyFactory;

	/**
	 * 
	 * @param invocationStrategyFactory
	 */
	public HATargetSelectorInterceptor(
			InvocationStrategyInstanceFactory<T> invocationStrategyFactory) {
		this.invocationStrategyFactory = invocationStrategyFactory;
	}

	/**
	 * 
	 */
	@Override
	public Object invoke(Invocation invocation) throws Throwable {

		InvocationStrategyInstance<T> strategy = invocationStrategyFactory
				.getInvocationStrategyInstance();
		Throwable firstBadException = null;
		System.out.println("got new invocationStrategyInstance. instance = ");
		System.out.println(strategy.prettyToString());
		// we will either return a result here or throw an exception to get out
		// of the
		// loop.
		while (true) {
			T obj = strategy.getNextInvocationObject();

			if (obj == null) {
				// out of real objects to invoke on.
				// throw a runtime exception.
				throw new NoObjectsAvailableException(firstBadException);
			}

			invocation.setValue(HAConstants.TARGET_OBJECT_KEY, obj);
			try {
				System.out.printf("trying to invoke on %s.\n", obj);
				return getNext().invoke(invocation);
			} catch (RequestFailedException ex) {
				// add it to the invocation history and try again.
				strategy.addBadInvocation(obj);
				System.out.printf("Failed invocation for %s.\n", obj);
			} finally {
				// null out the target. just in case.
				invocation.setValue(HAConstants.TARGET_OBJECT_KEY, null);
			}
		}
	}
}
