// %1144179518478:%
package org.markandersen.service.invocation;

import java.util.List;

/**
 * DOCUMENT ME!
 */
public class InvocationStrategyInstanceImpl<T> implements
		InvocationStrategyInstance<T> {

	private static final String LF = System.getProperty("line.separator");

	/**
	 * 
	 */
	private InvocationStrategyInstanceFactory<T> factory;

	/**
	 * 
	 */
	private List<T> references;

	/**
	 * 
	 */
	private int index = 0;

	/**
	 * Creates a new InvocationStrategyImpl object.
	 * 
	 * @param strategyFactory
	 * @param objectReferences
	 */
	public InvocationStrategyInstanceImpl(
			InvocationStrategyInstanceFactory<T> strategyFactory,
			List<T> objectReferences) {
		factory = strategyFactory;
		references = objectReferences;
	}

	/**
	 * Gets the next available object in the list.
	 * 
	 * @return
	 */
	public T getNextInvocationObject() {

		if (index < references.size()) {

			T obj = references.get(index);
			index++;
			return obj;
		} else {
			// out of elements
			return null;
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param object
	 */
	public void addBadInvocation(T object) {
		factory.reportBadReference(object);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public List<T> getReferences() {
		return references;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("InvocationStrategyInstanceImpl: index = ").append(index);
		buf.append(", references = ").append(references);
		return buf.toString();
	}

	/**
	 * 
	 * 
	 */
	public String prettyToString() {

		StringBuffer buf = new StringBuffer();
		buf.append("InvocationStrategyInstanceImpl: ").append(LF);
		buf.append("index = ").append(index).append(LF);
		buf.append("references:").append(LF);
		for (int i = 0; i < references.size(); i++) {
			T ref = references.get(i);
			buf.append(i + 1).append(". ").append(ref).append(LF);
		}
		return buf.toString();
	}
}