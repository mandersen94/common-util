package org.markandersen.threads;

import java.lang.reflect.Field;

public class ThreadLocalUtil {

	private static Field threadLocalField;

	private static Field inheritableThreadLocalField;

	static {
		try {
			threadLocalField = Thread.class.getDeclaredField("threadLocals");
			threadLocalField.setAccessible(true);

			inheritableThreadLocalField = Thread.class
					.getDeclaredField("inheritableThreadLocals");
			inheritableThreadLocalField.setAccessible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object getThreadLocals() {
		Thread thread = Thread.currentThread();
		try {
			Object value = threadLocalField.get(thread);
			return value;
		} catch (Exception e) {
			// wrap and rethrow
			throw new RuntimeException("Problems getting thread local map.", e);
		}

	}

	public static Object getInheritableThreadLocals() {
		Thread thread = Thread.currentThread();
		try {
			Object value1 = inheritableThreadLocalField.get(thread);
			return value1;
		} catch (Exception e) {
			// wrap and rethrow
			throw new RuntimeException(
					"Problems getting inheritable thread local map.", e);
		}

	}

	public static void setThreadLocals(Object threadLocals) {
		try {
			Thread thread = Thread.currentThread();
			threadLocalField.set(thread, threadLocals);
		} catch (Exception e) {
			// wrap and rethrow
			throw new RuntimeException("Problems setting thread local map.", e);
		}
	}

	public static void setInheritableThreadLocals(Object threadLocals) {
		try {
			Thread thread = Thread.currentThread();
			inheritableThreadLocalField.set(thread, threadLocals);
		} catch (Exception e) {
			// wrap and rethrow
			throw new RuntimeException("Problems setting thread local map.", e);
		}
	}

}
