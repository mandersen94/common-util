package org.markandersen.metric;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.markandersen.jmx.JMXUtil;
import org.markandersen.util.ClassUtil;


/**
 * 
 * @author e63582
 */
public class MetricManager {

	private static MetricManager singletonInstance;

	private boolean useJMX = false;

	private static Logger logger = Logger.getLogger(MetricManager.class);

	private Map<String, OperationStatistics> operationRecords = Collections
			.synchronizedMap(new HashMap<String, OperationStatistics>());

	/**
	 * 
	 * @return
	 */
	public synchronized static MetricManager getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new MetricManager();
		}
		return singletonInstance;
	}

	/**
	 * Private to enforce singleton.
	 * 
	 */
	private MetricManager() {

	}

	/**
	 * 
	 * @param metric
	 */
	public void record(SingleMetric metric) {

		String name = metric.getMetricName();
		OperationStatistics record = (OperationStatistics) operationRecords
				.get(name);
		if (record == null) {
			record = createNewOperationsRecord(metric);
			// add it to the map for later.
			operationRecords.put(name, record);
		}

		record.add(metric);

	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private OperationStatistics createNewOperationsRecord(SingleMetric metric) {

		OperationStatistics record = new OperationStatistics(metric
				.getMetricClassName(), metric.getMetricMethodName());
		if (useJMX) {
			registerNewMBean(record);
		}
		return record;
	}

	/**
	 * Registers a new mbean for the given operation statistics.
	 * 
	 * @param operationStats
	 */
	private void registerNewMBean(OperationStatistics operationStats) {
		try {
			// need to register it.
			String className = ClassUtil.shortenClassName(operationStats
					.getMetricClassName());
			String methodName = ClassUtil.shortenMethodName(operationStats
					.getMetricMethodName());

			String objectName = "metricManager:className=" + className
					+ ",method=" + methodName;
			JMXUtil.registerMBean(operationStats, objectName);
		} catch (Exception ex) {
			logger.error("Couldnt register OperationStatitics.", ex);
		}
	}

}
