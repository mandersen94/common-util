package org.markandersen.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author Mark Andersen
 */
public class JavaProcessBuilder {

	private static final String DEFAULT_JAVA_EXE = "java";

	private boolean useMainJar = false;

	private List<String> javaProgramArgs = new ArrayList<String>();

	private List<String> classpath = new ArrayList<String>();

	private List<String> javaSystemArgs = new ArrayList<String>();

	private String mainJar;

	private String mainClass;

	private File baseDir;

	private String javaExe;

	private Logger logger = Logger.getLogger(JavaProcessBuilder.class);

	private boolean redirectErrorStream = true;

	private static File defaultBaseDir;

	private ProcessManager processManager;

	private String processName;

	private boolean dumpOutputToStdout = true;

	private ProcessOutputWatcherCallback callback;

	private List<String> bootClasspathPrepend = new ArrayList<String>();

	static {
		String baseDir = System.getProperty("user.dir");
		defaultBaseDir = new File(baseDir);
	}

	/**
	 * 
	 * 
	 */
	public JavaProcessBuilder() {

	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Process start() throws IOException {
		return start(ProcessManager.getDefaultProcessManager());
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Process start(ProcessManager manager) throws IOException {

		checkOptions();
		List<String> cmdarray = buildCmdArray();
		ProcessBuilder pb = new ProcessBuilder(cmdarray);
		pb.redirectErrorStream(redirectErrorStream);
		File dir = baseDir == null ? defaultBaseDir : baseDir;
		pb.directory(dir);
		Map<String, String> env = pb.environment();
		if (manager.getProcess(processName) != null) {
			throw new IOException("process already registered under the name "
					+ processName);
		}
		logger.info("starting process.  baseDir = " + dir.getPath()
				+ ", cmdline = " + cmdarray);
		Process process = pb.start();
		manager.registerProcess(processName, dir, cmdarray, env, process,
				dumpOutputToStdout, callback);
		return process;
	}

	/**
	 * 
	 * 
	 */
	private void checkOptions() {
		if (processName == null) {
			throw new IllegalArgumentException("processName must be set.");
		}
	}

	/**
	 * Builds up command array.
	 * 
	 * <java.exe> <java-args> -cp <CLASSPATH> <main-class> <program args>
	 * <java.exe> <java-args> -cp <CLASSPATH> -jar <main-jar> <program args>
	 * 
	 * @return
	 */
	private List<String> buildCmdArray() {
		List<String> cmd = new ArrayList<String>();

		String javaCmd = javaExe == null ? DEFAULT_JAVA_EXE : javaExe;
		cmd.add(javaCmd);

		if (!bootClasspathPrepend.isEmpty()) {
			StringBuilder buf = new StringBuilder();
			buf.append("-Xbootclasspath/p:");
			for (String classpath : bootClasspathPrepend) {
				buf.append(classpath);
				buf.append(File.pathSeparator);
			}
			cmd.add(buf.toString());
		}

		// java system args
		if (javaSystemArgs != null) {
			for (String systemArg : javaSystemArgs) {
				cmd.add(systemArg);
			}
		}

		// classpath
		if ((classpath != null) && (!classpath.isEmpty())) {
			cmd.add("-cp");
			StringBuilder buf = new StringBuilder();
			for (String singleClasspath : classpath) {
				buf.append(singleClasspath);
				buf.append(File.pathSeparator);
			}
			cmd.add(buf.toString());
		}

		// main class or main jar
		if (useMainJar) {
			cmd.add("-jar");
			cmd.add(mainJar);
		} else {
			// add main class.
			cmd.add(mainClass);
		}

		// add program args.
		for (String progArg : javaProgramArgs) {
			cmd.add(progArg);
		}
		return cmd;
	}

	public boolean isUseMainJar() {
		return useMainJar;
	}

	public void setUseMainJar(boolean useMainJar) {
		this.useMainJar = useMainJar;
	}

	public String getMainJar() {
		return mainJar;
	}

	public void setMainJar(String mainJar) {
		this.mainJar = mainJar;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	public String getJavaExe() {
		return javaExe;
	}

	public void setJavaExe(String javaExe) {
		this.javaExe = javaExe;
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public boolean isRedirectErrorStream() {
		return redirectErrorStream;
	}

	public void setRedirectErrorStream(boolean redirectErrorStream) {
		this.redirectErrorStream = redirectErrorStream;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public boolean isDumpOutputToStdout() {
		return dumpOutputToStdout;
	}

	public void setDumpOutputToStdout(boolean dumpOutputToStdout) {
		this.dumpOutputToStdout = dumpOutputToStdout;
	}

	public ProcessOutputWatcherCallback getCallback() {
		return callback;
	}

	public void setCallback(ProcessOutputWatcherCallback callback) {
		this.callback = callback;
	}

	public List<String> getJavaProgramArgs() {
		return javaProgramArgs;
	}

	public void setJavaProgramArgs(List<String> javaProgramArgs) {
		this.javaProgramArgs = javaProgramArgs;
	}

	public void addJavaProgramArg(String arg) {
		javaProgramArgs.add(arg);
	}

	public List<String> getJavaSystemArgs() {
		return javaSystemArgs;
	}

	public void setJavaSystemArgs(List<String> javaSystemArgs) {
		this.javaSystemArgs = javaSystemArgs;
	}

	public void addJavaSystemArg(String arg) {
		javaSystemArgs.add(arg);
	}

	public void addClasspath(String oneClasspath) {
		classpath.add(oneClasspath);
	}

	public List<String> getBootClasspathPrepend() {
		return bootClasspathPrepend;
	}

	public void setBootClasspathPrepend(List<String> bootClasspathsPrepend) {
		this.bootClasspathPrepend = bootClasspathsPrepend;
	}

	public void addBootClasspathPrepend(String theClasspath) {
		bootClasspathPrepend.add(theClasspath);
	}

}
