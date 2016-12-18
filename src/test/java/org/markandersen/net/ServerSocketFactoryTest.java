package org.markandersen.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.markandersen.test.BaseTestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 
 */
public class ServerSocketFactoryTest extends BaseTestCase {

	private ClassPathXmlApplicationContext context;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// context = new
		// ClassPathXmlApplicationContext("applicationContext.xml");
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void _testServerSocketFactory() throws Exception {
		final ServerSocket serverSocket = (ServerSocket) context
				.getBean("serverSocket");
		Callable<Boolean> task = new Callable<Boolean>() {
			public Boolean call() throws Exception {
				Socket socket = serverSocket.accept();
				int temp = socket.getInputStream().read();
				socket.getOutputStream().write(temp);
				socket.close();
				return true;
			}
		};
		Future<Boolean> future = Executors.newSingleThreadExecutor().submit(
				task);
		Socket socket = new Socket("localhost", serverSocket.getLocalPort());
		byte testByte = (byte) 0x23;
		socket.getOutputStream().write(testByte);
		byte responseByte = (byte) socket.getInputStream().read();
		assertEquals(testByte, responseByte);
		Thread.sleep(10);
		Boolean response = future.get(100, TimeUnit.MILLISECONDS);
		assertTrue(response);
	}

	public void testDummy() {

	}
}
