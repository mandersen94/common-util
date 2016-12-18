package org.markandersen.j2ee.servlet;

/**
 * 
 * @author mandersen
 */
public class TxId {

	private static final InheritableThreadLocal instance = new InheritableThreadLocal();
	
	public static void set(String txId) {
		instance.set(txId);
	}

	public static String get(){
		return (String) instance.get();
	}
	
	public static void clear() {
		instance.remove();
	
	}

	
	
}
