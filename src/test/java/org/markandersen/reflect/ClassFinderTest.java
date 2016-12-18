package org.markandersen.reflect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.markandersen.reflect.ClassFinder;
import org.markandersen.test.BaseTestCase;
import org.markandersen.test.ThreadRunnable;

import junit.framework.TestCase;


public class ClassFinderTest extends BaseTestCase {

	List<String> paths = new ArrayList<String>();

	Class[] superClasses;

	String eclipseBaseTestPath = "./eclipse-classes/test";

	String eclipseBaseTestPath2 = "./bin";

	String gradleMainClassPath = "./build/classes/main";

	String gradleTestClassPath = "./build/classes/test";

	String antBaseTestPath = "./build/test/classes";

	String mavenBaseTestPath = "./target/test-classes";

	/**
	 *
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		paths.add(eclipseBaseTestPath);
		paths.add(eclipseBaseTestPath2);
		paths.add(gradleMainClassPath);
		paths.add(gradleTestClassPath);
		paths.add(antBaseTestPath);
		paths.add(mavenBaseTestPath);
		superClasses = new Class[] { TestCase.class };
	}

	/**
	 *
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void testFindClassesGoodCase() throws IOException,
			ClassNotFoundException {

		List<String> classNames = ClassFinder.findClassesThatExtend(paths
				.toArray(new String[0]), superClasses, false, null, null,
				false, null);
		String expectedClass = "org.markandersen.reflect.ClassFinderTest";
		assertTrue("couldn't find test class.", classNames
				.contains(expectedClass));
	}

	/**
	 *
	 * @throws Exception
	 */
	public void testFindClassesNoResults() throws Exception {
		List<String> results = ClassFinder.findClassesThatExtend(paths
				.toArray(new String[0]), new Class[] { ThreadRunnable.class },
				false, null, null, false, null);
		assertEquals("results found when they shouldn't have been.", 0, results
				.size());
	}
}
