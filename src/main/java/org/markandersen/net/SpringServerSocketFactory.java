package org.markandersen.net;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

import org.springframework.beans.factory.FactoryBean;

/**
 * ServerSocket factory for spring
 * 
 * @author Mark Andersen
 */
public class SpringServerSocketFactory implements FactoryBean {

	private boolean singleton = true;

	private int backlog = 50;

	private int port = 0;

	private String hostname;

	/**
	 * 
	 */
	public Object getObject() throws Exception {
		ServerSocketChannel channel = ServerSocketChannel.open();
		ServerSocket serverSocket = channel.socket();

		SocketAddress endpoint = null;
		if (hostname == null) {
			endpoint = new InetSocketAddress(port);
		} else {
			endpoint = new InetSocketAddress(hostname, port);
		}
		serverSocket.bind(endpoint, backlog);
		return serverSocket;
	}

	public Class getObjectType() {
		return ServerSocket.class;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

}
