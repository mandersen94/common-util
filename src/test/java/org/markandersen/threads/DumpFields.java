package org.markandersen.threads;

import java.lang.reflect.Field;

public class DumpFields {

	public static void dumpFields(Object object) {

		Class<? extends Object> class1 = object.getClass();
		Field[] fields = class1.getDeclaredFields();
		for (Field temp : fields) {
			try {
				System.out.println("/***************************************/");
				System.out.println("field name = " + temp.getName());
				temp.setAccessible(true);
				System.out.println("value = " + temp.get(object));
				System.out.println("modifiers = " + temp.toGenericString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
