package org.markandersen.j2ee.servlet;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

public class LogSysOutSessionActivationListener implements HttpSessionActivationListener {

	public LogSysOutSessionActivationListener() {
		System.out.println("SessionActivationListener.<init>");
	}

	/**
	 * 
	 */
	public void sessionDidActivate(HttpSessionEvent sessionEvent) {
		HttpSession session = sessionEvent.getSession();
		System.out
				.println("SessionActivationListener.sessionDidActiviate(): id = "
						+ session.getId()
						+ ", creationTime = "
						+ new Date(session.getCreationTime()));

	}

	/**
	 * 
	 */
	public void sessionWillPassivate(HttpSessionEvent sessionEvent) {
		HttpSession session = sessionEvent.getSession();
		System.out
				.println("SessionActivationListener.sessionWillPassivate(): id = "
						+ session.getId()
						+ ", creationTime = "
						+ new Date(session.getCreationTime()));

	}

}
