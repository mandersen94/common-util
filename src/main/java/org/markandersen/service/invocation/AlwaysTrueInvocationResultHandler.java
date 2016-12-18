package org.markandersen.service.invocation;

public class AlwaysTrueInvocationResultHandler implements
		InvocationResultHandler {

	public boolean didInvocationSucceedException(Throwable ex) {
		return true;
	}

	public boolean didInvocationSucceedReturnValue(Object result) {
		return true;
	}

}
