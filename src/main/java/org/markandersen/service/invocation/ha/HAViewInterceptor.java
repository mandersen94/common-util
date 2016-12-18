package org.markandersen.service.invocation.ha;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.markandersen.invocation.Interceptor;
import org.markandersen.invocation.Invocation;


/**
 * 
 * 
 */
public class HAViewInterceptor extends Interceptor implements MethodInterceptor {

	/**
	 * 
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			// add view information to thread.
			View view = null;
			ViewContext.setView(view);
			return invocation.proceed();
		} finally {
			// pull out the view information and upate the view.
			View view = ViewContext.getView();

			// update view
			// find the service manager for this object and update it with
			// any new view information.

			// remove view info
			ViewContext.removeView();
		}

	}

	@Override
	public Object invoke(Invocation invocation) throws Throwable {
		try {
			// add view information to thread.
			View view = null;
			ViewContext.setView(view);
			return getNext().invoke(invocation);
		} finally {
			// pull out the view information and upate the view.
			View view = ViewContext.getView();

			// update view
			// find the service manager for this object and update it with
			// any new view information.

			// remove view info
			ViewContext.removeView();
		}
	}

}
