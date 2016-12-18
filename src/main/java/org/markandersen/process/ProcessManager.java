package org.markandersen.process;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 
 * @author Mark Andersen
 */
public class ProcessManager {

	private static final Logger logger = Logger.getLogger(ProcessManager.class);

	/** default process manager. */
	private static final ProcessManager defaultProcessManager = new ProcessManager();

	/** map of names and processes. */
	private Map<String, ProcessInfo> processes = new HashMap<String, ProcessInfo>();

	private static final long PROCESS_KILL_WAIT_TIME = 500;

	static {
		logger.info("Registering shutdown hook to kill all managed processes.");
		Runtime.getRuntime().addShutdownHook(new ProcessKillerShutdownHook());
	}

	/**
	 * Register a new process without a given name.
	 * 
	 * @param cmdarray
	 * @param env
	 * @param process
	 * @param dumpOutputToStdout
	 */
	public synchronized void registerProcess(String processName, File baseDir,
			List<String> cmdarray, Map<String, String> env, Process process,
			boolean dumpOutputToStdout, ProcessOutputWatcherCallback callback) {
		ProcessWatcherThread watcherThread = new ProcessWatcherThread(process);
		watcherThread.start();
		ProcessOutputWatcherThread outputThread = null;
		if (dumpOutputToStdout) {
			outputThread = new ProcessOutputWatcherThread(process, System.out,
					"process[" + processName + "]: ", callback);
			outputThread.start();
		}
		processes.put(processName, new ProcessInfo(baseDir, cmdarray, env,
				process, watcherThread, outputThread));
	}

	/**
	 * 
	 * @param processName
	 * @return
	 */
	public synchronized Process getProcess(String processName) {
		ProcessInfo info = processes.get(processName);
		if (info == null) {
			return null;
		}
		return info.getProcess();
	}

	/**
	 * 
	 * 
	 */
	public synchronized void killAllProcesses() {
		Set<String> processNames = new HashSet<String>(processes.keySet());
		for (String processName : processNames) {
			killProcess(processName);
			processes.remove(processName);
		}
	}

	/**
	 * set of all processes.
	 * 
	 * @return
	 */
	public synchronized Set<ProcessInfo> getAllProcesses() {
		return new HashSet<ProcessInfo>(processes.values());
	}

	/**
	 * 
	 * @param processName
	 * @return
	 */
	public synchronized Process killProcess(String processName) {

		ProcessInfo processInfo = processes.remove(processName);
		if (processInfo == null) {
			throw new IllegalArgumentException(
					"no process found with the name " + processName);
		}

		Process process = processInfo.getProcess();
		if (process != null) {
			logger.info("Killing process " + processName + ".  baseDir = "
					+ processInfo.getBaseDir() + ", cmdLine = "
					+ processInfo.getCmdarray() + ", env = "
					+ processInfo.getEnv());
			process.destroy();
			try {
				Thread.sleep(PROCESS_KILL_WAIT_TIME);
			} catch (InterruptedException e) {
			}
		}
		processInfo.getWatcherThread().interrupt();
		ProcessOutputWatcherThread outputThread = processInfo.getOutputThread();
		if (outputThread != null) {
			outputThread.interrupt();
		}
		return process;
	}

	/**
	 * 
	 * @return
	 */
	public static ProcessManager getDefaultProcessManager() {
		return defaultProcessManager;
	}

	/**
	 * is the process still alive.
	 * 
	 * @param processName
	 * @return
	 */
	public boolean isAlive(String processName) {
		ProcessInfo info = processes.get(processName);
		return info.getWatcherThread().isProcessAlive();
	}

	/**
	 * 
	 */
	static class ProcessKillerShutdownHook extends Thread {

		private static Logger hookLogger = Logger
				.getLogger(ProcessKillerShutdownHook.class);

		@Override
		public void run() {
			try {
				hookLogger.info("running ProcessManager shutdown hook.");
				hookLogger.info("Killing all managed processes.");
				ProcessManager manager = ProcessManager
						.getDefaultProcessManager();
				hookLogger.info(manager.getAllProcesses().size()
						+ " processes registered.");
				manager.killAllProcesses();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
