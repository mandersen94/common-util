package org.markandersen.time;

/**
 * 
 * @author mandersen
 */
public class TimeUtil {

	/**
	 * Returns the elapsed time in milliseconds.
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String format(long startTime, long endTime) {
		long delta = endTime - startTime;
		return delta + " ms";
	}

	/**
	 * Returns the time elapsed in milliseconds.
	 * 
	 * @return
	 */
	public static String format(long timeElapsed) {
		return timeElapsed + " ms";
	}

}
