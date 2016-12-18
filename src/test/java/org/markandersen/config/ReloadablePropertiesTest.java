package org.markandersen.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.markandersen.io.file.FileInterface;
import org.markandersen.test.BaseTestCase;

public class ReloadablePropertiesTest extends BaseTestCase {

	private String fileName1 = "reloadable.properties";
	private String fileName2 = "reloadableNew.properties";

	private String resourceName = "org/markandersen/config/" + fileName1;
	private String tempResourceName = "org/markandersen/config/" + fileName2;
	private File tempDirectory = new File("./target/tmp");

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// FileUtils.cleanDirectory(tempDirectory );
	}

	/**
	 * 
	 */
	@Ignore
	public void __testGetInstance() {

		ReloadableProperties2 instance = ReloadableProperties2
				.getClasspathInstance(resourceName, 5 * 1000);
		assertEquals("testValue1", instance.getProperty("testKey1"));
		assertEquals("testValue2", instance.getProperty("testKey2"));

	}

	/**
	 * Load the initial properties file, update the file and see that the
	 * properties were reloaded.
	 */
	public void testGetInstanceReload() throws Exception {

		File srcFile = new File("./src/test/resources/" + resourceName);
		FileUtils.copyFileToDirectory(srcFile, tempDirectory);

		int interval = 5 * 1000;
		// ReloadableProperties instance = ReloadableProperties
		// .getClasspathInstance(testFile , 5 * 1000);

		// TestFileInterface testFile = new TestFileInterface();
		// InputStream is = new
		// ByteArrayInputStream("testKey1=testValue1\ntestKey2=testValue2"
		// .getBytes());
		// testFile.setInputStream(is);
		File file = new File(tempDirectory + "/" + srcFile.getName());
		FileInterface testFile = new FileInterfaceImpl(file);
		ReloadableProperties2 instance = new ReloadableProperties2(testFile,
				interval);

		assertEquals("testValue1", instance.getProperty("testKey1"));
		assertEquals("testValue2", instance.getProperty("testKey2"));

		// update the file.
		File newSrcFile = new File("./src/test/resources/" + tempResourceName);
		FileUtils.copyFile(newSrcFile, file);

		assertEquals("testValue1", instance.getProperty("testKey1"));
		assertEquals("testValue1", instance.getProperty("testKey2"));

	}
}
