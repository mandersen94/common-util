package org.markandersen.service.invocation;

import java.io.DataOutputStream;
import java.net.Socket;

import org.markandersen.io.IOUtils;


public class SimpleInvocationImpl implements SimpleInvocation {

	private String host;

	private int port;

	public SimpleInvocationImpl(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * 
	 * @see org.markandersen.service.invocation.SimpleInvocation#sendBytes(int)
	 */
	public int sendBytes(int numberOfBytes) {
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			DataOutputStream stream = new DataOutputStream(socket
					.getOutputStream());
			String dataString = "alskdjfweoriuxcv.,msdf";
			byte[] bytes = dataString.getBytes();
			stream.write(bytes);
			return bytes.length;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(socket);
		}
		return -1;
	}

}
