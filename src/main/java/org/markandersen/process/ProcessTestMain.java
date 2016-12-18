package org.markandersen.process;

import java.util.Arrays;

/**
 * Class with main to test for java process builder.
 * 
 * @author mandersen
 */
public class ProcessTestMain {

	public static final String SYSOUT_TEST_MESSAGE = "testing standard out";

	public static final String SYSERR_TEST_MESSAGE = "testing standard err";

	public static final String ARGS_TITLE = "Start arguments;";

	public static final String END_PROCESS_MESSAGE = "End process.";

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(SYSOUT_TEST_MESSAGE);

		System.out.println(SYSERR_TEST_MESSAGE);

		System.out.println(ARGS_TITLE);

		System.out.println(Arrays.asList(args));

		System.out.println(END_PROCESS_MESSAGE);
	}
}
