package org.markandersen.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author mandersen
 */
public class Invocation {

	protected InvocationContext invocationContext;

	protected Object[] args;

	protected Method method;

	protected Map<Object, Object> payload;

	/**
	 * 
	 */
	public Invocation() {
		payload = new HashMap<Object, Object>();
	}

	/**
	 * 
	 * @param m
	 * @param args
	 */
	public Invocation(Method m, Object[] args) {
		setMethod(m);
		setArguments(args);
		payload = new HashMap<Object, Object>();
	}

	/**
	 * 
	 * @param target2
	 * @param instance
	 * @param m
	 * @param arguments
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws Exception
	 */
	public Object performCall(Object objectTarget)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		return method.invoke(objectTarget, args);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object getValue(Object key) {
		return payload.get(key);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void setValue(Object key, Object value) {
		payload.put(key, value);
	}

	public Map<Object, Object> getPayload() {
		return payload;
	}

	public void setPayload(Map<Object, Object> payload) {
		this.payload = payload;
	}

	/**
	 * set on method Return the invocation method.
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * get on method Return the invocation method.
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * A list of arguments for the method.
	 */
	public void setArguments(Object[] arguments) {
		this.args = arguments;
	}

	public Object[] getArguments() {
		return this.args;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public InvocationContext getInvocationContext() {
		return invocationContext;
	}

	public void setInvocationContext(InvocationContext ctx) {
		this.invocationContext = ctx;
	}

}
