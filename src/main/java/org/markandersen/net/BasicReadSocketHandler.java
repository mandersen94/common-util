package org.markandersen.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class BasicReadSocketHandler implements SocketHandler {

	private byte[] byteArray;

	private int totalBytesRead;

	private int soTimeout = 30 * 1000; // 30 seconds

	public BasicReadSocketHandler(int maxSize) {
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

	/**
	 * 
	 * @return
	 */
	public byte[] getTrimmedByteArray() {
		byte[] bytes = new byte[totalBytesRead];
		System.arraycopy(byteArray, 0, bytes, 0, totalBytesRead);
		return bytes;
	}

	public byte[] getByteArray() {
		return byteArray;
	}

	public void setByteArray(byte[] byteArray) {
		this.byteArray = byteArray;
	}

	public int getBytesRead() {
		return totalBytesRead;
	}

	public void setBytesRead(int bytesRead) {
		this.totalBytesRead = bytesRead;
	}

}
