package org.markandersen.net;

import java.net.ServerSocket;

import org.markandersen.net.NetworkUtil;
import org.markandersen.test.BaseTestCase;


public class NetworkUtilTest extends BaseTestCase {

	/**
	 * 
	 * @throws Exception
	 */
	public void testGetFreePort() throws Exception {
		int counter = 10;
		for (int i = 0; i < counter; i++) {
			int freePort = NetworkUtil.getFreePort();
			// this will throw an exception if it fails.
			ServerSocket socket = new ServerSocket(freePort);
			socket.close();

		}
	}
}
