package org.markandersen.net;

import java.io.OutputStream;
import java.net.Socket;

import org.markandersen.io.IOUtils;


public class WriteThenCloseSocketHandler implements SocketHandler {

	protected byte[] bytes;

	protected long waitTime;

	public WriteThenCloseSocketHandler(byte[] bytes, int waitTime) {
		this.bytes = bytes;
		this.waitTime = waitTime;

	}

	public void handleSocket(Socket clientSocket) {
		OutputStream os = null;
		try {
			os = clientSocket.getOutputStream();
			os.write(bytes);
			Thread.sleep(waitTime);
		} catch (Exception e) {
			// nop
		} finally {
			// clientSocket.shutdownOutput();
			IOUtils.closeQuietly(clientSocket);

		}

	}

}
