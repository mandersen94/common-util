package org.markandersen.invocation;

/**
 * 
 * @author Mark Andersen
 */
public interface Invoker {

	/**
	 * 
	 * @param invocation
	 * @return
	 * @throws Exception
	 */
	public Object invoke(Invocation invocation) throws Throwable;
}
