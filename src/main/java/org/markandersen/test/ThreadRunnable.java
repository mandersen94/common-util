package org.markandersen.test;

public interface ThreadRunnable {
	public void setup();

	public void teardown();

	public boolean runOne();
}
