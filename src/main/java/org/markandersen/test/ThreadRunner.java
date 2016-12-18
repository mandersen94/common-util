package org.markandersen.test;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.markandersen.reflect.ClassFinder;
import org.markandersen.system.SystemUtils;
import org.markandersen.util.Keyboard;


public class ThreadRunner {

	private static int threadCount = 1;

	private static int iterations = 1;

	/** time to wait in between interations. */
	private int waitTime = 0;

	/** amount of variance in the wait time. */
	private int variance = 0;

	private static boolean verbose = false;

	private static boolean interactive = true;

	private static String testClassname;

	private static boolean useSeparateClassloaders = false;

	protected static boolean staticClassName = false;

	protected static String[] includeList = null;

	/**
	 * 
	 * @param args
	 */
	public ThreadRunner(String args[]) {
		processArgs(args);
	}

	/**
	 * 
	 */
	private static void usage() {
		System.out
				.println("Tester [-threads n] [-iterations m] [-oneRun] [-classloader] [-waitTime millis] [-variance millis] -class testclassname");
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		boolean loop = true;
		for (int i = 0; i < args.length; i++) {
			// we need to check this up front to make sure we start the client
			// correctly.
			if ("-oneRun".equalsIgnoreCase(args[i])) {
				loop = false;
			} else if ("-classloader".equalsIgnoreCase(args[i])) {
				useSeparateClassloaders = true;
			} else if ("-include".equalsIgnoreCase(args[i])) {
				includeList = new String[] { args[++i] };
				System.out.println("using includeList = " + includeList[0]);
			} else if ("-class".equals(args[i])) {
				staticClassName = true;
			}
		}

		if (loop) {
			ThreadRunner theTester = new ThreadRunner(args);
			theTester.runTests();

			while (true) {
				System.out
						.println("Press Enter to run test.  Type \"quit\" to quit.");
				String answer = Keyboard.readStringLine();

				if ("quit".equalsIgnoreCase(answer)) {
					System.exit(0);
				}

				System.out.print("Enter threads:");
				String threadsString = Keyboard.readStringLine();

				System.out.print("Enter iterations:");
				String interationsString = Keyboard.readStringLine();

				System.out.print("Enter wait:");
				String waitString = Keyboard.readStringLine();

				System.out.print("Enter variance:");
				String variance = Keyboard.readStringLine();

				String[] newArgs = new String[] { "-threads", threadsString,
						"-iterations", interationsString, "-waitTime",
						waitString, "-variance", variance };
				ThreadRunner newTester = new ThreadRunner(newArgs);
				newTester.runTests();

			}
		} else {
			// do it once
			ThreadRunner theTester = new ThreadRunner(args);
			theTester.runTests();
		}
	}

	/**
	 * 
	 * 
	 */
	private void selectClassname() {

		System.out.println("No class name specified.");
		System.out
				.println("Searching for classes that implement ThreadRunnable.");
		while (true) {
			List<String> classes = displayPossibleClassnames();
			System.out.println("");
			System.out.print("please select class by number: ");
			try {
				String input = Keyboard.readStringLine();
				int index = Integer.parseInt(input) - 1;
				if ((index < 0) || (index >= classes.size())) {
					System.out.println("Bad input.  try again.");
					System.out.println("");
					System.out.println("");
					continue;
				}
				String selectedClass = (String) classes.get(index);
				testClassname = selectedClass;
				System.out.println("Using class " + selectedClass);
				break;
			} catch (Exception e) {
				System.out.println("Bad input.  try again.");
				System.out.println("");
				System.out.println("");
			}
		}
	}

	/**
	 * @return
	 */
	private List<String> displayPossibleClassnames() {
		List<String> classes = fetchClassNames();

		System.out.println("Found the following classes.");
		int counter = 1;
		Iterator<String> iterator = classes.iterator();
		while (iterator.hasNext()) {
			String className = iterator.next();
			System.out.println(counter + ". " + className);
			counter++;
		}
		return classes;
	}

