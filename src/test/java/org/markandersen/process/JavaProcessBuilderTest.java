package org.markandersen.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.markandersen.process.JavaProcessBuilder;
import org.markandersen.process.ProcessManager;
import org.markandersen.process.ProcessTestMain;
import org.markandersen.test.BaseTestCase;


/**
 * 
 * @author mandersen
 */
public class JavaProcessBuilderTest extends BaseTestCase {

	protected String windowsExe = "ipconfig";

	protected String unixExe = "/sbin/ifconfig";

	protected String exe;

	protected Process process;

	protected ProcessManager processManager = ProcessManager
			.getDefaultProcessManager();

	private String buildClassMainLocationEclipse = "./eclipse-classes/main";

	private String buildClassMainLocationAnt = "./build/main/classes";

	private String buildClassMainLocationMaven = "./target/classes";

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String osName = System.getProperty("os.name");
		System.out.println("os.name = " + osName);
		if (osName.startsWith("Windows")) {
			exe = windowsExe;
		} else {
			exe = unixExe;
		}
		System.out.println("exe set to " + exe);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (process != null) {
			process.destroy();
		}
		ProcessManager.getDefaultProcessManager().killAllProcesses();

	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testCreateProcess() throws Exception {

		ProcessBuilder builder = new ProcessBuilder(exe);
		builder.redirectErrorStream(true);
		System.out.println("process line = " + builder.command());

		process = builder.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(process
				.getInputStream()));

		String line = br.readLine();
		while (line != null) {
			System.out.println(line);
			line = br.readLine();
		}
		process.destroy();
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void testCreateJavaProcessExecutableJar() throws IOException {
		JavaProcessBuilder javaProcess = new JavaProcessBuilder();
		String javaHome = System.getenv("JAVA_HOME");
		String javaExe = javaHome + File.separator + "bin" + File.separator
				+ "java";
		if (!new File(javaExe).exists()) {
			javaExe = javaHome + File.separator + "java";
			if (!new File(javaExe).exists()) {
				javaExe = null;
			}
		}
		if (javaExe != null) {
			javaProcess.setJavaExe(javaExe);
		}
		javaProcess.setUseMainJar(true);
		String baseDir = System.getProperty("user.dir");
		File jarFile = new File(baseDir + File.separator + "lib"
				+ File.separator + "processTest.jar");
		javaProcess.setMainJar(jarFile.getPath());
		String testArg1 = "my first argument";
		String testArg2 = "my second argument";
		String[] appArgs = new String[] { testArg1, testArg2 };
		javaProcess.addJavaProgramArg(testArg1);
		javaProcess.addJavaProgramArg(testArg2);
		javaProcess.setBaseDir(new File(baseDir));
		String processName = "process1";
		javaProcess.setProcessName(processName);
		javaProcess.setDumpOutputToStdout(false);
		process = javaProcess.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(process
				.getInputStream()));

		String line = br.readLine();
		System.out.println(line);
		assertEquals(ProcessTestMain.SYSOUT_TEST_MESSAGE, line);

		line = br.readLine();
		System.out.println(line);
		assertEquals(ProcessTestMain.SYSERR_TEST_MESSAGE, line);

		line = br.readLine();
		System.out.println(line);
		assertEquals(ProcessTestMain.ARGS_TITLE, line);

		line = br.readLine();
		System.out.println(line);
		assertEquals(Arrays.asList(appArgs).toString(), line);

		line = br.readLine();
		System.out.println(line);
		assertEquals(ProcessTestMain.END_PROCESS_MESSAGE, line);

		assertSame(process, processManager.getProcess(processName));

	}

	/**
	 * 
	 * @throws IOException
	 */
	public void testCreateJavaProcessUsingClass() throws IOException {
		JavaProcessBuilder javaProcess = new JavaProcessBuilder();

		String javaHome = System.getenv("JAVA_HOME");
		String javaExe = javaHome + File.separator + "bin" + File.separator
				+ "java";
		if (!new File(javaExe).exists()) {
			javaExe = javaHome + File.separator + "java";
			if (!new File(javaExe).exists()) {
				javaExe = null;
			}
		}
		if (javaExe != null) {
			javaProcess.setJavaExe(javaExe);
		}

		String baseDir = System.getProperty("user.dir");
		String className = ProcessTestMain.class.getName();
		javaProcess.setMainClass(className);
		String[] classpath = new String[] { buildClassMainLocationEclipse,
				buildClassMainLocationAnt };

		javaProcess.addClasspath(buildClassMainLocationEclipse);
		javaProcess.addClasspath(buildClassMainLocationAnt);
		javaProcess.addClasspath(buildClassMainLocationMaven);

		String testArg1 = "my first argument";
		String testArg2 = "my second argument";
		String[] appArgs = new String[] { testArg1, testArg2 };
		javaProcess.addJavaProgramArg(testArg1);
		javaProcess.addJavaProgramArg(testArg2);
		javaProcess.setBaseDir(new File(baseDir));
		String processName = "process2";
		javaProcess.setProcessName(processName);
		javaProcess.setDumpOutputToStdout(false);
		process = javaProcess.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(process
				.getInputStream()));

		String line = br.readLine();
		System.out.println(line);
		assertEquals(ProcessTestMain.SYSOUT_TEST_MESSAGE, line);

		line = br.readLine();
		System.out.println(line);
		assertEquals(ProcessTestMain.SYSERR_TEST_MESSAGE, line);

		line = br.readLine();
		System.out.println(line);
		assertEquals(ProcessTestMain.ARGS_TITLE, line);

		line = br.readLine();
		System.out.println(line);
		assertEquals(Arrays.asList(appArgs).toString(), line);

		line = br.readLine();
		System.out.println(line);
		assertEquals(ProcessTestMain.END_PROCESS_MESSAGE, line);

		assertSame(process, processManager.getProcess(processName));
	}

}
