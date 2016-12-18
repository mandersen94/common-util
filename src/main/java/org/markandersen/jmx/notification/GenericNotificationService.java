package org.markandersen.jmx.notification;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

/**
 * Generic class to send generic JMX notifications through.
 * 
 * @author Mark Andersen
 */
public class GenericNotificationService extends NotificationBroadcasterSupport
		implements GenericNotificationServiceMBean {

	private int defaultSequenceNumber = 1;

	private static final String DEFAULT_NOTIFICATION_TYPE = "notification.generic";

	public static final String JMX_NAME = "type=genericNotification";

	private static GenericNotificationService service;

	private static MBeanServer platformMBeanServer;

	private static ObjectName objectName;

	/**
	 * 
	 * @param type
	 * @param source
	 * @param sequenceNumber
	 * @param timeStamp
	 * @param message
	 */
	public void doNotification(String type, Object source, long sequenceNumber,
			long timeStamp, String message) {
		Notification notification = new Notification(type, source,
				sequenceNumber, timeStamp, message);
		sendNotification(notification);
	}

	/**
	 * 
	 * @param type
	 * @param source
	 * @param sequenceNumber
	 * @param timeStamp
	 * @param message
	 */
	public void doNotification(String message) {

		Notification notification = new Notification(DEFAULT_NOTIFICATION_TYPE,
				"notification.bean", getNextSequenceNumber(), System
						.currentTimeMillis(), message);
		sendNotification(notification);
	}

	/**
	 * 
	 * @return
	 */
	public synchronized long getNextSequenceNumber() {
		return defaultSequenceNumber++;
	}

	/**
	 * 
	 * @return
	 * @throws NotCompliantMBeanException
	 * @throws MBeanRegistrationException
	 */
	public synchronized static GenericNotificationService start()
			throws MBeanRegistrationException, NotCompliantMBeanException {

		if (service == null) {
			service = new GenericNotificationService();
			platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
			try {
				objectName = new ObjectName(platformMBeanServer
						.getDefaultDomain()
						+ ":" + JMX_NAME);
				platformMBeanServer.registerMBean(service, objectName);

			} catch (InstanceAlreadyExistsException ex) {
				// eat it.
				ex.printStackTrace();
			} catch (MalformedObjectNameException ex) {
				// eat it.
				ex.printStackTrace();
			}
		}
		return service;
	}

	/**
	 * 
	 * 
	 */
	public static void stop() {

		try {
			platformMBeanServer.unregisterMBean(objectName);
		} catch (InstanceNotFoundException ex) {
			ex.printStackTrace();
		} catch (MBeanRegistrationException ex) {
			ex.printStackTrace();
		}
	}

	public static GenericNotificationService getService() {
		return service;
	}
}
