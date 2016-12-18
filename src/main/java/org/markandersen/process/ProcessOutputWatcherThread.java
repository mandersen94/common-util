package org.markandersen.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class ProcessOutputWatcherThread extends Thread {

	private Process process;

	private PrintStream out;

	private String logPrefix;

	private ProcessOutputWatcherCallback callback;

	public ProcessOutputWatcherThread(Process process, PrintStream out,
			String logPrefix, ProcessOutputWatcherCallback callback) {
		this.process = process;
		this.out = out;
		this.logPrefix = logPrefix;
		this.callback = callback;
		setDaemon(true);
	}

	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(process
				.getInputStream()));
		try {
			String line = br.readLine();
			while (line != null) {
				out.println(logPrefix + line);
				if (callback != null) {
					callback.outputWritten(line);
				}
				line = br.readLine();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {

			}
		}
	}
}
