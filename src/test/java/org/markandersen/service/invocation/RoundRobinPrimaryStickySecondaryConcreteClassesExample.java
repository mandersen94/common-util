package org.markandersen.service.invocation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.markandersen.service.invocation.ClientInvocationFramework;
import org.markandersen.service.invocation.InvocationResultHandler;
import org.markandersen.service.invocation.InvocationStrategyFactory;
import org.markandersen.service.invocation.InvocationStrategyInstanceFactory;
import org.markandersen.service.invocation.StubInvocationResultHandler;
import org.markandersen.test.SimpleOperationImpl;


/**
 * DOCUMENT ME!
 */
public class RoundRobinPrimaryStickySecondaryConcreteClassesExample {

	/**
	 * DOCUMENT ME!
	 * 
	 * @param args
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		List references1 = Arrays.asList(new SimpleOperationImpl[] {
				new SimpleOperationImpl(), new SimpleOperationImpl(),
				new SimpleOperationImpl() });
		List references2 = Arrays.asList(new SimpleOperationImpl[] {
				new SimpleOperationImpl(), new SimpleOperationImpl() });

		// EnhancedInvocationStrategyFactory[] factories = new
		// EnhancedInvocationStrategyFactory[2];
		// factories[0] = new RoundRobinInvocationStrategyFactory(references1);
		// factories[1] = new StickyInvocationStrategyFactory(references2);
		// InvocationStrategyInstanceFactory invocationStrategy = new
		// AggregateInvocationStrategyInstanceFactory(
		// factories);
		InvocationStrategyInstanceFactory invocationStrategy = InvocationStrategyFactory
				.createRoundRobinInstance(references1, references2);
		Class[] effectiveClasses = new Class[] { SimpleOperationImpl.class,
				Serializable.class };
		InvocationResultHandler handler = new StubInvocationResultHandler(true,
				true);
		ClientInvocationFramework invocationFwk = new ClientInvocationFramework(
				invocationStrategy, handler, effectiveClasses,
				RoundRobinPrimaryStickySecondaryConcreteClassesExample.class
						.getClassLoader());
		SimpleOperationImpl proxiedObject = (SimpleOperationImpl) invocationFwk
				.createInvocationManager();
		proxiedObject.concatString("sdaf", "asdewr");
		proxiedObject.concatString("sdaf", "asdewr");
		proxiedObject.concatString("sdaf", "asdewr");
		proxiedObject.concatString("sdaf", "asdewr");
		proxiedObject.concatString("sdaf", "asdewr");
		proxiedObject.concatString("sdaf", "asdewr");
		proxiedObject.concatString("sdaf", "asdewr");
		proxiedObject.concatString("sdaf", "asdewr");
	}
}