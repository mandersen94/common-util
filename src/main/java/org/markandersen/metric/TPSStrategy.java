package org.markandersen.metric;

/**
 * Stratetgy for calculating the TPS.
 * 
 * @author e63582
 */
public interface TPSStrategy {

	void addMetric(SingleMetric metric);

	float getTPS();

	float getTPSWindowOnly();
}
