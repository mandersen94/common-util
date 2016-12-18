package org.markandersen.service.invocation.ha;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * 
 * @author mandersen
 */
public class RMILookupProxy implements InvocationHandler {

	private int registryPort;

	private String registryPath;

	private Object reference;

	/**
	 * 
	 * @param registryPort
	 * @param registryPath
	 */
	public RMILookupProxy(int registryPort, String registryPath) {
		this.registryPort = registryPort;
		this.registryPath = registryPath;
	}

	/**
	 * 
	 * @param registryPort
	 * @param registryPath
	 */
	public static Object createProxy(int registryPort, String registryPath,
			Class[] interfaces, ClassLoader loader) {

		RMILookupProxy handler = new RMILookupProxy(registryPort, registryPath);
		return Proxy.newProxyInstance(loader, interfaces, handler);
	}

	/**
	 * 
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (reference == null) {
			lookupObject();
		}
		try {
			return method.invoke(reference, args);
		} catch (InvocationTargetException ex) {
			if (shouldRetry(ex.getCause())) {
				lookupObject();
				return method.invoke(reference, args);
			} else {
				throw ex.getCause();
			}
		}
	}

	/**
	 * 
	 * @param ex
	 * @return
	 */
	private boolean shouldRetry(Throwable throwable) {
		Throwable cause = throwable.getCause();
		if (cause instanceof RemoteException) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @throws RemoteException
	 * @throws NotBoundException
	 * 
	 * 
	 */
	private void lookupObject() throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(registryPort);
		reference = registry.lookup(registryPath);
	}

}
