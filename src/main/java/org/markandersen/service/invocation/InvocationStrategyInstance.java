// %1144179518431:%
package org.markandersen.service.invocation;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 */
public interface InvocationStrategyInstance<T> {

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	T getNextInvocationObject();

	/**
	 * DOCUMENT ME!
	 * 
	 * @param obj
	 */
	void addBadInvocation(T obj);

	/**
	 * 
	 * @return
	 */
	public String prettyToString();
}