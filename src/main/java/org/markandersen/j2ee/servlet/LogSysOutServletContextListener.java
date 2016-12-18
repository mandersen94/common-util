package org.markandersen.j2ee.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author mark@markandersen.org
 */
public class LogSysOutServletContextListener implements ServletContextListener {

	public LogSysOutServletContextListener() {
		System.out.println("ServletContextListenerImpl.<init>()");
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		System.out
				.println("ServletContextListenerImpl.contextDestroyed(): servletContext = "
						+ servletContext);

	}

	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		System.out
				.println("ServletContextListenerImpl.contextInitialized(): servletContext = "
						+ servletContext);
	}

}
