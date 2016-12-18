package org.markandersen.logger;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RollingFileLogger {

	private static final int DEFAULT_SIZE = 1 * 1024 * 1024; // 1 MB
	private static final int DEFAULT_COUNT = 3;
	private String filename;
	private int limit;
	private int count;
	private FileHandler handler;
	private Logger logger;
	private Formatter formatter;

	public RollingFileLogger(String filename) {
		this(filename, DEFAULT_SIZE, DEFAULT_COUNT);
	}

	/**
	 * 
	 * @param filename
	 * @param size
	 * @param count
	 */
	public RollingFileLogger(String filename, int size, int count) {
		this.filename = filename;
		this.limit = size;
		this.count = count;

		try {
			logger = Logger.getLogger("theme");
			logger.setUseParentHandlers(false);
			Handler[] handlers = logger.getHandlers();
			handler = new FileHandler(filename, size, count, true);
			formatter = new PlainFormatter();

			handler.setFormatter(formatter);
			handler.setLevel(Level.ALL);
			logger.addHandler(handler);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 
	 * @param message
	 */
	public void log(String message) {

		logger.info(message);
	}
}
