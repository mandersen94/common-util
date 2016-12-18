package org.markandersen.j2ee.servlet;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Logs the requests as they are created and destroyed.
 * 
 * @author mark@markandersen.org
 */
public class LogSysOutRequestListener implements ServletRequestListener {

	public LogSysOutRequestListener() {
		System.out.println("RequestLoggerListener:<init>.");
	}

	/**
	 * 
	 */
	public void requestInitialized(ServletRequestEvent event) {
		HttpServletRequest servletRequest = (HttpServletRequest) event
				.getServletRequest();
		System.out
				.println("RequestLoggerListener.requestInitialized(): servletRequest = "
						+ servletRequest);

	}

	/**
	 * 
	 */
	public void requestDestroyed(ServletRequestEvent event) {
		HttpServletRequest servletRequest = (HttpServletRequest) event
				.getServletRequest();

		System.out
				.println("RequestLoggerListener.requestDestroyed()");
	}

}
