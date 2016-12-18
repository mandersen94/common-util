package org.markandersen.jmx.notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * 
 * @author mandersen
 */
public class GenericNotificationListener implements NotificationListener {

	private List<Notification> notifications = new ArrayList<Notification>();

	private String host = "localhost";

	private int jmxPort = 9999;

	private MBeanServerConnection beanServerConnection;

	private ObjectName objectName;

	private JMXConnector jmxc;

	private boolean listening = false;

	private NotificationListener connectionNotificationListener = new ConnectionNotificationListener();

	public List<Notification> connectionNotifications = new ArrayList<Notification>();

	public void startListening() throws IOException,
			MalformedObjectNameException, InstanceNotFoundException {
		if (!listening) {
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://" + host + ":" + jmxPort
							+ "/jmxrmi");
			jmxc = JMXConnectorFactory.connect(url, null);

			jmxc.addConnectionNotificationListener(
					connectionNotificationListener, null, null);
			beanServerConnection = jmxc.getMBeanServerConnection();

			objectName = new ObjectName("DefaultDomain:"
					+ GenericNotificationService.JMX_NAME);
			beanServerConnection.addNotificationListener(objectName, this,
					null, null);
			listening = true;

		}
	}

	/**
	 * 
	 * @throws InstanceNotFoundException
	 * @throws ListenerNotFoundException
	 * @throws IOException
	 */
	public void stopListening() throws InstanceNotFoundException,
			ListenerNotFoundException, IOException {
		if (listening) {
			try {
				beanServerConnection.removeNotificationListener(objectName,
						this);
				jmxc
						.removeConnectionNotificationListener(connectionNotificationListener);
				jmxc.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			listening = false;
		}
	}

	/**
	 * 
	 */
	public synchronized void handleNotification(Notification notification,
			Object handback) {
		System.out.println("received notification: " + notification);
		notifications.add(notification);
		notifyAll();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public int getJmxPort() {
		return jmxPort;
	}

	public void setJmxPort(int port) {
		this.jmxPort = port;
	}

	public synchronized void clearNotifications() {
		notifications.clear();
	}

	/**
	 * 
	 * @param maxWaitTime
	 * @return
	 */
	public synchronized Notification getNewNotification(long maxWaitTime) {
		try {
			if (notifications.isEmpty()) {
				// if empty, wait until notified (which means a new notification
				// came in
				wait(maxWaitTime);
			}
			if (notifications.isEmpty()) {
				// still empty. return null.
				return null;
			} else {
				return notifications.get(notifications.size() - 1);
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * @author e63582
	 */
	class ConnectionNotificationListener implements NotificationListener {

		public void handleNotification(Notification notification,
				Object handback) {
			connectionNotifications.add(notification);
		}

	}

	public static void main(String[] args) {
		try {
			GenericNotificationListener listener = new GenericNotificationListener();
			listener.startListening();
			Thread.sleep(Integer.MAX_VALUE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
