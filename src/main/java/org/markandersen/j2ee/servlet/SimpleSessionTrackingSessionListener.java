package org.markandersen.j2ee.servlet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 
 * @author mark@markandersen.org
 */
public class SimpleSessionTrackingSessionListener implements
		HttpSessionListener {

	private static final String PATTERN = "yyyy/MM/dd HH:mm:ss,SSS";
	private static int numberOfSessions = 0;
	private static boolean dumpAttributes = true;

	/**
	 * 
	 */
	public SimpleSessionTrackingSessionListener() {
		log("SessionListener.<init>");
	}

	/**
	 * 
	 */
	public void sessionCreated(HttpSessionEvent sessionEvent) {
		HttpSession session = sessionEvent.getSession();
		String sessionId = session.getId();
		synchronized (SimpleSessionTrackingSessionListener.class) {
			numberOfSessions++;
			log("SessionListener:sessionCreated(): numberOfSessions = "
					+ numberOfSessions + ", id = " + sessionId);
		}
	}

	/**
	 * 
	 */
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		HttpSession session = sessionEvent.getSession();
		String sessionId = session.getId();
		synchronized (SimpleSessionTrackingSessionListener.class) {
			numberOfSessions--;
			printDetails(sessionEvent);
		}
	}

	/**
	 * 
	 * @param sessionEvent
	 */
	private void printDetails(HttpSessionEvent sessionEvent) {
		HttpSession session = sessionEvent.getSession();
		long creationTime = session.getCreationTime();
		long destructionTime = System.currentTimeMillis();
		long sessionDurationMillis = destructionTime - creationTime;
		long lastAccessedTime = session.getLastAccessedTime();
		String sessionId = session.getId();

		StringBuffer buf = new StringBuffer();
		buf.append(
				"SessionListener:sessionDestroyed(): numberOfSessionsAlive = ")
				.append(numberOfSessions);
		buf.append(", sessionId = ").append(sessionId).append(", ");
		buf.append("sessionDuration = ").append(
				formatTime(sessionDurationMillis)).append(", ");
		buf.append("creationTime = ").append(formatDate(creationTime)).append(
				", ");
		buf.append("lastAccessedTime = ").append(formatDate(lastAccessedTime))
				.append(", ");
		buf.append("sessionDestructionTime = ").append(
				formatDate(destructionTime));
		if (dumpAttributes) {
			buf.append(", sessionAttributes[ ");
			Enumeration attributeNames = session.getAttributeNames();
			while (attributeNames.hasMoreElements()) {
				String attributeName = (String) attributeNames.nextElement();
				Object attributeValue = session.getAttribute(attributeName);
				buf.append(attributeName).append(" = ").append(attributeValue);
				buf.append(", ");
			}
			buf.append("]");
		}
		log(buf.toString());

	}

	/**
	 * 
	 * @param creationTime
	 * @return
	 */
	private String formatDate(long creationTime) {

		DateFormat dateFormat = new SimpleDateFormat(PATTERN);
		String format = dateFormat.format(new Date(creationTime));
		return format;
	}

	/**
	 * 
	 * @param timeInMillis
	 * @return
	 */
	private String formatTime(long timeInMillis) {
		long seconds = timeInMillis / 1000;
		long minutes = seconds / 60;

		return timeInMillis + " ms ( " + minutes + " minutes )";
	}

	/**
	 * 
	 */
	private void log(String message) {
		System.out.println(message);
	}

}
