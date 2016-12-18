package org.markandersen.portal.logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.markandersen.text.MessageFormatter;

public class LogParser {

	private String filePattern = "xspeip11";
	private String directory;
	private List<LoginEntry> database = new ArrayList<LoginEntry>();

	private Map<String, LoginEntry> map = new HashMap<String, LoginEntry>();

	public LogParser(String directory) {
		this.directory = directory;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Starting log parsing.");
			String directory = "/C:/stats-logs/";
			LogParser parser = new LogParser(directory);
			parser.parse();

			parser.buildReportingDatastructure();
			parser.reportNumberOfUniqueUsers();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void buildReportingDatastructure() {
		Collections.sort(database);
		int count = 1;
		String previousUserid = "lkwjerwoieu";
		int uniqueUsers = 0;
		
		for (LoginEntry entry : database) {
			String username = entry.getUserid();
			if(!previousUserid.equalsIgnoreCase(username)){
				uniqueUsers++;
				previousUserid = username;
			}
			
			System.out.println(count + ": " + username);
			map.put(username, entry);
			count++;
		}
		System.out.println("# of unique users = " + uniqueUsers);
	}

	/**
	 * 
	 */
	private void reportNumberOfUniqueUsers() {
		int numberOfUsers = map.keySet().size();
		System.out.println(MessageFormatter.format("# of unique users = {}",
				numberOfUsers));

	}

	/**
	 * @throws IOException
	 * 
	 */
	private void parse() throws IOException {

		List<File> filesUnderDirectory = getFilesUnderDirectory();
		System.out.println("files in directory are = " + filesUnderDirectory);
		// String filename = "stats.log";

		for (File file : filesUnderDirectory) {
			System.out.println("Parsing file " + file.getName());
			parseFile(file);
			System.out.println("Finished parsing file " + file.getName());
		}
	}

	private List<File> getFilesUnderDirectory() {
		List<File> files = new ArrayList<File>();
		File dir = new File(directory);
		if (!dir.isDirectory()) {
			System.out.println(MessageFormatter.format(
					"{} is not a directory.", directory));
			return files;
		}

		addFiles(files, dir);

		return files;
	}

	/**
	 * 
	 * @param goodFiles
	 * @param dir
	 */
	private void addFiles(List<File> goodFiles, File dir) {
		File[] listFiles = dir.listFiles();
		for (File oneFile : listFiles) {
			if (oneFile.isDirectory()) {
				addFiles(goodFiles, oneFile);
			} else if (matchFileName(oneFile.getAbsolutePath())) {
				goodFiles.add(oneFile);
			} else {
				// do nothing
			}
		}

	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private boolean matchFileName(String name) {
		if (name.indexOf(filePattern) > -1) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void parseFile(File file) throws IOException {
		System.out.println("Opening file " + file + " to read in entries.");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		String line = br.readLine();
		int counter = 1;
		while (line != null) {
			try {
				// System.out.println("reading entry " + counter);
				addEntry(line);
				counter++;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			line = br.readLine();
		}
		System.out.println(MessageFormatter.format(
				"finished reading in {} entries.", counter));
		long freeMemory = Runtime.getRuntime().freeMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		System.out
				.println(MessageFormatter.format("free memory = {} MB, {} KB,",
						freeMemory / (double) (1024 * 1024), freeMemory
								/ (double) (1024)));
		System.out
				.println(MessageFormatter.format(
						"total memory = {} MB, {} KB,", totalMemory
								/ (double) (1024 * 1024), totalMemory
								/ (double) (1024)));

		br.close();

	}

	/**
	 * 
	 * @param line
	 * @throws ParseException
	 */
	private void addEntry(String line) throws ParseException {
		LoginEntry entry = new LoginEntry();
		entry.parse(line);
		database.add(entry);

	}

}
