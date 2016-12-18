package org.markandersen.net;

import java.io.InputStream;
import java.net.Socket;

import org.markandersen.io.IOUtils;


public class ReadTimeoutSocketHandler implements SocketHandler {

	protected long timeToWait;

	protected int numberBytesToRead = 1000;

	public ReadTimeoutSocketHandler(int numberBytes, long timeToWait) {
		this.numberBytesToRead = numberBytes;
		this.timeToWait = timeToWait;
	}

	/**
	 * 
	 */
	public void handleSocket(Socket clientSocket) {

		InputStream is = null;
		try {
			// set this to the size to make sure we aren't reading bytes in
			// the buffer.
			clientSocket.setReceiveBufferSize(numberBytesToRead);
			is = clientSocket.getInputStream();
			byte[] bytes = new byte[numberBytesToRead];
			int bytesRead = is.read(bytes);
			if (timeToWait > 0) {
				Thread.sleep(timeToWait);
			}
		} catch (Exception e) {
			// nop
		} finally {
			IOUtils.closeQuietly(clientSocket);
		}
	}

}
