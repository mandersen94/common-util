package org.markandersen.service.invocation.ha.rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.markandersen.text.MessageFormatter;


/**
 * 
 * @author Mark Andersen
 */
public class RMILookupProxy implements InvocationHandler {

	private String host;

	private int registryPort;

	private String registryPath;

	private Object reference;

	/**
	 * 
	 * @param host
	 * @param registryPort
	 * @param registryPath
	 */
	public RMILookupProxy(String host, int registryPort, String registryPath) {
		this.host = host;
		this.registryPort = registryPort;
		this.registryPath = registryPath;
	}

	/**
	 * 
	 * @param host
	 * @param registryPort
	 * @param registryPath
	 * @return
	 */
	public static Object createProxy(String host, int registryPort,
			String registryPath, Class[] interfaces, ClassLoader loader) {
		RMILookupProxy handler = new RMILookupProxy(host, registryPort,
				registryPath);
		Object proxy = Proxy.newProxyInstance(loader, interfaces, handler);
		return proxy;
	}

	/**
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (reference == null) {
			lookupObject();
		}
		// override toString()
		if (("toString".equals(method.getName())) && (args == null)) {
			return MessageFormatter.format("objRef [path = {}]", new Object[] {
					registryPath, host, registryPort });
		}
		try {
			Object result = method.invoke(reference, args);
			return result;

		} catch (InvocationTargetException ex) {
			Throwable cause = ex.getCause();
			if (shouldRetry(cause)) {
				lookupObject();
				try {
					Object result = method.invoke(reference, args);
					return result;
				} catch (InvocationTargetException e) {
					throw e.getCause();
				}
			} else {
				throw cause;
			}
		}
	}

	/**
	 * 
	 * @param cause
	 * @return
	 */
	private boolean shouldRetry(Throwable cause) {
		if (cause instanceof RemoteException) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void lookupObject() throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(host, registryPort);
		reference = registry.lookup(registryPath);
	}

}
