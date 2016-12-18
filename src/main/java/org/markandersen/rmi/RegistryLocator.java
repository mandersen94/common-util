package org.markandersen.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Class to locate the RMI Registry
 * 
 * @author mandersen
 */
public class RegistryLocator {

	/**
	 * Returns the registry that is started on a certain port. if there is
	 * already one started, it returns a reference to it.
	 * 
	 * 
	 * @param port
	 * @return
	 * @throws RemoteException
	 */
	public static Registry getRegistry(int port) throws RemoteException {
		Registry registry = null;
		try {
			// try to
			registry = LocateRegistry.getRegistry(port);
			// test to see if it is really there.
			registry.lookup("asdweroiu0982341lkjamwer");
		} catch (NotBoundException ex) {
			// means its aive. correct outcome
		} catch (RemoteException ex) {
			registry = LocateRegistry.createRegistry(port);
		}
		return registry;
	}
}
