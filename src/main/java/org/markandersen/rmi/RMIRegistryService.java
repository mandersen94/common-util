package org.markandersen.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * 
 * @author Mark Andersen
 */
public class RMIRegistryService {

	public static final String SUCCESSFULLY_STARTED_MESSAGE = "successfully started RMIRegistryService on port ";

	public static final String SHUTDOWN_MESSAGE = "shutting down RMIRegistryService";

	private int registryPort;

	private boolean started = false;

	private Registry registry;

	public int getRegistryPort() {
		return registryPort;
	}

	public void setRegistryPort(int registryPort) {
		this.registryPort = registryPort;
	}

	/**
	 * starts a new registry.
	 * 
	 * @throws RemoteException
	 */
	public void startService() throws RemoteException {
		if (!started) {
			// use LocateRegistry to create a registry in our process.
			// we don't want someone elses.
			registry = LocateRegistry.createRegistry(registryPort);
		}
		started = true;
	}

	/**
	 * does nothing.
	 * 
	 */
	public void stopService() {
		if (started) {

			started = false;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 1) {
			System.out
					.println("usage java com.andersen.rmi.RMIRegistryService $port");
			System.exit(1);
		}

		try {
			int port = Integer.parseInt(args[0]);
			RMIRegistryService service = new RMIRegistryService();
			service.setRegistryPort(port);
			System.out.println("starting RMIRegistryService on port " + port);
			service.startService();
			System.out.println(SUCCESSFULLY_STARTED_MESSAGE + port);
			Runtime.getRuntime().addShutdownHook(
					new Thread(new RMIRegistryShutdownHook()));
			// wait forever
			synchronized (RMIRegistryService.class) {
				RMIRegistryService.class.wait();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static class RMIRegistryShutdownHook implements Runnable {

		public void run() {
			System.out.println(SHUTDOWN_MESSAGE);
		}

	}
}
