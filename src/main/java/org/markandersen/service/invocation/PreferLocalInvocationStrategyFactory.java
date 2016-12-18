package org.markandersen.service.invocation;

import java.util.ArrayList;
import java.util.List;

/**
 * DOCUMENT ME!
 */
public class PreferLocalInvocationStrategyFactory<T> extends
		BaseInvocationStrategyFactory<T> {

	/**
	 * 
	 */
	protected T localObject;

	/**
	 * DOCUMENT ME!
	 * 
	 * @param preferLocalObject
	 * @param initialReferences
	 */
	public PreferLocalInvocationStrategyFactory(T preferLocalObject,
			List<T> initialReferences) {
		super(initialReferences);
		localObject = preferLocalObject;

		// if they didn't included the preferLocalObject in the list,
		// add it at the beginning.
		if (!allKnownReferences.contains(preferLocalObject)) {
			addNewObject(preferLocalObject);
		}

		if (goodReferences.contains(preferLocalObject)) {

			int index = goodReferences.indexOf(preferLocalObject);

			if (index != 0) {
				goodReferences.remove(index);
				goodReferences.add(0, preferLocalObject);
			}
		}
	}

	/**
	 * Updates the histories and then creates a list with the good objects first
	 * and the bad objects last.
	 * 
	 * @return list of object references to use.
	 */
	protected synchronized List<T> createReferenceList() {
		checkList();

		List<T> refs = new ArrayList<T>();
		refs.addAll(goodReferences);
		refs.addAll(badReferences);
		return refs;
	}

	/**
	 * 
	 */
	private void checkList() {
		updateInvocationHistory();

		// if the localObject is in the good list and not first, make it
		// first.
		int localObjectIndex = goodReferences.indexOf(localObject);

		if ((goodReferences.contains(localObject)) && (localObjectIndex != 0)) {
			goodReferences.remove(localObjectIndex);
			goodReferences.add(0, localObject);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public List<T> calculateGoodReferences() {
		checkList();

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