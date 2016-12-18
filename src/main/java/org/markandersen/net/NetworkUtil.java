package org.markandersen.net;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

public class NetworkUtil {

	/**
	 * 
	 * @return
	 */
	public static int getFreePort() {

		int port = 2002;
		while (true) {
			try {
				ServerSocket socket = new ServerSocket();
				//socket.setReuseAddress(true);
				SocketAddress endpoint = new InetSocketAddress(port);
				socket.bind(endpoint);
				socket.close();
				return port;
			} catch (Exception ex) {
				// caught exception. increment port
				port++;
			}

		}
	}

}
