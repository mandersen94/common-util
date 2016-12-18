package org.markandersen.service.invocation;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.markandersen.net.SocketHandler;


/**
 * 
 */
public class TestSocketHandler implements SocketHandler {

	private byte[] byteArray;

	private int totalBytesRead;

	private int soTimeout = 30 * 1000; // 30 seconds

	private static final int MAX_BYTES = 1024 * 1024;

	public TestSocketHandler() {
		this(MAX_BYTES);
	}

	public TestSocketHandler(int maxSize) {
		byteArray = new byte[maxSize];
		totalBytesRead = 0;
	}

	public void handleSocket(Socket clientSocket) {
		InputStream inputStream = null;
		try {
			clientSocket.setSoTimeout(soTimeout);
			inputStream = clientSocket.getInputStream();
			int bytesRead = 0;
			while (totalBytesRead < byteArray.length) {
				int len = byteArray.length - totalBytesRead;
				bytesRead = inputStream.read(byteArray, totalBytesRead, len);
				if (bytesRead == -1) {
					System.err.println("received a value of -1 from read.");

					break;
				}
				totalBytesRead = totalBytesRead + bytesRead;
			}
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
				clientSocket.close();
			} catch (Exception ex) {
			}
		}
	}

}