	/**
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private List<String> fetchClassNames() {
		try {
			long startTime = System.currentTimeMillis();
			String[] paths = SystemUtils.getClasspathPaths();

			List<String> results = ClassFinder.findClassesThatExtend(paths,
					new Class[] { ThreadRunnable.class });
			System.out.println("Took "
					+ (System.currentTimeMillis() - startTime) + " ms to find "
					+ results.size() + " classes.");
			return results;
		} catch (Exception e) {
			System.err.println("Problems searching for classes.");
			return new ArrayList<String>();
		}
	}

	/**
	 * 
	 */
	public void runTests() {

		NumberFormat nf = NumberFormat.getInstance();
		TesterThread[] testers = new TesterThread[threadCount];

		System.out.println("making threads");
		for (int i = 0; i < threadCount; i++) {
			testers[i] = new TesterThread(iterations);
		}

		System.out.println("starting test");
		long startT = System.currentTimeMillis();
		for (int i = 0; i < threadCount; i++) {
			testers[i].start();
		}

		for (int i = 0; i < threadCount; i++) {
			try {
				testers[i].join();
			} catch (Exception e) {
			}
		}
		long stopT = System.currentTimeMillis();

		long omax = 0;
		long omin = 1000000;
		double ttps = 0.0;
		int ofails = 0;
		long oavg = 0;

		nf.setMaximumFractionDigits(2);

		System.out.println("");
		System.out.println("------------------------------------------------");
		int offset = 10;

		System.out.println(rightAlign("thread", 7) + " "
				+ rightAlign("time", 7) + " " + rightAlign("tps", 7) + " "
				+ rightAlign("max", 7) + " " + rightAlign("min", 7) + " "
				+ rightAlign("avg", 7));
		System.out.println("------------------------------------------------");

		for (int i = 0; i < threadCount; i++) {
			ofails += testers[i].getFailures();
			long timet = testers[i].getTime();
			double tps = 1000 * (iterations / (double) (timet > 0 ? timet
					: -1000 * iterations));
			long tavg = timet / iterations;
			oavg += tavg;
			ttps += tps;

			System.out.println(rightAlign("" + (i + 1), 7)
					+ rightAlign(String.valueOf(timet), 8)
					+ rightAlign(String.valueOf(nf.format(tps)), 8)
					+ rightAlign(String.valueOf(testers[i].getMax()), 8)
					+ rightAlign(String.valueOf(testers[i].getMin()), 8)
					+ rightAlign(String.valueOf(tavg), 8));
			if (testers[i].getMax() > omax) {
				omax = testers[i].getMax();
			}
			if (testers[i].getMin() < omin) {
				omin = testers[i].getMin();
			}

		}

		long ttime = stopT - startT;
		double tps = 1000 * (threadCount * iterations / (double) ttime);
		double atps = ttps / threadCount;
		long aavg = ttime / (threadCount * iterations);

		System.out.println("------------------------------------------------");
		System.out.println("");
		System.out
				.println(rightAlign(String.valueOf(ttime), offset)
						+ " total time (ms)\n"
						+ rightAlign(
								String.valueOf((threadCount * iterations)),
								offset)
						+ " requests\n"
						+ rightAlign(String.valueOf(nf.format(tps)), offset)
						+ " tps\n"
						+ rightAlign(
								String.valueOf((int) (oavg / threadCount)),
								offset) + " avg request time (ms)\n"
						+ rightAlign(String.valueOf(ofails), offset)
						+ " failures\n");

		// System.out.println("Thread avg tps: "+nf.format(atps));
		// System.out.println("Totals: "+ttime+" ms, "+tps+" tps "+
		// "max "+omax+", min "+omin+", avg "+
		// aavg);

	}

	/**
	 * 
	 * @param args
	 */
	public void processArgs(String[] args) {

		if ((staticClassName == false) && (testClassname == null)) {
			usage();
			selectClassname();
		}

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				try {
					if (args[i].equalsIgnoreCase("-threads")
							&& i + 1 < args.length) {
						threadCount = Integer.parseInt(args[++i]);
					} else if (args[i].equalsIgnoreCase("-iterations")
							&& i + 1 < args.length) {
						iterations = Integer.parseInt(args[++i]);
					} else if (args[i].equalsIgnoreCase("-waitTime")
							&& i + 1 < args.length) {
						waitTime = Integer.parseInt(args[++i]);
					} else if (args[i].equalsIgnoreCase("-variance")
							&& i + 1 < args.length) {
						variance = Integer.parseInt(args[++i]);
					} else if (args[i].equalsIgnoreCase("-class")
							&& i + 1 < args.length) {
						testClassname = args[++i];
						System.out.println("Using class: " + testClassname);
					} else if (args[i].equals("-classloader")) {
						// nop
					} else if (args[i].equals("-interactive")) {
						// nop
					} else if (args[i].equals("-v")) {
						verbose = true;
					} else if (args[i].equals("-include")) {
						i++;
						// nop
					} else {
						System.out.println("Unknown option: " + args[i]);
						usage();
						System.exit(-1);
					}
				} catch (Exception e) {
					System.out.println("can't parse number of arg: " + e);
					System.exit(-1);
				}
			}
		}
	}

	/**
	 * 
	 */
	class TesterThread extends Thread {
		private ThreadRunnable t;

		private int trials;

		private long time = -1;

		private int failures = 0;

		private long max = 0;

		private long min = 1000000;

		/**
		 * 
		 * @param trials
		 */
		public TesterThread(int trials) {
			this.trials = trials;
			try {
				ClassLoader classLoader;
				if (useSeparateClassloaders == true) {
					URL[] urls = SystemUtils.getURLs();
					classLoader = new DelegatingClassLoader(urls, this
							.getClass().getClassLoader(), false,
							new String[] { "com.andersen.test.ThreadRunnable" });
				} else {
					classLoader = this.getClass().getClassLoader();
				}

				Class<?> c = Class.forName(testClassname, false, classLoader);
				System.out.flush();
				t = (ThreadRunnable) c.newInstance();
				t.setup();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		public void run() {
			System.out.println("waitTime = " + waitTime);
			System.out.println("variance = " + variance);

			if (trials > 0) {
				long startT = System.currentTimeMillis();

				for (int i = 0; i < trials; i++) {
					long i1 = System.currentTimeMillis();

					if (!t.runOne()) {
						failures++;
					}

					long itime = System.currentTimeMillis() - i1;
					if (itime < min) {
						min = itime;
					}
					if (itime > max) {
						max = itime;
					}

					try {
						if (waitTime != 0) {
							long newWaitTime = calculateWait(waitTime, variance);
							System.out.println("waiting = " + newWaitTime);
							Thread.sleep(newWaitTime);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				long stopT = System.currentTimeMillis();
				time = stopT - startT;
			} else {
				while (true) {
					t.runOne();
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}

				}
			}

		}

		/**
		 * 
		 * @param waitTime
		 * @param variance
		 * @return
		 */
		protected long calculateWait(int waitTime, int variance) {
			double stuff = Math.random();
			// subtract waitTime / 2 to shift it +/-
			long var = Math.round((stuff * variance) - (variance / 2));
			return (waitTime + var);
		}

		public long getTime() {
			return time;
		}

		public long getMin() {
			return min;
		}

		public long getMax() {
			return max;
		}

		public int getFailures() {
			return failures;
		}

		public void finish() {
			t.teardown();
		}

	}

	String rightAlign(String s, int cols) {
		String padded = "                       " + s;
		return padded.substring(padded.length() - cols);
	}

}
