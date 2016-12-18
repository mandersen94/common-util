package org.markandersen.service.invocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory for InvocationStrategyInstance's that round robin though all the
 * objects.
 * 
 * @author Mark Andersen
 */
public class RoundRobinInvocationStrategyFactory<T> extends
		BaseInvocationStrategyFactory<T> implements
		InvocationStrategyInstanceFactory<T> {

	/**
	 * Default constructor.
	 */
	public RoundRobinInvocationStrategyFactory() {
		super();
	}

	/**
	 * @see BaseInvocationStrategyFactory#BaseInvocationStrategyFactory(List)
	 */
	public RoundRobinInvocationStrategyFactory(List<T> initialReferences) {
		super(initialReferences);
	}

	/**
	 * Updates the histories and then creates a list with the good objects first
	 * and the bad objects last. Then rotate the good references for round
	 * robin'ing.
	 * 
	 * @return list of object references to use.
	 */
	protected List<T> createReferenceList() {
		updateInvocationHistory();

		List<T> refs = new ArrayList<T>();
		// add the good references first. then add the bad just in case.
		refs.addAll(goodReferences);
		refs.addAll(badReferences);
		// rotate the list one slot for next invocation.
		Collections.rotate(goodReferences, -1);
		return refs;
	}

	/**
	 * Returns a list of good references
	 * 
	 * @return
	 */
	public List<T> calculateGoodReferences() {
		updateInvocationHistory();

		List<T> refs = new ArrayList<T>();
		refs.addAll(goodReferences);
		// rotate the list one slot for next invocation.
		Collections.rotate(goodReferences, -1);
		return refs;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public List<T> calculateBadReferences() {

		List<T> refs = new ArrayList<T>();
		refs.addAll(badReferences);
		return refs;
	}
}