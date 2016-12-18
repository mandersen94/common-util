package org.markandersen.metric;

/**
 * Represents a single metric.
 * 
 * @author e63582
 */
public class SingleMetric {

	private String metricName;

	private String metricClassName;

	private String metricMethodName;

	private long startTime;

	private long endTime;

	/**
	 * 
	 * @param metricName
	 */
	public SingleMetric(String metricClassName, String metricMethodName) {
		this.metricClassName = metricClassName;
		this.metricMethodName = metricMethodName;
		this.startTime = System.currentTimeMillis();
	}

	/**
	 * Update the time the metric finished.
	 */
	public void finish() {
		endTime = System.currentTimeMillis();
	}

	public long getElapsedTime() {
		return endTime - startTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getMetricClassName() {
		return metricClassName;
	}

	public String getMetricMethodName() {
		return metricMethodName;
	}

	public String getMetricName() {
		return metricClassName + "." + metricMethodName;
	}

}
