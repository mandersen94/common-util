package org.markandersen.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author mandersen
 */
public class InterceptorFramework implements InvocationHandler {

	protected final static Object[] EMPTY_ARGS = new Object[0];

	protected InvocationContext invocationContext;

	/** The first interceptor in the chain. */
	protected Interceptor next;

	public InterceptorFramework(InvocationContext context) {
		this.invocationContext = context;
	}

	/**
	 * 
	 * @param interceptors
	 * @return
	 */
	public static Object createInterceptorInvocationPOJO(Object target,
			ClassLoader classLoader, Class<?>[] interfaces,
			List<Interceptor> interceptors) {
		return createInterceptorInvocation(new POJOInvoker(target),
				classLoader, interfaces, interceptors);
	}

	/**
	 * 
	 * @param targetMock
	 * @param classLoader
	 * @param interfaces
	 * @param interceptors
	 * @return
	 */
	public static Object createInterceptorInvocation(Invoker invoker,
			ClassLoader classLoader, Class<?>[] interfaces,
			List<Interceptor> interceptors) {
		if (interceptors == null) {
			interceptors = new ArrayList<Interceptor>();
		}

		// create a context.
		InvocationContext context = new InvocationContext();
		context.setInvoker(invoker);

		InterceptorFramework handler = new InterceptorFramework(context);

		// add invoker interceptor to the end.
		interceptors.add(new InvokerInterceptor());

		handler.setInterceptors(interceptors);
		Object proxy = Proxy.newProxyInstance(classLoader, interfaces, handler);
		return proxy;
	}

	/**
	 * 
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// Normalize args to always be an array
		// Isn't this a bug in the proxy call??
		if (args == null)
			args = EMPTY_ARGS;

		// Create the invocation object
		Invocation invocation = new Invocation();

		// Contextual information for the interceptors
		invocation.setInvocationContext(invocationContext);
		invocation.setMethod(method);
		invocation.setArguments(args);

		// send the invocation down the client interceptor chain
		Object obj = next.invoke(invocation);
		return obj;
	}

	/**
	 * 
	 * @return
	 */
	public List<Interceptor> getInterceptors() {
		List<Interceptor> tmp = new ArrayList<Interceptor>();
		Interceptor inext = next;
		while (inext != null) {
			tmp.add(inext);
			inext = inext.nextInterceptor;
		}
		return tmp;
	}

	/**
	 * 
	 * @param interceptors
	 */
	public void setInterceptors(List<Interceptor> interceptors) {
		if (interceptors.size() == 0) {
			return;
		}
		next = (Interceptor) interceptors.get(0);
		Interceptor i = next;
		for (int n = 1; n < interceptors.size(); n++) {
			Interceptor inext = (Interceptor) interceptors.get(n);
			i.setNext(inext);
			i = inext;
		}
	}

	/**
	 * 
	 * @param interceptor
	 * @return
	 */
	public Interceptor setNext(Interceptor interceptor) {
		next = interceptor;

		return interceptor;
	}

}
