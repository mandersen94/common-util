package org.markandersen.service.invocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 */
public abstract class BaseInvocationStrategyFactory<T> implements
		EnhancedInvocationStrategyFactory<T> {

	private static Logger logger = LoggerFactory
			.getLogger(BaseInvocationStrategyFactory.class);

	/**
	 * clean list of references
	 */
	protected List<T> references;

	/**
	 * all known references wrapped with history objects
	 */
	protected List<T> allKnownReferences;

	/**
	 * good references.
	 */
	protected List<T> goodReferences;

	/**
	 * bad references.
	 */
	protected List<T> badReferences;

	/** maps objects to there histories. */
	protected Map<T, InvocationObjectHistory<T>> objectHistory;

	/**
	 * 
	 */
	protected long objectRetryDelay = 5 * 60 * 1000;

	/**
	 * Creates a instance that isn't initialized.
	 * 
	 * @param initialReferences
	 */
	public BaseInvocationStrategyFactory() {
		super();
	}

	/**
	 * Intializes the goodReference list, badReference list, and the object
	 * history map. All references are considered good at the start. The List
	 * parameter is shallowed copy into internal lists.
	 * 
	 * @param initialReferences
	 */
	public BaseInvocationStrategyFactory(List<T> initialReferences) {
		super();
		this.references = initialReferences;
		init();
	}

	/**
	 * Initialize the objects.
	 * 
	 * @param initialReferences
	 */
	public void init() {
		// make a copy.
		allKnownReferences = new ArrayList<T>();
		goodReferences = new ArrayList<T>();
		objectHistory = new HashMap<T, InvocationObjectHistory<T>>();

		Iterator<T> iterator = references.iterator();
		while (iterator.hasNext()) {
			T obj = iterator.next();
			InvocationObjectHistory<T> info = new InvocationObjectHistory<T>(
					obj);
			allKnownReferences.add(obj);
			goodReferences.add(obj);
			objectHistory.put(obj, info);
		}
		// no bad references yet.
		badReferences = new ArrayList<T>();
	}

	/**
	 * Creates a list of references with the good objects first, and then the
	 * "bad" objects. The same good object should be first until it fails.
	 * 
	 * @return
	 */
	public synchronized InvocationStrategyInstance<T> getInvocationStrategyInstance() {

		List<T> references = createReferenceList();
		InvocationStrategyInstanceImpl<T> impl = new InvocationStrategyInstanceImpl<T>(
				this, references);
		return impl;
	}

	/**
	 * Iterate through the objectHistory looking for objects that are bad but
	 * should be reset to good (based on how long it has been since they were
	 * last tried).
	 */
	protected synchronized void updateInvocationHistory() {

		logger.debug("updateInvocationHistory entered.");
		Iterator<InvocationObjectHistory<T>> iterator = objectHistory.values()
				.iterator();

		while (iterator.hasNext()) {
			InvocationObjectHistory<T> history = iterator.next();
			T ref = history.getObj();
			if (!history.isObjectGood()) {
				logger
						.debug(
								"updateInvocationHistory: evaluating bad ref {} to see if it should be put back in the list.",
								ref);
				long now = System.currentTimeMillis();
				long badInvocationTime = history.getBadInvocationTime();
				long objectRetryTime = badInvocationTime + objectRetryDelay;

				if (now > objectRetryTime) {
					logger.debug("updateInvocationHistory: bad ref {} being put back in the good list.",
							ref);
					renewObject(ref);
				}
			}
		}
		logger.debug("updateInvocationHistory exit.");
	}

	/**
	 * Make an object "good" again so it is moved out of the bad list.
	 * 
	 * @param obj
	 */
	public synchronized void renewObject(T obj) {

		InvocationObjectHistory<T> history = (InvocationObjectHistory<T>) objectHistory
				.get(obj);
		history.enableGoodStatus();
		// add to the good list(at the end) and remove from the bad.
		goodReferences.add(history.getObj());
		badReferences.remove(history.getObj());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param obj
	 */
	public synchronized void reportBadReference(T obj) {

		if (obj == null) {
			return;
		}

		if (!allKnownReferences.contains(obj)) {
			// object isn't from this invocation strategy factory.
			return;
		}

		boolean wasPresent = goodReferences.remove(obj);

		if (!badReferences.contains(obj)) {
			badReferences.add(obj);
		}

		InvocationObjectHistory<T> history = (InvocationObjectHistory<T>) objectHistory
				.get(obj);
		history.badInvocation();
	}

	/**
	 * add a new object to all the lists/maps.
	 * 
	 * @param obj
	 */
	public synchronized void addNewObject(T obj) {

		if (!allKnownReferences.contains(obj)) {
			allKnownReferences.add(obj);
		}

		if (!goodReferences.contains(obj)) {
			goodReferences.add(obj);
		}

		if (!objectHistory.containsKey(obj)) {

			InvocationObjectHistory<T> info = new InvocationObjectHistory<T>(
					obj);
			objectHistory.put(obj, info);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	protected abstract List<T> createReferenceList();

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public List<T> getAllKnownReferences() {
		return allKnownReferences;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public List<T> getBadReferences() {
		return badReferences;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public List<T> getGoodReferences() {
		return goodReferences;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public Map<T, InvocationObjectHistory<T>> getObjectHistory() {
		return objectHistory;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return the objectRetryDelay
	 */
	public long getObjectRetryDelay() {
		return objectRetryDelay;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param objectRetryDelay
	 *            the objectRetryDelay to set
	 */
	public void setObjectRetryDelay(long objectRetryDelay) {
		this.objectRetryDelay = objectRetryDelay;
	}

	public List<T> getReferences() {
		return references;
	}

	public void setReferences(List<T> references) {
		this.references = references;
	}

}