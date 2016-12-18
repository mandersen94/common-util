package org.markandersen.service.invocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for InvocationStrategyInstance's that are sticky once a good request
 * is made. All invocations will return to this object until it fails.
 */
public class StickyInvocationStrategyFactory<T> extends
		BaseInvocationStrategyFactory<T> implements
		InvocationStrategyInstanceFactory<T> {

	/**
	 * Intializes the goodReference list, badReference list, and the object
	 * history map. All references are considered good at the start. The List
	 * parameter is shallowed copy into internal lists.
	 * 
	 * @param initialReferences
	 */
	public StickyInvocationStrategyFactory(List<T> initialReferences) {
		super(initialReferences);
	}

	/**
	 * Updates the histories and then creates a list with the good objects first
	 * and the bad objects last.
	 * 
	 * @return list of object references to use.
	 */
	protected List<T> createReferenceList() {
		updateInvocationHistory();

		List<T> refs = new ArrayList<T>();
		// add the good references first. then add the bad just in case.
		refs.addAll(goodReferences);
		refs.addAll(badReferences);
		return refs;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public List<T> calculateGoodReferences() {
		updateInvocationHistory();

		List<T> refs = new ArrayList<T>();
		refs.addAll(goodReferences);
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