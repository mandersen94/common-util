package org.markandersen.reflect;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * 
 * @author e63582
 */
public class ClassFinder {

	private ClassFinder() {
	}

	// static only
	/**
	 * Convenience method for <code>findClassesThatExtend(Class[],
	 * boolean)</code>
	 * with the option to include inner classes in the search set to false.
	 * 
	 * @return List<String> containing discovered classes.
	 */
	public static List<String> findClassesThatExtend(String[] paths,
			Class[] superClasses) throws IOException, ClassNotFoundException {
		return findClassesThatExtend(paths, superClasses, false, null, null,
				true, ClassFinder.class.getClassLoader());
	}

	/**
	 * Convenience method for <code>findClassesThatExtend(Class[],
	 * boolean)</code>
	 * with the option to include inner classes in the search set to false.
	 * 
	 * @return ArrayList containing discovered classes.
	 */
	public static List<String> findClassesThatExtend(String[] paths,
			Class[] superClasses, List<String> includesList,
			List<String> excludesList) throws IOException,
			ClassNotFoundException {
		return findClassesThatExtend(paths, superClasses, false, includesList,
				excludesList, true, ClassFinder.class.getClassLoader());
	}

	/**
	 * 
	 * @param strPathsOrJars
	 * @param superClasses
	 * @param innerClasses
	 *            set true if you wnat to search for inner classes.
	 * @param ensurePathsOnClasspath
	 *            if true, only looks at classes in paths that are on the
	 *            classpath.
	 * @return List<String> of class names found.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static List<String> findClassesThatExtend(String[] strPathsOrJars,
			Class[] superClasses, boolean innerClasses,
			List<String> includesList, List<String> excludesList,
			boolean ensurePathsOnClasspath, ClassLoader classLoader)
			throws IOException, ClassNotFoundException {

		if (classLoader == null) {
			// if no classloader specified, use the one this class was
			// loaded from.
			classLoader = ClassFinder.class.getClassLoader();
		}

		long startTime = System.currentTimeMillis();
		strPathsOrJars = addJarsInPath(strPathsOrJars);

		// This checks to make sure the strPathsOrJars is is on the
		// classpath if desired.
		List<String> listPaths = null;
		if (ensurePathsOnClasspath) {
			listPaths = getPathsThatAreOnTheClasspath(strPathsOrJars);
		} else {
			listPaths = Arrays.asList(strPathsOrJars);
		}

		Set<String> listClasses = new TreeSet<String>();
		List<String> listSuperClasses = new ArrayList<String>();
		for (int i = 0; i < superClasses.length; i++) {
			listSuperClasses.add(superClasses[i].getName());
		}
		// first get all the classes
		findClassesInPaths(listPaths, listClasses, includesList, excludesList);

		// hack to store the classloader. used in later methods.
		Thread.currentThread().setContextClassLoader(classLoader);

		// now this parses through all the classes and find out which ones are
		// subclasses of my requested class.

		Set<String> subClassList = findAllSubclasses(listSuperClasses,
				listClasses, innerClasses);

		return new ArrayList<String>(subClassList);
	}

	/**
	 * converts a list of string paths to an array of URLs.
	 * 
	 * @param listPaths
	 * @return
	 */
	public static URL[] convertPathsToURLs(List<String> listPaths) {
		List<URL> urlList = new ArrayList<URL>();
		for (String stringPath : listPaths) {
			File file = new File(stringPath);
			try {
				urlList.add(file.toURL());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return urlList.toArray(new URL[urlList.size()]);

	}

	/**
	 * Find classes in the provided path(s)/jar(s) that extend the class(es).
	 * 
	 * @return ArrayList containing discovered classes
	 */
	private static String[] addJarsInPath(String[] paths) {
		Set<String> fullList = new HashSet<String>();
		for (String onePath : paths) {
			fullList.add(onePath);
			if (!onePath.endsWith(".jar")) {
				File dir = new File(onePath);
				if (dir.exists() && dir.isDirectory()) {
					String[] jars = dir.list(new FilenameFilter() {
						public boolean accept(File f, String name) {
							if (name.endsWith(".jar")) {
								return true;
							}
							return false;
						}
					});
					for (String oneJar : jars) {
						fullList.add(oneJar);
					}
				}
			}
		}
		return (String[]) fullList.toArray(new String[0]);
	}

	/**
	 * Goes through each of the strPathsOrJars and sees if it is on the current
	 * classpath. If not, the classes in the path aren't added to the list.
	 * 
	 * @param strPathsOrJars
	 * @return
	 */
	private static List<String> getPathsThatAreOnTheClasspath(
			String[] strPathsOrJars) {

		List<String> listPaths = new ArrayList<String>();
		// logger.debug("Classpath = " + System.getProperty("java.class.path"));

		if (strPathsOrJars != null) {
			strPathsOrJars = fixDotDirs(strPathsOrJars);
			strPathsOrJars = fixSlashes(strPathsOrJars);
			strPathsOrJars = fixEndingSlashes(strPathsOrJars);
		}

		// getting the classpaths as tokens.
		StringTokenizer javaClassPathTokenizer = new StringTokenizer(System
				.getProperty("java.class.path"), System
				.getProperty("path.separator"));
		// if (logger.isDebugEnabled()) {
		// for (int i = 0; i < strPathsOrJars.length; i++) {
		// logger
		// .debug("strPathsOrJars[" + i + "] : "
		// + strPathsOrJars[i]);
		// }
		// }

		// find all jar files or paths that end with strPathOrJar
		while (javaClassPathTokenizer.hasMoreTokens()) {
			String strPath = (String) javaClassPathTokenizer.nextToken();
			strPath = fixDotDir(strPath);
			strPath = fixSlashes(strPath);
			strPath = fixEndingSlashes(strPath);
			if (strPathsOrJars == null) {
				// logger.debug("Adding: " + strPath);
				listPaths.add(strPath);
			} else {
				boolean found = false;
				for (int i = 0; i < strPathsOrJars.length; i++) {
					if (strPath.endsWith(strPathsOrJars[i])) {
						found = true;
						// logger.debug("Adding " + strPath + " found at " + i);
						listPaths.add(strPath);
						break;// no need to look further
					}
				}
				if (!found) {
					// logger.debug("Did not find: " + strPath);
				}
			}
		}
		return listPaths;
	}

	/**
	 * Get all interfaces that the class implements, including parent
	 * interfaces. This keeps us from having to instantiate and check
	 * instanceof, which wouldn't work anyway since instanceof requires a
	 * hard-coded class or interface name.
	 * 
	 * @param theClass
	 *            the class to get interfaces for
	 * @param hInterfaces
	 *            a Map to store the discovered interfaces in
	 * 
	 * NOTUSED private static void getAllInterfaces(Class theClass, Map
	 * hInterfaces) { Class[] interfaces = theClass.getInterfaces(); for (int i =
	 * 0; i < interfaces.length; i++) { hInterfaces.put(interfaces[i].getName(),
	 * interfaces[i]); getAllInterfaces(interfaces[i], hInterfaces); } }
	 */
	private static String[] fixDotDirs(String[] paths) {
		for (int i = 0; i < paths.length; i++) {
			paths[i] = fixDotDir(paths[i]);
		}
		return paths;
	}

	private static String fixDotDir(String path) {
		if (path == null)
			return null;
		if (path.equals(".")) {
			return System.getProperty("user.dir");
		} else {
			return path.trim();
		}
	}

	private static String[] fixEndingSlashes(String[] strings) {
		String[] strNew = new String[strings.length];
		for (int i = 0; i < strings.length; i++) {
			strNew[i] = fixEndingSlashes(strings[i]);
		}
		return strNew;
	}

	private static String fixEndingSlashes(String string) {
		if (string.endsWith("/") || string.endsWith("\\")) {
			string = string.substring(0, string.length() - 1);
			string = fixEndingSlashes(string);
		}
		return string;
	}

	private static String[] fixSlashes(String[] strings) {
		String[] strNew = new String[strings.length];
		for (int i = 0; i < strings.length; i++) {
			strNew[i] = fixSlashes(strings[i]) /* .toLowerCase() */;
		}
		return strNew;
	}

	private static String fixSlashes(String str) {
		// replace \ with /
		str = str.replace('\\', '/');
		// compress multiples into singles;
		// do in 2 steps with dummy string
		// to avoid infinte loop
		str = replaceString(str, "//", "_____");
		str = replaceString(str, "_____", "/");
		return str;
	}

	private static String replaceString(String s, String strToFind,
			String strToReplace) {
		int index;
		int currentPos;
		StringBuffer buffer = null;
		if (s.indexOf(strToFind) == -1) {
			return s;
		}
		currentPos = 0;
		buffer = new StringBuffer();
		while (true) {
			index = s.indexOf(strToFind, currentPos);
			if (index == -1) {
				break;
			}
			buffer.append(s.substring(currentPos, index));
			buffer.append(strToReplace);
			currentPos = index + strToFind.length();
		}
		buffer.append(s.substring(currentPos));
		return buffer.toString();
	}

	/**
	 * NOTUSED * Determine if the class implements the interface.
	 * 
	 * @param theClass
	 *            the class to check
	 * @param theInterface
	 *            the interface to look for
	 * @return boolean true if it implements
	 * 
	 * private static boolean classImplementsInterface( Class theClass, Class
	 * theInterface) { HashMap mapInterfaces = new HashMap(); String strKey =
	 * null; // pass in the map by reference since the method is recursive
	 * getAllInterfaces(theClass, mapInterfaces); Iterator iterInterfaces =
	 * mapInterfaces.keySet().iterator(); while (iterInterfaces.hasNext()) {
	 * strKey = (String) iterInterfaces.next(); if (mapInterfaces.get(strKey) ==
	 * theInterface) { return true; } } return false; }
	 */

	/**
	 * Convenience method for <code>findAllSubclasses(List, List,
	 * boolean)</code>
	 * with the option to include inner classes in the search set to false.
	 * 
	 * @param listSuperClasses
	 *            the base classes to find subclasses for
	 * @param listAllClasses
	 *            the collection of classes to search in
	 * @return ArrayList of the subclasses
	 * 
	 * NOTUSED private static ArrayList findAllSubclasses( List
	 * listSuperClasses, List listAllClasses) { return
	 * findAllSubclasses(listSuperClasses, listAllClasses, false); }
	 */

	/**
	 * Finds all classes that extend the classes in the listSuperClasses
	 * ArrayList, searching in the listAllClasses ArrayList.
	 * 
	 * @param listSuperClasses
	 *            the base classes to find subclasses for
	 * @param listAllClasses
	 *            the collection of classes to search in
	 * @param innerClasses
	 *            indicate whether to include inner classes in the search
	 * @return ArrayList of the subclasses
	 */
	private static Set<String> findAllSubclasses(List<String> listSuperClasses,
			Set<String> listAllClasses, boolean innerClasses) {

		String superClassClassName = null;
		Class<?> tempClass = null;
		Set<String> listSubClasses = new TreeSet<String>();
		Iterator<String> superClassesIterator = listSuperClasses.iterator();
		while (superClassesIterator.hasNext()) {
			superClassClassName = superClassesIterator.next();
			// only check classes if they are not inner classes
			// or we intend to check for inner classes
			if ((superClassClassName.indexOf("$") == -1) || innerClasses) {
				// might throw an exception, assume this is ignorable
				try {
					ClassLoader cl = Thread.currentThread()
							.getContextClassLoader();
					tempClass = Class.forName(superClassClassName, false, cl);

					// this gets the subclass for one class.
					findAllSubclassesOneClass(tempClass, listAllClasses,
							listSubClasses, innerClasses);
					// call by reference - recursive
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return listSubClasses;
	}

	/**
	 * Finds all classes that extend the class, searching in the listAllClasses
	 * ArrayList.
	 * 
	 * @param theClass
	 *            the parent class
	 * @param listAllClasses
	 *            the collection of classes to search in
	 * @param listSubClasses
	 *            the collection of discovered subclasses
	 * @param innerClasses
	 *            indicates whether inners classes should be included in the
	 *            search
	 */
	private static void findAllSubclassesOneClass(Class<?> theClass,
			Set<String> listAllClasses, Set<String> listSubClasses,
			boolean innerClasses) {

		boolean bIsSubclass = false;
		for (String strClassName : listAllClasses) {
			// only check classes if they are not inner classes
			// or we intend to check for inner classes
			if ((strClassName.indexOf("$") == -1) || innerClasses) {
				// might throw an exception, assume this is ignorable
				try {
					ClassLoader classLoader = Thread.currentThread()
							.getContextClassLoader();
					Class<?> c = Class
							.forName(strClassName, false, classLoader);

					if (!c.isInterface()
							&& !Modifier.isAbstract(c.getModifiers())) {
						bIsSubclass = theClass.isAssignableFrom(c);
					} else {
						bIsSubclass = false;
					}
					if (bIsSubclass) {
						listSubClasses.add(strClassName);
					}
				} catch (Throwable ex) {
					// ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Finds all classes that extend the class, searching in the listAllClasses
	 * ArrayList.
	 * 
	 * @param theClass
	 *            the parent class
	 * @param listAllClasses
	 *            the collection of classes to search in
	 * @param listSubClasses
	 *            the collection of discovered subclasses
	 * @param innerClasses
	 *            indicates whether inners classes should be included in the
	 *            search
	 */
	private static void findAllSubclassesOneClass(Class<?> theClass,
			Set<String> listAllClasses, Set<String> listSubClasses,
			boolean innerClasses, String[] includesList) {

		String strClassName = null;
		boolean bIsSubclass = false;
		Iterator<String> iterClasses = listAllClasses.iterator();
		while (iterClasses.hasNext()) {
			strClassName = iterClasses.next();

			if (!include(strClassName, includesList)) {
				continue;
			}

			// only check classes if they are not inner classes
			// or we intend to check for inner classes
			if ((strClassName.indexOf("$") == -1) || innerClasses) {
				// might throw an exception, assume this is ignorable
				try {
					Class<?> c = Class.forName(strClassName, false, Thread
							.currentThread().getContextClassLoader());

					if (!c.isInterface()
							&& !Modifier.isAbstract(c.getModifiers())) {
						bIsSubclass = theClass.isAssignableFrom(c);
					} else {
						bIsSubclass = false;
					}
					if (bIsSubclass) {
						listSubClasses.add(strClassName);
					}
				} catch (Throwable ignored) {
				}
			}
		}
	}

	/**
	 * 
	 * @param strClassName
	 * @param name
	 * @return
	 */
	private static boolean include(String strClassName, String[] includesList) {

		for (int i = 0; i < includesList.length; i++) {
			String include = includesList[i];
			if (strClassName.indexOf(include) > -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Converts a class file from the text stored in a Jar file to a version
	 * that can be used in Class.forName().
	 * 
	 * @param strClassName
	 *            the class name from a Jar file
	 * @return String the Java-style dotted version of the name
	 */
	private static String fixClassName(String strClassName) {
		strClassName = strClassName.replace('\\', '.');
		strClassName = strClassName.replace('/', '.');
		// remove ".class" from the string
		strClassName = strClassName.substring(0, strClassName.length() - 6);
		return strClassName;
	}

	/**
	 * Finds classes in the one path and adds them to the listClasses set.
	 * 
	 * @param strPath
	 * @param listClasses
	 * @throws IOException
	 */
	private static void findClassesInOnePath(String strPath,
			Set<String> listClasses, List<String> includesList,
			List<String> excludesList) throws IOException {

		File file = new File(strPath);
		if (file.isDirectory()) {
			// its a directory so delegate to another method.
			findClassesInPathsDir(strPath, file, listClasses, includesList,
					excludesList);
		} else if (file.exists()) {
			// it is not a directory and exists.
			// assuming it is a zip or jar.
			findClassesInJar(listClasses, includesList, excludesList, file);
		}
	}

	/**
	 * Goes throught the jar file and adds all the classes in it if necessary.
	 * 
	 * @param listClasses
	 * @param includesList
	 * @param jarFile
	 * @throws ZipException
	 * @throws IOException
	 */
	private static void findClassesInJar(Set<String> listClasses,
			List<String> includesList, List<String> excludesList, File jarFile)
			throws ZipException, IOException {
		ZipFile zipFile = new ZipFile(jarFile);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			String strEntry = entries.nextElement().toString();
			if (strEntry.endsWith(".class")) {
				// if it is a class file, massage the class name
				// and determine if it should be added.
				String className = fixClassName(strEntry);
				if (shouldIncludeClass(className, includesList, excludesList)) {
					listClasses.add(className);
				}
			}
		}
	}

	/**
	 * For each path in the listPaths, calls findClassesInOnePath.
	 * 
	 * @param listPaths
	 * @param listClasses
	 * @throws IOException
	 */
	private static void findClassesInPaths(List<String> listPaths,
			Set<String> listClasses, List<String> includesList,
			List<String> excludesList) throws IOException {

		for (String path : listPaths) {
			findClassesInOnePath(path, listClasses, includesList, excludesList);
		}
	}

	/**
	 * Adds classes found in the strPathElement to the listClasses set.
	 * 
	 * @param strPathElement
	 *            String version of the ROOT of the path. Not the current
	 *            directory. Needed to determine what the class name is.
	 * @param dir
	 *            File object representing the current directory.
	 * @param allClasses
	 *            Set to add classes to.
	 * @throws IOException
	 */
	private static void findClassesInPathsDir(String strPathElement, File dir,
			Set<String> allClasses, List<String> includesPatternList,
			List<String> excludesPatternList) throws IOException {

		String[] list = dir.list();
		for (String childEntry : list) {
			File file = new File(dir, childEntry);
			if (file.isDirectory()) {
				// if entry is another directory, call this method recursively
				// but
				// with the original path value so we can determine the class
				// name.
				findClassesInPathsDir(strPathElement, file, allClasses,
						includesPatternList, excludesPatternList);
			} else if (file.exists() && (file.length() != 0)
					&& childEntry.endsWith(".class")) {

				// startIndex is the length of the path.
				// end index is everything minus the .class
				int startIndex = strPathElement.length() + 1;
				int endIndex = file.getPath().lastIndexOf(".");
				String className = file.getPath().substring(startIndex,
						endIndex);
				// replace "/" or "\" with "." for classname.
				className = className.replace(File.separator.charAt(0), '.');
				// check and make sure the class matches the includes pattern
				// if necessary.
				if (shouldIncludeClass(className, includesPatternList,
						excludesPatternList)) {
					allClasses.add(className);
				}
			}
		}
	}

	/**
	 * Determines if the class should be added to the list. Only matters if
	 * there are include patterns included in the includePatternList.
	 * 
	 * @param className
	 *            to examine.
	 * @param includePatternList
	 *            if not null, checks class to see if its name includes one of
	 *            the String patterns in the list.
	 * @return true if the class should be included in the list.
	 */
	private static boolean shouldIncludeClass(String className,
			List<String> includePatternList, List<String> excludePatternList) {

		if (matchIncludesList(className, includePatternList)) {
			if (matchExcludesList(className, excludePatternList)) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param className
	 * @param excludePatternList
	 * @return
	 */
	private static boolean matchExcludesList(String className,
			List<String> excludePatternList) {

		if ((excludePatternList == null) || (excludePatternList.size() == 0)) {
			return false;
		}

		for (String excludePattern : excludePatternList) {
			if (className.indexOf(excludePattern) > -1) {
				// match
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param className
	 * @param includePatternList
	 * @return
	 */
	private static boolean matchIncludesList(String className,
			List<String> includePatternList) {

		if ((includePatternList == null) || (includePatternList.size() == 0)) {
			return true;
		}

		for (String includePattern : includePatternList) {
			if (className.indexOf(includePattern) > -1) {
				// match
				return true;
			}
		}
		return false;
	}

}
