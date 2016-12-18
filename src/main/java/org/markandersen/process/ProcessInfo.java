package org.markandersen.process;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author mandersen
 */
class ProcessInfo {

	private List<String> cmdarray;

	private Process process;

	private ProcessWatcherThread watcherThread;

	private ProcessOutputWatcherThread outputThread;

	private File baseDir;

	private Map<String, String> env;

	public ProcessInfo(File baseDir, List<String> cmdarray,
			Map<String, String> env, Process process,
			ProcessWatcherThread watcherThread,
			ProcessOutputWatcherThread outputThread) {
		this.baseDir = baseDir;
		this.cmdarray = cmdarray;
		this.env = env;
		this.process = process;
		this.watcherThread = watcherThread;
		this.outputThread = outputThread;
	}

	public List<String> getCmdarray() {
		return cmdarray;
	}

	public void setCmdarray(List<String> cmdarray) {
		this.cmdarray = cmdarray;
	}

	public ProcessOutputWatcherThread getOutputThread() {
		return outputThread;
	}

	public void setOutputThread(ProcessOutputWatcherThread outputThread) {
		this.outputThread = outputThread;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public ProcessWatcherThread getWatcherThread() {
		return watcherThread;
	}

	public void setWatcherThread(ProcessWatcherThread watcherThread) {
		this.watcherThread = watcherThread;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	public Map<String, String> getEnv() {
		return env;
	}

	public void setEnv(Map<String, String> env) {
		this.env = env;
	}

}