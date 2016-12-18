package org.markandersen.service.invocation.ha.sample;

import org.markandersen.process.BaseProcessOutputWatcherCallback;

public class CalculatorServerProcessOutputWatcher extends
		BaseProcessOutputWatcherCallback {

	/**
	 * 
	 * @return
	 */
	public String getLogMessageMatch() {
		return CalculatorService.SUCCESSFULLY_STARTED_MESSAGE;
	}

}
