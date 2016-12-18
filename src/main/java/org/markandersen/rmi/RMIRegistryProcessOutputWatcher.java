package org.markandersen.rmi;

import org.markandersen.process.BaseProcessOutputWatcherCallback;

/**
 * 
 * @author Mark Andersen
 */
public class RMIRegistryProcessOutputWatcher extends
		BaseProcessOutputWatcherCallback {

	/**
	 * 
	 * @see org.markandersen.process.ProcessOutputWatcherCallback#getLogMessageMatch()
	 */
	public String getLogMessageMatch() {
		return RMIRegistryService.SUCCESSFULLY_STARTED_MESSAGE;
	}

}
