package org.markandersen.service.invocation.ha;

public class ViewContext {

	private static final ThreadLocal<View> singleton = new ThreadLocal<View>();

	public static void setView(View value) {
		singleton.set(value);
	}

	public static View getView() {
		return singleton.get();
	}

	public static void removeView() {
		singleton.remove();
	}
}
