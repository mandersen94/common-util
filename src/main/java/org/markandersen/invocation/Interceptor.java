package org.markandersen.invocation;

import java.io.Serializable;

/**
 * 
 * @author mandersen
 */
public abstract class Interceptor implements Serializable {

	/** The next interceptor in the chain. */
	protected Interceptor nextInterceptor;

	/**
	 * Set the next interceptor in the chain.
	 * 
	 * <p>
	 * String together the interceptors We return the passed interceptor to
	 * allow for interceptor1.setNext(interceptor2).setNext(interceptor3)...
	 * constructs.
	 */
	public Interceptor setNext(final Interceptor interceptor) {

		nextInterceptor = interceptor;
		return interceptor;
	}

	public Interceptor getNext() {
		return nextInterceptor;
	}

	/**
	 * 
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	public abstract Object invoke(Invocation invocation) throws Throwable;
}
