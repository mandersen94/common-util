package org.markandersen.invocation;

import org.markandersen.invocation.Interceptor;
import org.markandersen.invocation.Invocation;

/**
 * 
 * @author e63582
 */
public class StubInterceptor extends Interceptor {

	private boolean invoked = false;

	/**
	 * 
	 * @see org.markandersen.invocation.Interceptor#invoke(org.markandersen.invocation.Invocation)
	 */
	@Override
	public Object invoke(Invocation invocation) throws Throwable {
		invoked = true;
		return getNext().invoke(invocation);
	}

	public boolean isInvoked() {
		return invoked;
	}

	public void setInvoked(boolean invoked) {
		this.invoked = invoked;
	}

}
