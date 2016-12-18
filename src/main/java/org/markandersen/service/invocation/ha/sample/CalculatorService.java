package org.markandersen.service.invocation.ha.sample;

import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;

import org.markandersen.jmx.notification.GenericNotificationService;
import org.markandersen.rmi.RegistryLocator;


/**
 * 
 * @author mandersen
 */
public class CalculatorService implements Calculator {

	public static final String SUCCESSFULLY_STARTED_MESSAGE = "successfully started CalculatorService.";

	public static final String SHUTDOWN_MESSAGE = "CalculatorService is shutdown.";

	private boolean addCalled = false;

	private boolean multiplyCalled = false;

	private boolean divideCalled = false;

	private int port;

	private Registry registry;

	private Calculator remoteStub;

	private String registryKey;

	private boolean started = false;

	private GenericNotificationService notificationService;

	private String hostname;

	/**
	 * 
	 * @param registry
	 * @param registryKey
	 * @throws RemoteException
	 */
	public CalculatorService(Registry registry, String registryKey)
			throws RemoteException {
		this(0, registry, registryKey);
	}

	/**
	 * 
	 * @param port
	 * @param registry
	 * @param registryKey
	 * @throws RemoteException
	 */
	public CalculatorService(int port, Registry registry, String registryKey)
			throws RemoteException {
		this.port = port;
		this.registry = registry;
		this.registryKey = registryKey;
		try {
			this.hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			this.hostname = "unknown";
		}
	}

	/**
	 * 
	 */
	public int add(int arg1, int arg2) throws RemoteException {
		System.out.printf("CalculatorImpl.add(): called. instance %d.\n", port);
		addCalled = true;
		notificationService
				.doNotification("Calculator.add(): called. myHost = "
						+ hostname + ", my port = " + port);
		return arg1 + arg2;
	}

	/**
	 * 
	 */
	public int multiply(int arg1, int arg2) throws RemoteException {
		System.out.printf("CalculatorImpl.multiply(): called. instance %d.\n",
				port);
		multiplyCalled = true;
		notificationService
				.doNotification("Calculator.multiply(): called. myHost = "
						+ hostname + ", my port = " + port);
		return arg1 * arg2;
	}

	/**
	 * 
	 */
	public float divide(int arg1, int arg2) throws DivideByZeroException,
			RemoteException {
		System.out.printf("CalculatorImpl.divide(): called. instance %d.\n",
				port);
		divideCalled = true;
		notificationService
				.doNotification("Calculator.divide(): called. myHost = "
						+ hostname + ", my port = " + port);
		if (arg2 == 0) {
			throw new DivideByZeroException();
		} else {
			return arg1 / (float) arg2;
		}
	}

	/**
	 * 
	 * @throws RemoteException
	 * @throws AlreadyBoundException
	 * @throws NotCompliantMBeanException
	 * @throws MBeanRegistrationException
	 */
	public void startService() throws RemoteException, AlreadyBoundException,
			MBeanRegistrationException, NotCompliantMBeanException {
		if (!started) {
			remoteStub = (Calculator) UnicastRemoteObject.exportObject(this,
					port);
			registry.rebind(registryKey, remoteStub);

			notificationService = GenericNotificationService.start();
			started = true;
		}
	}

	/**
	 * stops the exported RMI object and removes it from the registry.
	 * 
	 * @throws NotBoundException
	 * @throws RemoteException
	 * @throws AccessException
	 * 
	 */
	public void stopService() throws AccessException, RemoteException,
			NotBoundException {
		if (started) {
			// leave the object bound.
			// registry.unbind(registryKey);
			UnicastRemoteObject.unexportObject(this, true);
			started = false;
		}
	}

	public boolean isAddCalled() {
		return addCalled;
	}

	public void setAddCalled(boolean addCalled) {
		this.addCalled = addCalled;
	}

	public boolean isMultiplyCalled() {
		return multiplyCalled;
	}

	public void setMultiplyCalled(boolean multiplyCalled) {
		this.multiplyCalled = multiplyCalled;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isDivideCalled() {
		return divideCalled;
	}

	public void setDivideCalled(boolean divideCalled) {
		this.divideCalled = divideCalled;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			System.out
					.println("usage: CalculatorService registryPort registryKey [objectPort]");
			System.exit(1);
		}

		int registryPort = Integer.parseInt(args[0]);
		String registryKey = args[1];

		int objectPort = 0;
		if (args.length > 2) {
			objectPort = Integer.parseInt(args[2]);
		}
		try {
			Registry rmiRegistry = RegistryLocator.getRegistry(registryPort);
			CalculatorService service = new CalculatorService(objectPort,
					rmiRegistry, registryKey);
			System.out.println("trying to start CalculatorService.");

			service.startService();
			System.out.println(SUCCESSFULLY_STARTED_MESSAGE);
			Runtime.getRuntime().addShutdownHook(new ShutdownThread());

			synchronized (CalculatorService.class) {
				CalculatorService.class.wait();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @author e63582
	 * 
	 */
	static class ShutdownThread extends Thread {
		@Override
		public void run() {
			System.out.println(SHUTDOWN_MESSAGE);
		}
	}
}
