package org.markandersen.rmi;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class RMIBrowser {

	private String host;

	private int port;

	private static String serviceURLBase = "service:jmx:rmi:///jndi/rmi://";

	private static String path = "jmxrmi";

	public RMIBrowser(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void dumpRegistry() {

		try {
			Registry registry = LocateRegistry.getRegistry(host, port);
			assert registry != null : "Registry was null";
			String[] registryEntries = registry.list();
			for (String entry : registryEntries) {
				Remote remote = registry.lookup(entry);
				System.out.println(entry + " = " + remote);
				System.out.println("class = " + remote.getClass());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @return
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public MBeanServerConnection connect() {

		try {
			String serviceURL = serviceURLBase + host + ":" + port + "/" + path;
			JMXServiceURL url = new JMXServiceURL(serviceURL);

			JMXConnector connector = JMXConnectorFactory.connect(url);
			MBeanServerConnection beanServerConnection = connector
					.getMBeanServerConnection();
			Set<ObjectName> set = beanServerConnection.queryNames(null, null);
			for (ObjectName name : set) {
				System.out.println("name = " + name);
			}
			return beanServerConnection;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public static void main(String[] args) {
		String host = "localhost";
		int port = 4321;
		RMIBrowser browser = new RMIBrowser(host, port);
		browser.dumpRegistry();
		browser.connect();
	}
}
