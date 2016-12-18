package org.markandersen.metric;

import edu.emory.mathcs.backport.java.util.Deque;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingDeque;

/**
 * 
 * @author e63582
 */
public class OperationStatistics implements OperationStatisticsMBean {

	private static final int MAX_HISTORY_SIZE = 1000; // retain 1 thousands

	// items.
	private String metricClassName;

	private String metricMethodName;

	private String operationName;

	private Deque metricHistoryDeque;

	private long counterOverall;

	private long counterCurrent;

	private long sumElapsedTimeOverall;

	private int averageElapsedTimeOverall;

	private int minimumElapsedTimeOverall;

	private int maximumElapsedTimeOverall;

	private long sumElapsedTimeCurrent;

	private int averageElapsedTimeCurrent;

	private int minimumElapsedTimeCurrent;

	private int maximumElapsedTimeCurrent;

	// tps
	private int tpsWindowInMillis = 60 * 1000; // defaults to 60 seconds.

	private int metricHistorySize = MAX_HISTORY_SIZE;

	private boolean greedyHistoryCleanUp = true;

	private TPSStrategy tpsStrategy;

	/**
	 * 
	 * @param metricClassName
	 * @param metricMethodName
	 */
	public OperationStatistics(String metricClassName, String metricMethodName) {
		this.metricClassName = metricClassName;
		this.metricMethodName = metricMethodName;
		this.operationName = metricClassName + "." + metricMethodName;
		metricHistoryDeque = new LinkedBlockingDeque(metricHistorySize);

		averageElapsedTimeOverall = 0;
		sumElapsedTimeOverall = 0;
		minimumElapsedTimeOverall = Integer.MAX_VALUE;
		maximumElapsedTimeOverall = Integer.MIN_VALUE;

		averageElapsedTimeCurrent = 0;
		sumElapsedTimeCurrent = 0;
		minimumElapsedTimeCurrent = Integer.MAX_VALUE;
		maximumElapsedTimeCurrent = Integer.MIN_VALUE;
		tpsStrategy = new TPSStrategyImpl(metricHistorySize);
	}

	/**
	 * 
	 */
	public int hashCode() {
		return operationName.hashCode();
	}

	/**
	 * 
	 */
	public boolean equals(Object obj) {
		return operationName.equals(obj);
	}

	public String getOperationName() {
		return operationName;
	}

	/**
	 * 
	 * @param metric
	 */
	public synchronized void add(SingleMetric metric) {
		updateHistory(metric);
		++counterOverall;
		++counterCurrent;
		sumElapsedTimeOverall += metric.getElapsedTime();
		sumElapsedTimeCurrent += metric.getElapsedTime();
		updateMinimum(metric);
		updateMaximum(metric);
		updateAverage();
	}

	/**
	 * 
	 * @param metric
	 */
	private synchronized void updateHistory(SingleMetric metric) {

		tpsStrategy.addMetric(metric);
		// if (metricHistoryDeque.size() >= metricHistoryDequeSize) {
		// // pull the last one off to create room as the deque is full.
		// metricHistoryDeque.pollLast();
		// }
		//
		// boolean result = metricHistoryDeque.offerFirst(metric);
		// if (result) {
		// System.out
		// .println("WARNING Couldn't add metric element to front of queue.");
		// }
		//
		// // try to remove stuff outside the tps time window.
		// boolean oneItemCleanupResult = cleanOutLastMetricIfNecessary();
		// while (oneItemCleanupResult && greedyHistoryCleanUp) {
		// // keep removing until we find a metric that is in the window.
		// oneItemCleanupResult = cleanOutLastMetricIfNecessary();
		// }

	}

