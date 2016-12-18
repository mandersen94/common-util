package org.markandersen.invocation;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.markandersen.invocation.Interceptor;
import org.markandersen.invocation.InterceptorFramework;
import org.markandersen.invocation.POJOInvoker;
import org.markandersen.test.SimpleOperation;


/**
 * 
 * @author Mark Andersen
 */
public class InvocationFrameworkTest extends TestCase {

	protected MockControl<SimpleOperation> targetMockControl;

	protected SimpleOperation targetMock;

	protected String arg1 = "arg1";

	protected String arg2 = "arg2";

	protected String expected = arg1 + arg2;

	private StubInterceptor interceptor1;

	private StubInterceptor interceptor2;

	@SuppressWarnings("deprecation")
	public void setUp() throws Exception {
		super.setUp();
		targetMockControl = MockControl.createControl(SimpleOperation.class);
		targetMock = targetMockControl.getMock();

		interceptor1 = new StubInterceptor();
		interceptor2 = new StubInterceptor();
	}

	/**
	 * @throws Exception
	 */
	public void testCreateInterceptorInvocationNoInterceptors()
			throws Exception {

		targetMock.concatString(arg1, arg2);
		targetMockControl.setReturnValue(expected);
		targetMockControl.replay();

		Class<?>[] interfaces = new Class[] { SimpleOperation.class };
		List<Interceptor> interceptors = new ArrayList<Interceptor>();

		SimpleOperation proxy = (SimpleOperation) InterceptorFramework
				.createInterceptorInvocation(new POJOInvoker(targetMock),
						targetMock.getClass().getClassLoader(), interfaces,
						interceptors);

		String result = proxy.concatString(arg1, arg2);
		assertEquals("wrong return value.", expected, result);
		targetMockControl.verify();
	}

	/**
	 * @throws Throwable
	 */
	public void testCreateInterceptorInvocationNoInterceptorsMultipleInterceptors()
			throws Throwable {

		Class<?>[] interfaces = new Class[] { SimpleOperation.class };
		List<Interceptor> interceptors = new ArrayList<Interceptor>();
		interceptors.add(interceptor1);
		interceptors.add(interceptor2);

		targetMock.concatString(arg1, arg2);
		targetMockControl.setReturnValue(expected);
		targetMockControl.replay();

		SimpleOperation proxy = (SimpleOperation) InterceptorFramework
				.createInterceptorInvocation(new POJOInvoker(targetMock),
						targetMock.getClass().getClassLoader(), interfaces,
						interceptors);

		String result = proxy.concatString(arg1, arg2);
		assertEquals("wrong return value.", expected, result);
		assertTrue("interceptor1 not called.", interceptor1.isInvoked());
		assertTrue("interceptor2 not called.", interceptor2.isInvoked());
		targetMockControl.verify();
	}
}
