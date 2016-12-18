package org.markandersen.service.invocation.ha;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 
 * @author mandersen
 */
public class ServiceLocatorInterceptor implements MethodInterceptor {

	private ViewPropagationStrategy viewUpdateStrategy;

	private ServiceLocator serviceLocator;

	/**
	 * Before the method invocation, we ask the ViewUpdateStrategy to possibly
	 * add the view id to the request.
	 * 
	 * After the invocation, we see if there is a ViewUpdate which will contain
	 * upated view information or a new view id which will indicate that we need
	 * to get a new view from the service locator.
	 * 
	 * 
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		View view = getView();
		viewUpdateStrategy.addViewToRequest(view);
		try {
			return invocation.proceed();
		} finally {

		}
	}

	/**
	 * 
	 * @return
	 */
	private View getView() {
		return null;
	}

}
