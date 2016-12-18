package org.markandersen.test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author e63582
 */
public class DelegatingClassLoader extends URLClassLoader {

	private static Object PLACE_HOLDER = new Object();

	private String[] exclusionList;

	private boolean delegateParentFirst = false;

	private Map<String, Object> exclusionMap;

	/**
	 * @param urls
	 */
	public DelegatingClassLoader(URL[] urls, ClassLoader parent,
			boolean parentFirst) {
		this(urls, parent, parentFirst, new String[0]);
	}

	/**
	 * @param urls
	 */
	public DelegatingClassLoader(URL[] urls, ClassLoader parent,
			boolean parentFirst, String[] exclusions) {
		super(urls, parent);
		delegateParentFirst = parentFirst;
		this.exclusionList = exclusions;
		initExclusions();
	}

	/**
	 * 
	 */
	private void initExclusions() {
		exclusionMap = new HashMap<String, Object>();

		for (int i = 0; i < exclusionList.length; i++) {
			exclusionMap.put(exclusionList[i], PLACE_HOLDER);
		}
	}

	/**
	 * @param name
	 * @return
	 * @throws ClassNotFoundException
	 */
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// do nothing.
		return super.findClass(name);
	}

	/**
	 * @param name
	 * @param resolve
	 * @return
	 * @throws ClassNotFoundException
	 */
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		log("loadClass : " + name + ", resolve : " + resolve);

		if (exclusionMap.containsKey(name)) {
			// always delegate to parent
			return loadClassDelegateParentFirst(name, resolve);
		}

		if (delegateParentFirst) {
			return loadClassDelegateParentFirst(name, resolve);
		} else {
			return loadClassDelegateParentLast(name, resolve);
		}

		// return super.loadClass(name, resolve);
	}

	/**
	 * @param name
	 * @param resolve
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Class<?> loadClassDelegateParentFirst(String name, boolean resolve)
			throws ClassNotFoundException {

		Class<?> c = findLoadedClass(name);

		if (c == null) {

			try {
				c = getParent().loadClass(name);
				log("Found class " + name + " in parent classloader.");
			} catch (ClassNotFoundException ex) {
				log("Couldn't find class " + name
						+ " in parent classloader.  using current classloader.");
				c = findClass(name);
			}
		}

		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	/**
	 * @param name
	 * @param resolve
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Class<?> loadClassDelegateParentLast(String name, boolean resolve)
			throws ClassNotFoundException {

		Class<?> c = findLoadedClass(name);

		if (c == null) {

			try {
				c = findClass(name);
				log("Found class " + name + " in delegation classpath");
			} catch (ClassNotFoundException ex) {
				log("Couldn't find class " + name
						+ " in current classloader.  delegating to parent.");
				c = getParent().loadClass(name);
			}
		}

		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	/**
	 * @param message
	 */
	protected void log(String message) {

		if (false) {
			System.out.println(message);
		}
	}
}
