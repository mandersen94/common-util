/*
 * Copyright 2007 Tailrank, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.spinn3r.log5j;

import java.util.*;

import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerFactory;

/**
 * 
 * 
 * Logger facade that supports printf style message format for both performance
 * and ease of use and performance and easy constructor suppor to determine the
 * category name by inspection.
 * 
 * http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/Logger.html
 * 
 * The log5j package supports a 'modernized' interface on top of the class Log4j
 * API usage.
 * 
 * It provides a few syntactic extensions thanks to JDK 1.5 (hence the name
 * log5j).
 * 
 * First. It is no long required to give log4j the category when creating a new
 * class level logger. Log5j just figures it out from the call stack.
 * 
 * For example old usage was:
 * 
 * private static final Logger log = Logger.getLogger( FeedTask.class );
 * 
 * and the new syntax with Log5j:
 * 
 * private static final Logger log = new Logger();
 * 
 * Much better and fixes a lot of copy/paste errors.
 * 
 * Second. It provides sprintf support for logging messages
 * 
 * Before:
 * 
 * log.error( "This thing broke: " + foo + " due to bar: " + bar + " on this
 * thing: " + car );
 * 
 * After:
 * 
 * log.error( "This thing broke: %s due to bar: %s on this thing: %s", foo, bar,
 * car );
 * 
 * That is SOOOOOO much better. Good god!
 * 
 * There's also a performance advantage here.
 * 
 * If you were using log.debug() calls with string concat the strings are
 * CONSTANTLY generated even if the debug level is disabled. This burns CPU and
 * pollutes your heap leading to addition garbage collection.
 * 
 * Now internally the log.debug message isn't even called and the string is
 * never expanded/formatted unless the debug level is enabled.
 * 
 */
public class Logger extends org.apache.log4j.Logger {

	/**
	 * Use our own LoggerFactory impl.
	 */
	private final static LoggerFactory loggerFactory = new LoggerFactoryImpl();

	/**
	 * Cache Formatters in ThreadLocal variables for additional performance.
	 */
	private static ThreadLocal<Formatter> formatterCache = new FormatterCache();

	/**
	 * Create a new logger class using the caller's classname as the name of the
	 * category.
	 * 
	 * @deprecated
	 */
	Logger(String name) {
		super(name);
	}

	/**
	 * Obtain a new logger to use with the Log5j system. This is mostly provided
	 * for legacy support.
	 */
	public static Logger getLogger(Class clazz) {
		return getLoggerImpl(clazz.getName());
	}

	/**
	 * Obtain a new logger to use with the Log5j system.
	 */
	public static Logger getLogger() {
		String name = new Exception().getStackTrace()[1].getClassName();
		return getLoggerImpl(name);
	}

	/**
	 * Obtain a new logger to use with the Log5j system. This is mostly provided
	 * for legacy support.
	 */
	private static Logger getLoggerImpl(String name) {

		Logger log = (Logger) org.apache.log4j.Logger.getLogger(name,
				loggerFactory);

		return log;

	}

	public void info(String format, Object... args) {

		if (!isInfoEnabled())
			return;

		super.info(sprintf(format, args));
	}

	public void info(String format, Throwable t, Object... args) {

		if (!isInfoEnabled())
			return;

		super.info(sprintf(format, args), t);
	}

	public void debug(String format, Object... args) {

		if (!isDebugEnabled())
			return;

		super.debug(sprintf(format, args));
	}

	public void debug(String format, Throwable t, Object... args) {

		if (!isDebugEnabled())
			return;

		super.debug(sprintf(format, args), t);
	}

	public void error(String format, Object... args) {

		if (!isEnabledFor(Priority.ERROR))
			return;

		super.error(sprintf(format, args));
	}

	public void error(String format, Throwable t, Object... args) {

		if (!isEnabledFor(Priority.ERROR))
			return;

		super.error(sprintf(format, args), t);
	}

	public void fatal(String format, Object... args) {

		if (!isEnabledFor(Priority.FATAL))
			return;

		super.fatal(sprintf(format, args));
	}

	public void fatal(String format, Throwable t, Object... args) {

		if (!isEnabledFor(Priority.FATAL))
			return;

		super.fatal(sprintf(format, args), t);
	}

	public void warn(String format, Object... args) {

		if (!isEnabledFor(Priority.WARN))
			return;

		super.warn(sprintf(format, args));
	}

	public void warn(String format, Throwable t, Object... args) {

		if (!isEnabledFor(Priority.WARN))
			return;

		super.warn(sprintf(format, args), t);
	}

	/**
	 * Clone of C sprintf support.
	 * 
	 * @see Formatter
	 */
	public static String sprintf(String format, Object... args) {

		Formatter f = getFormatter();
		f.format(format, args);

		StringBuilder sb = (StringBuilder) f.out();
		String message = sb.toString();
		sb.setLength(0);

		return message;

	}

	/**
	 * Interface to cached formatters.
	 */
	private static Formatter getFormatter() {
		return formatterCache.get();
	}

}

/**
 * Used to swap in our log5j impl in place of the log4j one.
 */
class LoggerFactoryImpl implements LoggerFactory {

	public Logger makeNewLoggerInstance(String name) {
		return new com.spinn3r.log5j.Logger(name);
	}

}

class FormatterCache extends ThreadLocal<Formatter> {

	protected synchronized Formatter initialValue() {
		return new Formatter();
	}

}