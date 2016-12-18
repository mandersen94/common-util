package org.markandersen.metric;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

/**
 * 
 * @author e63582
 */
public class MetricProxy {

	private static final Logger logger = Logger.getLogger(MetricProxy.class);

	/**
	 * 
	 * @param classloader
	 * @param interfaces
	 * @param targetObject
	 * @return
	 */
	public static Object wrapInterface(ClassLoader classloader,
			Class[] interfaces, Object targetObject) {

		InvocationHandler handler = new MetricsCounterProxy(targetObject);
		return Proxy.newProxyInstance(classloader, interfaces, handler);
	}

	/**
	 * 
	 * @author e63582
	 */
	static class MetricsCounterProxy implements InvocationHandler {

		private Object targetObject;

		private String className;

		private MetricManager metricManager;

		/**
		 * 
		 * @param targetObject
		 * @param server
		 */
		public MetricsCounterProxy(Object targetObject) {
			this.targetObject = targetObject;
			this.className = targetObject.getClass().getName();
			this.metricManager = MetricManager.getInstance();
		}

		/**
		 * 
		 */
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			String name = method.getName();
			Class[] parameterTypes = method.getParameterTypes();
			String otherParams = formatMethodArgs(parameterTypes);
			SingleMetric metric = new SingleMetric(className, name
					+ otherParams);
			try {
				return method.invoke(targetObject, args);
			} catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			} catch (Exception ex) {
				logger.error("Caught unexpected exception in MetricProxy.", ex);
				throw ex;
			} finally {
				metric.finish();
				metricManager.record(metric);
			}
		}

		/**
		 * 
		 * @param parameterTypes
		 * @return
		 */
		private String formatMethodArgs(Class[] parameterTypes) {
			StringBuffer otherParams = new StringBuffer("(");
			for (int i = 0; i < parameterTypes.length; i++) {
				otherParams.append(parameterTypes[i]).append(";");
			}
			otherParams.append(")");
			return otherParams.toString();
		}

	}
}