	/**
	 * Checks the end of the history deque and removes only one element if it is
	 * outside the window.
	 * 
	 * @return true if an element was removed.
	 */
	private boolean cleanOutLastMetricIfNecessary() {
		SingleMetric oldMetric = (SingleMetric) metricHistoryDeque.peekLast();
		long windowCutoff = System.currentTimeMillis() - tpsWindowInMillis;
		if ((oldMetric != null) && (oldMetric.getStartTime() < windowCutoff)) {
			// need to remove element.
			metricHistoryDeque.getLast();
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 
	 */
	private void updateAverage() {
		if (counterCurrent == 0) {
			// prevent division by zero.
			averageElapsedTimeCurrent = 0;
		} else {
			averageElapsedTimeCurrent = (int) (sumElapsedTimeCurrent / counterCurrent);
		}

		if (counterOverall == 0) {
			// prevent division by zero.
			averageElapsedTimeOverall = 0;
		} else {
			averageElapsedTimeOverall = (int) (sumElapsedTimeOverall / counterOverall);
		}

	}

	/**
	 * 
	 * @param metric
	 */
	private void updateMinimum(SingleMetric metric) {
		minimumElapsedTimeOverall = Math.min(minimumElapsedTimeOverall,
				(int) metric.getElapsedTime());
		minimumElapsedTimeCurrent = Math.min(minimumElapsedTimeCurrent,
				(int) metric.getElapsedTime());

	}

	/**
	 * 
	 * @param metric
	 */
	private void updateMaximum(SingleMetric metric) {
		maximumElapsedTimeOverall = Math.max(maximumElapsedTimeOverall,
				(int) metric.getElapsedTime());
		maximumElapsedTimeCurrent = Math.max(maximumElapsedTimeCurrent,
				(int) metric.getElapsedTime());

	}

	/**
	 * Resets all the current counters.
	 */
	public synchronized void resetCurrentStats() {
		counterCurrent = 0;
		sumElapsedTimeCurrent = 0;
		averageElapsedTimeCurrent = 0;
		minimumElapsedTimeCurrent = Integer.MAX_VALUE;
		maximumElapsedTimeCurrent = 0;
		// creating a new one in case the previous list was huge (so it is
		// gc'ed).
		metricHistoryDeque = new LinkedBlockingDeque(MAX_HISTORY_SIZE);
	}

	/**
	 * 
	 * @return
	 */
	public synchronized float getTPS() {

		return tpsStrategy.getTPS();

		// long endWindowTime = System.currentTimeMillis();
		// long startWindowTime = endWindowTime - tpsWindowInMillis;
		//
		// for (int i = metricHistoryDeque.size() - 1; i >= 0; i--) {
		// // SingleMetric singleMetric = (SingleMetric) metricHistoryDeque
		// // .get(i);
		// // if (singleMetric.getEndTime() < startWindowTime) {
		// // // this metric is the last metric before the window starts.
		// // int numberOfTransactions = (metricHistoryDeque.size() - 1) - i;
		// // return ((numberOfTransactions * 1000) / (float)
		// // tpsWindowInMillis);
		// }
		// // }
		// return 0;
	}

	// --- getters and setters.

	public int getAverageElapsedTimeCurrent() {
		return averageElapsedTimeCurrent;
	}

	public int getAverageElapsedTimeOverall() {
		return averageElapsedTimeOverall;
	}

	public long getCounterOverall() {
		return counterOverall;
	}

	public long getCounterCurrent() {
		return counterCurrent;
	}

	public int getMaximumElapsedTimeCurrent() {
		return maximumElapsedTimeCurrent;
	}

	public int getMaximumElapsedTimeOverall() {
		return maximumElapsedTimeOverall;
	}

	public int getMinimumElapsedTimeCurrent() {
		return minimumElapsedTimeCurrent;
	}

	public int getMinimumElapsedTimeOverall() {
		return minimumElapsedTimeOverall;
	}

	public String getMetricClassName() {
		return metricClassName;
	}

	public String getMetricMethodName() {
		return metricMethodName;
	}

	public int getTpsWindowInMillis() {
		return tpsWindowInMillis;
	}

	public void setTpsWindowInMillis(int tpsWindowInMillis) {
		this.tpsWindowInMillis = tpsWindowInMillis;
	}

	public int getMetricHistorySize() {
		return metricHistorySize;
	}

	public void setMetricHistorySize(int metricHistorySize) {
		this.metricHistorySize = metricHistorySize;
	}

	public boolean isGreedyHistoryCleanUp() {
		return greedyHistoryCleanUp;
	}

	public void setGreedyHistoryCleanUp(boolean greedyHistoryCleanUp) {
		this.greedyHistoryCleanUp = greedyHistoryCleanUp;
	}

}
