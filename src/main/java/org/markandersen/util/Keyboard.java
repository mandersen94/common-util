package org.markandersen.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * User: e63582 Date: Jul 12, 2004 Time: 8:15:45 PM
 */
public class Keyboard {

	private static boolean iseos = false;

	private static BufferedReader input;

	/** creates the reader to read from the System.in stream. */
	static {
		input = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * reads a line from the Std in (keyboard) and returns it.
	 * 
	 * @return the line of text typed
	 */
	public static String readStringLine() {
		String result = null;

		if (iseos) {
			return null;
		}

		System.out.flush();
		try {
			result = input.readLine();
		} catch (IOException ex) {
			System.exit(-1);
		}

		if (result == null) {
			iseos = true;
			return null;
		} else {
			return result;
		}
	}
}
