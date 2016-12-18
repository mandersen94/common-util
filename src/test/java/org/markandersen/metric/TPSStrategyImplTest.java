package org.markandersen.metric;

import org.junit.Ignore;
import org.markandersen.metric.SingleMetric;
import org.markandersen.metric.TPSStrategy;
import org.markandersen.metric.TPSStrategyImpl;
import org.markandersen.test.BaseTestCase;


public class TPSStrategyImplTest extends BaseTestCase {

	protected TPSStrategy strategy;

	protected TPSStrategyImpl strategyImpl;

	private String metricClassName = "com.fake.faker.fakey";

	private String metricMethodName = "add()";

	private int historySize = 5;

	private long windowSize = 30 * 1000;

	/**
	 * 
	 */
	protected void setUp() throws Exception {
		super.setUp();
		strategyImpl = new TPSStrategyImpl(historySize, windowSize);
		strategy = strategyImpl;
	}

	/**
	 * 
	 * 
	 */
	public void testAddMetric() {

		int smallCount = 3;
		populateMetrics(strategy, smallCount);
		assertEquals(smallCount, strategyImpl.getHistory().size());

		strategyImpl.getHistory().clear();
		populateMetrics(strategy, 10);
		SingleMetric oneMetric = new SingleMetric(metricClassName,
				metricMethodName);
		oneMetric.finish();
		strategyImpl.addMetric(oneMetric);

		assertEquals(historySize, strategyImpl.getHistory().size());
		assertSame(oneMetric, strategyImpl.getHistory().peekFirst());
	}

	/**
	 * 
	 * 
	 */
	public void testAddMetricClearOutOldEntries() throws Exception {
		long shortTime = 1000;
		int elementCount = 4;
		TPSStrategyImpl shortWindowStrategy = new TPSStrategyImpl(historySize,
				shortTime);
		shortWindowStrategy.setGreedyHistoryCleanUp(true);
		populateMetrics(shortWindowStrategy, elementCount);
		Thread.sleep(shortTime + 5000);
		// make sure all the metrics are there.
		assertEquals(elementCount, shortWindowStrategy.getHistory().size());

		// now add one more and this one should be the only one left.
		SingleMetric oneMetric = new SingleMetric("fake", "fake");
		oneMetric.finish();
		shortWindowStrategy.addMetric(oneMetric);
		assertEquals(1, shortWindowStrategy.getHistory().size());
		assertSame(oneMetric, shortWindowStrategy.getHistory().peekFirst());

		// now do the same without greedy cleanup
		shortWindowStrategy = new TPSStrategyImpl(historySize, shortTime);
		shortWindowStrategy.setGreedyHistoryCleanUp(false);
		populateMetrics(shortWindowStrategy, elementCount);
		Thread.sleep(shortTime + 1000);
		// make sure all the metrics are there.
		assertEquals(elementCount, shortWindowStrategy.getHistory().size());

		// now add one more and this one should be the only one left.
		oneMetric = new SingleMetric("fake", "fake");
		oneMetric.finish();
		shortWindowStrategy.addMetric(oneMetric);
		assertEquals(4, shortWindowStrategy.getHistory().size());
		assertSame(oneMetric, shortWindowStrategy.getHistory().peekFirst());

	}

	/**
	 * Calculate the tps for 3 entries.
	 * 
	 */
	@Ignore
	public void testGetTPS() throws Exception {

		SingleMetric oneMetric = new SingleMetric("fake", "fake");
		oneMetric.finish();
		long startTime = oneMetric.getStartTime();
		strategy.addMetric(oneMetric);

		oneMetric = new SingleMetric("fake", "fake");
		oneMetric.finish();
		strategy.addMetric(oneMetric);

		// pause to make sure the start and end times aren't equal.
		Thread.sleep(500);
		oneMetric = new SingleMetric("fake", "fake");
		oneMetric.finish();
		strategy.addMetric(oneMetric);

		// for seconds
		long elapsedTime = (System.currentTimeMillis() - startTime);
		float tpms = ((float) 3) / (elapsedTime);
		float tps = tpms * 1000;
		assertEquals(tps, strategy.getTPS(), 0.001);
	}

	/**
	 * Calculate the tps for 3 entries.
	 * 
	 */
	public void _fix_me_testGetTPSWindowOnly() throws Exception {
		// TODO: fix me.
		long shortWindow = 1000;
		TPSStrategyImpl shortWindowStrategy = new TPSStrategyImpl(historySize,
				shortWindow);
		shortWindowStrategy.setGreedyHistoryCleanUp(false);

		SingleMetric oneMetric = new SingleMetric("fake", "fake");
		oneMetric.finish();
		oneMetric.getStartTime();
		shortWindowStrategy.addMetric(oneMetric);

		Thread.sleep(500);
		oneMetric = new SingleMetric("fake", "fake");
		oneMetric.finish();
		shortWindowStrategy.addMetric(oneMetric);

		// pause to make sure the start and end times aren't equal.
		Thread.sleep(500);
		oneMetric = new SingleMetric("fake", "fake");
		oneMetric.finish();
		shortWindowStrategy.addMetric(oneMetric);

		// for seconds
		float tpms = ((float) 3) / windowSize;
		float tps = tpms * 1000;
		assertEquals(tps, shortWindowStrategy.getTPSWindowOnly(), 0.001);
	}

	/**
	 * 
	 */
	private static void populateMetrics(TPSStrategy theStrategy, int count) {
		for (int i = 0; i < count; i++) {
			SingleMetric metricLoop = new SingleMetric("fake", "fake");
			metricLoop.finish();
			theStrategy.addMetric(metricLoop);
		}
	}

}
