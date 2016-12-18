package org.markandersen.metric;

public interface OperationStatisticsMBean {

	public int getAverageElapsedTimeCurrent();

	public int getAverageElapsedTimeOverall();

	public long getCounterOverall();

	public long getCounterCurrent();

	public int getMaximumElapsedTimeCurrent();

	public int getMaximumElapsedTimeOverall();

	public int getMinimumElapsedTimeCurrent();

	public int getMinimumElapsedTimeOverall();

	public void resetCurrentStats();

	public float getTPS();

	public void setTpsWindowInMillis(int tpsWindow);

	public int getTpsWindowInMillis();

	public int getMetricHistorySize();

	public void setMetricHistorySize(int metricQueueHistorySize);

	public boolean isGreedyHistoryCleanUp();

	public void setGreedyHistoryCleanUp(boolean greedyHistoryCleanUp);

}