package org.markandersen.net;

import java.io.OutputStream;
import java.net.Socket;

import org.markandersen.io.IOUtils;


/**
 * 
 * @author mandersen
 */
public class WriteTimeoutSocketHandler implements SocketHandler {

	protected byte[] bytes;

	protected long timeToWait;

	public WriteTimeoutSocketHandler(byte[] bytes, long timeToWait) {
		this.bytes = bytes;
		this.timeToWait = timeToWait;
	}

	/**
	 * 
	 */
	public void handleSocket(Socket clientSocket) {
		OutputStream os = null;
		try {
			os = clientSocket.getOutputStream();
			os.write(bytes);
			Thread.sleep(timeToWait);
		} catch (Exception e) {
			// nop
		} finally {
			IOUtils.closeQuietly(os);
		}

	}

}
