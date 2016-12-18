package org.markandersen.service.invocation.ha;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.markandersen.invocation.Invocation;
import org.markandersen.invocation.Invoker;
import org.markandersen.service.invocation.InvocationStrategyInstance;
import org.markandersen.service.invocation.InvocationStrategyInstanceFactory;


public class HAInvoker implements Invoker {

	private List<?> targets;

	private InvocationStrategyInstanceFactory<?> invocationFactory;

	public HAInvoker(InvocationStrategyInstanceFactory<?> invocationFactory) {
		this.invocationFactory = invocationFactory;
	}

	/**
	 * 
	 */
	public Object invoke(Invocation invocation) throws Exception {

		InvocationStrategyInstance<?> invocationStrategyInstance = invocationFactory
				.getInvocationStrategyInstance();

		while (true) {
			try {
				Object nextInvocationObject = invocationStrategyInstance
						.getNextInvocationObject();
				Object result = invocation.performCall(nextInvocationObject);

				return result;
			} catch (InvocationTargetException ex) {
				ex.getCause().printStackTrace();
				throw ex;
			}
		}

	}
}
