package org.markandersen.invocation;

/**
 * 
 * @author Mark Andersen
 */
public class InvokerInterceptor extends Interceptor {

	private static final long serialVersionUID = 4859272601765372733L;

	/**
	 * 
	 * @see org.markandersen.invocation.Interceptor#invoke(org.markandersen.invocation.Invocation)
	 */
	@Override
	public Object invoke(Invocation invocation) throws Throwable {

		InvocationContext ctx = invocation.getInvocationContext();
		Invoker invoker = ctx.getInvoker();
		return invoker.invoke(invocation);
	}

}
