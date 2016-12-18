package org.markandersen.logger;

import org.apache.log4j.Level;

public class Logger extends org.apache.log4j.Logger {

	/**
	 * 
	 * @param name
	 */
	public Logger(String name) {
		super(name);
	}

	/**
	 * Override to give a hook to say log all messages for "x".
	 */
	public Level getEffectiveLevel() {
		boolean shouldLogAll = false;
		if (shouldLogAll) {
			return Level.ALL;
		} else {
			return super.getEffectiveLevel();
		}
	}

}
