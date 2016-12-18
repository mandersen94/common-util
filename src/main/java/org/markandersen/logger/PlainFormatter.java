package org.markandersen.logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class PlainFormatter extends Formatter {

	private static final String NEW_LINE = System.getProperty("line.separator");

	@Override
	public String format(LogRecord record) {

		String message = record.getMessage();
		StringBuffer buf = new StringBuffer(message);
		buf.append(NEW_LINE);
		return buf.toString();
	}

}
