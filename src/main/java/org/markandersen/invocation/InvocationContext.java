package org.markandersen.invocation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author mandersen
 */
public class InvocationContext implements Serializable {

	private static final long serialVersionUID = -2557867431576424108L;

	private Map context;

	private Invoker invoker;

	public InvocationContext() {
		context = new HashMap();
	}

	/**
	 * Invocation creation
	 */
	public InvocationContext(final Map context) {
		this.context = context;
	}

	/**
	 * The generic store of variables
	 */
	@SuppressWarnings("unchecked")
	public void setValue(Object key, Object value) {
		context.put(key, value);
	}

	/**
	 * Get a value from the stores.
	 */
	public Object getValue(Object key) {
		return context.get(key);
	}

	public Invoker getInvoker() {
		return invoker;
	}

	public void setInvoker(Invoker invoker) {
		this.invoker = invoker;
	}

}
