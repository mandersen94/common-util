package org.markandersen.io;

import java.io.IOException;
import java.io.Writer;

public class CountingWriter {

	private Writer writer;

	private int count;

	public CountingWriter(Writer w) {
		writer = w;
	}

	public void write(String message) throws IOException {
		writer.write(message);
		count += message.length();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void close() throws IOException {
		if (writer != null) {
			writer.close();
		}
	}

}
