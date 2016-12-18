package org.markandersen.service.invocation;

import java.util.Arrays;
import java.util.List;

import org.markandersen.service.invocation.ClientInvocationFramework;
import org.markandersen.service.invocation.InvocationResultHandler;
import org.markandersen.service.invocation.InvocationStrategyFactory;
import org.markandersen.service.invocation.InvocationStrategyInstanceFactory;
import org.markandersen.service.invocation.StubInvocationResultHandler;
import org.markandersen.test.SimpleOperation;
import org.markandersen.test.SimpleOperationImpl;


/**
 * DOCUMENT ME!
 */
public class RoundRobinPrimaryStickySecondaryExample {

	/**
	 * DOCUMENT ME!
	 * 
	 * @param args
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		List references1 = Arrays.asList(new SimpleOperation[] {
				new SimpleOperationImpl(), new SimpleOperationImpl(),
				new SimpleOperationImpl() });
		List references2 = Arrays.asList(new SimpleOperation[] {
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
		Class[] effectiveClasses = new Class[] { SimpleOperation.class };
		InvocationResultHandler handler = new StubInvocationResultHandler(true,
				true);
		ClientInvocationFramework invocationFwk = new ClientInvocationFramework(
				invocationStrategy, handler, effectiveClasses,
				RoundRobinPrimaryStickySecondaryExample.class.getClassLoader());
		SimpleOperation proxiedObject = (SimpleOperation) invocationFwk
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