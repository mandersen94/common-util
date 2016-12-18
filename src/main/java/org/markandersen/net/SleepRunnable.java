package org.markandersen.net;

public class SleepRunnable implements Runnable {

	private long sleepTime;

	public SleepRunnable(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public void run() {
		try {
			Thread.sleep(sleepTime);
		} catch (Exception ex) {
			System.err.println("sleep interrupted.");
			ex.printStackTrace();
		}
	}

}
