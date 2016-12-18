package org.markandersen.threads;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.RejectedExecutionHandler;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class ThreadLocalThreadPoolExecutor extends ThreadPoolExecutor {

	public ThreadLocalThreadPoolExecutor(int arg0, int arg1, long arg2,
			TimeUnit arg3, BlockingQueue arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
	}

	public ThreadLocalThreadPoolExecutor(int arg0, int arg1, long arg2,
			TimeUnit arg3, BlockingQueue arg4, ThreadFactory arg5) {
		super(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public ThreadLocalThreadPoolExecutor(int arg0, int arg1, long arg2,
			TimeUnit arg3, BlockingQueue arg4, RejectedExecutionHandler arg5) {
		super(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public ThreadLocalThreadPoolExecutor(int arg0, int arg1, long arg2,
			TimeUnit arg3, BlockingQueue arg4, ThreadFactory arg5,
			RejectedExecutionHandler arg6) {
		super(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public void execute(Runnable runnable) {

		// wrap the task and pass it up.
		ThreadLocalPropagatorRunnableWrapper wrapper = new ThreadLocalPropagatorRunnableWrapper(
				runnable);
		super.execute(wrapper);
	}

}
