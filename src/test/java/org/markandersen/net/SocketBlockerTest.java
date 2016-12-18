/**
 * 
 */
package org.markandersen.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import junit.framework.TestCase;

/**
 * @author e63582
 * 
 */
public class SocketBlockerTest extends TestCase {

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testSocketBlocket() throws IOException {
		Socket socket = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(3300), 5 * 1000);
			System.out.println("socket connected. writing to socket.");
			socket.setSoTimeout(15 * 1000);
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write("help me".getBytes());

			InputStream inputStream = socket.getInputStream();
			byte[] bytes = new byte[1 * 1024];
			System.out.println("trying to read bytes.");
			int number = inputStream.read(bytes);
			System.out.println("read " + number + " bytes.");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			socket.close();
		}
	}
}
