package org.markandersen.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketBlocker {

	private int port;

	private boolean eatData = true;

	private int readLength = 1 * 1024;

	public SocketBlocker() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			if (args.length == 0) {
				printUsage();
				System.exit(0);
			}

			SocketBlocker socketBlocker = new SocketBlocker();
			if (args.length > 0) {
				socketBlocker.setPort(Integer.parseInt(args[0]));
			}

			if (args.length > 1) {
				String value = args[1];
				if ("noRead".equalsIgnoreCase(value)) {
					socketBlocker.setEatData(false);
				}
			}

			socketBlocker.run();

		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public void run() throws IOException {
		log("Starting socket listening on port " + port);
		ServerSocket serverSocket = new ServerSocket(port);

		while (true) {
			final Socket socket = serverSocket.accept();
			final InetSocketAddress remoteSocketAddress = (InetSocketAddress) socket
					.getRemoteSocketAddress();
			log("recieved connection from " + remoteSocketAddress);
			Thread socketRunner = new Thread() {

				@Override
				public void run() {

					if (eatData) {
						log(remoteSocketAddress + ", eating data from socket.");
						try {
							InputStream inputStream = socket.getInputStream();
							byte[] bytes = new byte[readLength];
							while (true) {
								int result = inputStream.read(bytes);
								log(remoteSocketAddress + ", read " + result
										+ " bytes from socket.");
								if (result == -1) {
									log(remoteSocketAddress
											+ ", client socket closed. closing server socket.");
									socket.close();
									return;
								}
							}
						} catch (Exception ex) {
							log(remoteSocketAddress
									+ ", caught exception. closing socket.");
							ex.printStackTrace();
							try {
								socket.close();
							} catch (Exception tempEx) {
							}
						}
					} else {
						try {
							log(remoteSocketAddress
									+ ", not reading any data.  just sitting here.");
							InputStream inputStream = socket.getInputStream();
							Thread.sleep(1000 * 60 * 10);
						} catch (Exception tempEx) {
							log(remoteSocketAddress
									+ ", caught exception. closing socket.");
							tempEx.printStackTrace();
							try {
								socket.close();
							} catch (Exception ex) {

							}
						}
					}
				}
			};
			socketRunner.start();
		}
	}

	private void log(String message) {
		System.out.println(message);
	}

	private static void printUsage() {
		System.out
				.println("java -jar common-util.jar com.andersen.net.SocketBlocker portToListenOn [noRead]");
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isEatData() {
		return eatData;
	}

	public void setEatData(boolean eatData) {
		this.eatData = eatData;
	}

}
