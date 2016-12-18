package org.markandersen.jmx.notification;

import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;

public class GenericNotificationTestProcess {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GenericNotificationTestProcess tester = new GenericNotificationTestProcess();
		tester.startListenig();
	}

	private void startListenig() {
		try {
			GenericNotificationService.start();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		}

	}
}
