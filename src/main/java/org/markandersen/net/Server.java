package org.markandersen.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {

	private int listenerPort = 0;

	private boolean running = false;

	protected ServerSocket serverSocket;

	private Thread acceptorThread;

	private ExecutorService executorService;

	private SocketHandler handler;

	/**
	 * 
	 * @throws IOException
	 */
	public void startListener() throws IOException {

		serverSocket = new ServerSocket();
		if (listenerPort == 0) {
			// choose a random port.
			serverSocket.bind(null);
		} else {
			SocketAddress endpoint = new InetSocketAddress(listenerPort);
			serverSocket.bind(endpoint);
		}

		executorService = Executors.newCachedThreadPool();
		running = true;
		listenerPort = serverSocket.getLocalPort();
		acceptorThread = new Thread(new AcceptorThread(),
				"SocketAcceptorThread");
		acceptorThread.start();

	}

	/**
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void stopListener() throws IOException, InterruptedException {
		running = false;
		// acceptorThread.interrupt();
		executorService.shutdownNow();
		serverSocket.close();
		acceptorThread.join(1000);
	}

	/**
	 * 
	 * @author mandersen
	 */
	class AcceptorThread implements Runnable {

		public void run() {
			try {
				while (running) {
					final Socket clientSocket = serverSocket.accept();
					final SocketHandler tempHandler = handler;
					Callable<Object> callable = new Callable<Object>() {
						public Object call() throws Exception {
							tempHandler.handleSocket(clientSocket);
							return null;
						}
					};
					Future<Object> future = executorService.submit(callable);
					try {
						future.get();
					} catch (Exception e) {
						System.err.println("Caught Exception on get.");
						e.printStackTrace();

					}
				}
			} catch (SocketException ex) {
				// probably closed.
				if (ex.getMessage().indexOf("Socket closed") > -1) {
					// nop
				} else {
					ex.printStackTrace();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public Thread getAcceptorThread() {
		return acceptorThread;
	}

	public void setAcceptorThread(Thread acceptorThread) {
		this.acceptorThread = acceptorThread;
	}

	public SocketHandler getHandler() {
		return handler;
	}

	public void setHandler(SocketHandler handler) {
		this.handler = handler;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public void setListenerPort(int listenerPort) {
		this.listenerPort = listenerPort;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

}
