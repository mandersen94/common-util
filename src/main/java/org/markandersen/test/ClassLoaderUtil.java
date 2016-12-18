package org.markandersen.test;

/**
 * 
 * @author e63582
 */
public class ClassLoaderUtil {

	/**
	 * 
	 * @param obj
	 */
	public static void dumpClassloaderInfo(Class<?> clazz) {

		ClassLoader classloader = clazz.getClassLoader();
		System.out.println("/************************************/");
		System.out.println("classloader heirarchy for object " + clazz);
		System.out.println("object classloader  = " + classloader.toString());

		ClassLoader parentLoader = classloader.getParent();

		while (parentLoader != null) {
			System.out.println("parent classloader is " + parentLoader);
			parentLoader = parentLoader.getParent();
		}
		System.out.println("/************************************/");
	}

	/**
	 * 
	 * @param obj
	 */
	public static void dumpClassloaderInfo(Object obj) {
		dumpClassloaderInfo(obj.getClass());
	}
}
