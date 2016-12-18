package org.markandersen.j2ee.servlet;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

public class LogSysOutSessionAttributeListener implements HttpSessionAttributeListener {

	public LogSysOutSessionAttributeListener() {
		System.out.println("SessionAttributeListener.<init>");
	}

	public void attributeAdded(HttpSessionBindingEvent event) {
		String name = event.getName();
		System.out
				.println("SessionAttributeListener.attributeAdded(): attributeName "
						+ name);
	}

	public void attributeRemoved(HttpSessionBindingEvent event) {
		String name = event.getName();
		System.out
				.println("SessionAttributeListener.attributeRemoved(): attributeName "
						+ name);

	}

	public void attributeReplaced(HttpSessionBindingEvent event) {
		String name = event.getName();
		System.out
				.println("SessionAttributeListener.attributeReplaced(): attributeName "
						+ name);

	}

}
