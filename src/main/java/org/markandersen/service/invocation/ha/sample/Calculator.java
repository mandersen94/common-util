package org.markandersen.service.invocation.ha.sample;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * @author mandersen
 */
public interface Calculator extends Remote {

	public int add(int arg1, int arg2) throws RemoteException;

	public int multiply(int arg1, int arg2) throws RemoteException;

	public float divide(int arg1, int arg2) throws DivideByZeroException,
			RemoteException;

}
