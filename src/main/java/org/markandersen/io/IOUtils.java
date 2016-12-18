package org.markandersen.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class IOUtils {

	publicssss static void closeQuietly(InputStream inputStream) {
		if (inputStream == null) {
			return;
		}
		try {
			inputStream.close();
		} catch (IOException e) {
		}
	}

	public static void closeQuietly(Socket socket) {
		if (socket == null) {
			return;
		}
		try {
			socket.close();
		} catch (IOException e) {
		}
	}

	/**
	 * 
	 * @param outputStream
	 */
	public static void closeQuietly(OutputStream outputStream) {
		if (outputStream == null) {
			return;
		}
		try {
			outputStream.close();
		} catch (IOException e) {
		}
	}

}
