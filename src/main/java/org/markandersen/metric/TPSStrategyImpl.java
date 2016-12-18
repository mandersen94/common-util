package org.markandersen.metric;

import java.util.Date;
import java.util.Iterator;

import edu.emory.mathcs.backport.java.util.Deque;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingDeque;

/**
 * 
 * @author e63582
 */
public class TPSStrategyImpl implements TPSStrategy {

	private static final int DEFAULT_MAX_ELEMENTS = 1000;

	private static final long DEFAULT_WINDOW_SIZE = 60 * 1000; // 60 seconds.

	private Deque history;

	private long windowSize;

	private int maxElements;

	private boolean greedyHistoryCleanUp = false;

	/**
	 * 
	 * @param maxElements
	 */
	public TPSStrategyImpl(int maxElements, long windowSize) {
		history = new LinkedBlockingDeque(maxElements);
		this.windowSize = windowSize;
		this.maxElements = maxElements;
	}

	/**
	 * 
	 * @param maxElements
	 */
	public TPSStrategyImpl() {
		this(DEFAULT_MAX_ELEMENTS, DEFAULT_WINDOW_SIZE);
	}

	/**
	 * 
	 * @param maxElements
	 */
	public TPSStrategyImpl(int maxElements) {
		this(maxElements, DEFAULT_WINDOW_SIZE);
	}

	/**
	 * 
	 */
	public synchronized void addMetric(SingleMetric metric) {

		// new
		if (history.size() >= maxElements) {
			// pull the last one off to create room as the deque is full.
			history.removeLast();
		}

		boolean result = history.offerFirst(metric);
		if (!result) {
			System.out
					.println("WARNING Couldn't add metric element to front of queue.");
		}

		// try to remove stuff outside the tps time window.
		boolean oneItemCleanupResult = cleanOutLastMetricIfNecessary();
		while (oneItemCleanupResult && greedyHistoryCleanUp) {
			// keep removing until we find a metric that is in the window.
			oneItemCleanupResult = cleanOutLastMetricIfNecessary();
		}

	}

	/**
	 * Checks the end of the history deque and removes only one element if it is
	 * outside the window.
	 * 
	 * @return true if an element was removed.
	 */
	private boolean cleanOutLastMetricIfNecessary() {
		SingleMetric oldMetric = (SingleMetric) history.peekLast();
		long windowCutoff = System.currentTimeMillis() - windowSize;
		if ((oldMetric != null) && (oldMetric.getStartTime() < windowCutoff)) {
			// need to remove element.
			history.removeLast();
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public synchronized float getTPS() {
		SingleMetric lastMetric = (SingleMetric) history.peekLast();
		long timePeriodInMiillis = System.currentTimeMillis()
				- lastMetric.getStartTime();
		return calculateTPS(history.size(), timePeriodInMiillis);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public synchronized float getTPSWindowOnly() {
		long windowCutoffTime = System.currentTimeMillis() - windowSize;
		SingleMetric lastMetric = (SingleMetric) history.peekLast();
		Date windowCutoffDate = new Date(windowCutoffTime);
		Date lastMetricStartDate = new Date(lastMetric.getStartTime());
		if ((lastMetric != null)
				&& (windowCutoffTime < lastMetric.getStartTime())) {
			// all the tranactions are within the window. Just use the
			// normal tps.
			return getTPS();
		}

		Iterator<SingleMetric> iterator = history.iterator();
		int count = 0;

		long lastMetricStartTime = System.currentTimeMillis();
		while (iterator.hasNext()) {
			SingleMetric oneMetric = (SingleMetric) iterator.next();
			if (oneMetric.getStartTime() < windowCutoffTime) {
				// is the metric is outside of the window,
				// jump out of loop.
				break;
			} else {
				// metric is in range. count it.
				count++;
			}
		}
		return calculateTPS(count, windowSize);
	}

	/**
	 * 
	 * @param count
	 * @param windowSize2
	 * @return
	 */
	private float calculateTPS(int count, long timeInMillis) {
		int temp = count * 1000; // convert millis to seconds.
		float tps = ((float) temp) / timeInMillis;
		return tps;
	}

	public boolean isGreedyHistoryCleanUp() {
		return greedyHistoryCleanUp;
	}

	public void setGreedyHistoryCleanUp(boolean greedyHistoryCleanUp) {
		this.greedyHistoryCleanUp = greedyHistoryCleanUp;
	}

	public int getMaxElements() {
		return maxElements;
	}

	public void setMaxElements(int maxElements) {
		this.maxElements = maxElements;
	}

	public long getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(long windowSize) {
		this.windowSize = windowSize;
	}

	public Deque getHistory() {
		return history;
	}

	public void setHistory(Deque history) {
		this.history = history;
	}

}
