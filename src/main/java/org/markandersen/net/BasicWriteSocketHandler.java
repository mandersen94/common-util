package org.markandersen.net;

import java.io.OutputStream;
import java.net.Socket;

import org.markandersen.io.IOUtils;


/**
 * 
 * @author mandersen
 */
public class BasicWriteSocketHandler implements SocketHandler {

	private byte[] bytes;

	public BasicWriteSocketHandler(byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * 
	 */
	public void handleSocket(Socket clientSocket) {
		OutputStream outputStream = null;
		try {
			outputStream = clientSocket.getOutputStream();
			outputStream.write(bytes);
			outputStream.flush();
		} catch (Exception e) {
			IOUtils.closeQuietly(outputStream);
			IOUtils.closeQuietly(clientSocket);
		}
	}
}
