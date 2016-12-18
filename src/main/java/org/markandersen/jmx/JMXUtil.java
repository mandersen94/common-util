package org.markandersen.jmx;

import java.io.IOException;
import java.net.InetAddress;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

/**
 * 
 * @author e63582
 */
public class JMXUtil {

	private static final String jmxHttpAdaptorClassName = "mx4j.tools.adaptor.http.HttpAdaptor";

	private static MBeanServer singletonMBeanServer;

	/**
	 * Start the RMIRemoting for the JMX server
	 * 
	 * @param server
	 * @throws MalformedObjectNameException
	 * @throws InstanceAlreadyExistsException
	 * @throws MBeanRegistrationException
	 * @throws NotCompliantMBeanException
	 * @throws InstanceNotFoundException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws AttributeNotFoundException
	 * @throws IOException
	 */
	public static void startRMIRemoting(MBeanServer server)
			throws MalformedObjectNameException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, InstanceNotFoundException,
			ReflectionException, MBeanException, AttributeNotFoundException,
			IOException {

		// Register and start the rmiregistry MBean, needed by JSR 160
		// RMIConnectorServer
		ObjectName namingName = ObjectName
				.getInstance("naming:type=rmiregistry");
		server.createMBean("mx4j.tools.naming.NamingService", namingName, null);
		server.invoke(namingName, "start", null, null);
		int namingPort = ((Integer) server.getAttribute(namingName, "Port"))
				.intValue();

		String jndiPath = "/jmxconnector";
		JMXServiceURL url = new JMXServiceURL(
				"service:jmx:rmi://localhost/jndi/rmi://localhost:"
						+ namingPort + jndiPath);

		// Create and start the RMIConnectorServer
		JMXConnectorServer connectorServer = JMXConnectorServerFactory
				.newJMXConnectorServer(url, null, server);
		connectorServer.start();
	}

	/**
	 * Start the RMIRemoting for the JMX server
	 * 
	 * @throws MalformedObjectNameException
	 * @throws InstanceAlreadyExistsException
	 * @throws MBeanRegistrationException
	 * @throws NotCompliantMBeanException
	 * @throws InstanceNotFoundException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws AttributeNotFoundException
	 * @throws IOException
	 */
	public static void startRMIRemoting() throws MalformedObjectNameException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, InstanceNotFoundException,
			ReflectionException, MBeanException, AttributeNotFoundException,
			IOException {
		startRMIRemoting(getMBeanServer());
	}

	/**
	 * Start the HTTP adapter and connect it to the default server.
	 * 
	 * @param port
	 *            to run the HTTP run.
	 * @throws MalformedObjectNameException
	 * @throws NotCompliantMBeanException
	 * @throws MBeanRegistrationException
	 * @throws InstanceAlreadyExistsException
	 * @throws IOException
	 */
	public void startHttpAdapter(int port) throws MalformedObjectNameException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, IOException {

		MBeanServer server = getMBeanServer();
		Object adaptorInstance = null;
		try {
			Class<?> adaptorClass = Class.forName(jmxHttpAdaptorClassName);
			adaptorInstance = adaptorClass.newInstance();
		} catch (Exception e) {
			// TODO: handle exception
		}

		// HttpAdaptor adapter = new HttpAdaptor();
		ObjectName name = new ObjectName("Server:name=HttpAdaptor");
		server.registerMBean(adaptorInstance, name);

		// do the next 3 operations reflectively so we aren't using a
		// mx4j specific class.
		// adapter.setPort(port);
		// adapter.setHost(InetAddress.getLocalHost().getHostName());
		// adapter.start();
		try {
			server.invoke(name, "setPort", new Object[] { port },
					new String[] { "java.lang.Integer" });
			server.invoke(name, "setHost", new Object[] { InetAddress
					.getLocalHost().getHostName() },
					new String[] { "java.lang.String" });
			server.invoke(name, "start", new Object[0], new String[0]);
		} catch (Exception e) {

		}
	}

	/**
	 * Returns a singleton instance of the MBeanServer.
	 * 
	 * @return
	 */
	public synchronized static MBeanServer getMBeanServer() {

		if (singletonMBeanServer == null) {
			singletonMBeanServer = MBeanServerFactory.createMBeanServer();
		}
		return singletonMBeanServer;
	}

	/**
	 * Utility method to encode a JMX key name, escaping illegal characters.
	 * 
	 * @param jmxName
	 *            unescaped string buffer of form JMX keyname=key
	 * @param attrPos
	 *            position of key in String
	 */
	public static String jmxEncodeString(String name) {
		StringBuffer jmxName = new StringBuffer(name);
		for (int i = 0; i < jmxName.length(); i++) {
			if (jmxName.charAt(i) == ',') {
				jmxName.setCharAt(i, ';');
			} else if (jmxName.charAt(i) == '?' || jmxName.charAt(i) == '*'
					|| jmxName.charAt(i) == '\\') {
				jmxName.insert(i, '\\');
				i++;
			} else if (jmxName.charAt(i) == '\n') {
				jmxName.insert(i, '\\');
				i++;
				jmxName.setCharAt(i, 'n');
			} else if (jmxName.charAt(i) == '=') {
				jmxName.setCharAt(i, '|');
			}
		}
		return jmxName.toString();
	}

	/**
	 * 
	 * @param operationStats
	 * @param objectName
	 */
	public static void registerMBean(Object object, String name) {

		try {
			MBeanServer server = getMBeanServer();
			ObjectName objectName = new ObjectName(jmxEncodeString(name));
			server.registerMBean(object, objectName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isJMXEnabled() {
		return "true".equalsIgnoreCase(System
				.getProperty(JMXConstants.ENABLE_JMX_PROPERTY_NAME));
	}
}
