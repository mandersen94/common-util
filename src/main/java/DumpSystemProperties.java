import java.util.Iterator;
import java.util.Set;

public class DumpSystemProperties {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("System Properties:");
		Set<Object> set = System.getProperties().keySet();
		Iterator<Object> iter = System.getProperties().keySet().iterator();
		for (Object object : set) {
			String key = (String) object;
			String value = System.getProperty(key);
			System.out.printf("%s = %s\n", key, value);
		}
	}

}
