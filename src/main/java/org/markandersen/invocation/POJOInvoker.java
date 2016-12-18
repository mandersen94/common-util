package org.markandersen.invocation;

/**
 * 
 * @author Mark Andersen
 */
public class POJOInvoker implements Invoker {

	private Object target;

	/**
	 * 
	 * @param target
	 */
	public POJOInvoker(Object target) {
		this.target = target;
	}

	/**
	 * 
	 * @see org.markandersen.invocation.Invoker#invoke(org.markandersen.invocation.Invocation)
	 */
	public Object invoke(Invocation invocation) throws Exception {

		return invocation.performCall(target);
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

}
