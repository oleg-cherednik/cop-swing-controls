package com.deutschebank.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.deutschebank.test.files.ScanManager;
import com.deutschebank.test.files.Result;
import com.deutschebank.test.xml.InputData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public class Main {
	private static final String LINE = "-----------------------";
	private static final int INDEX_ROOT_PATH = 0;
	private static final int INDEX_FILE_NAME_MASK = 1;
	private static final int INDEX_PATTERN = 2;
	private static final int INDEX_MAX_THREADS_AMOUNT = 3;

	private Argument[] arguments = { new Argument("root path", "c:\\"), new Argument("file mask", "*.txt", true),
			new Argument("stringPatternInFile", null), new Argument("maxThreadsAmount", "10") };

	private final InputData inputData;

	/**
	 * Command line arguments:
	 * <ul>
	 * <li><b>1.</b> root path (<i>default: "c:\"</i>)
	 * <li><b>2.</b> file mask to search (<i>default: "*.txt"</i>)
	 * <li><b>3.</b> text search in file (<i>default: <tt>null</tt></i>)
	 * <li><b>4.</b> thread amount in pool (<i>default: 10</i>)
	 * </ul>
	 * 
	 * Arguments are separated with <tt>space</tt>. Each arguments can be optionally wrapped with <tt>'</tt> symbol.<br>
	 * If arguments number is less then 4, then they will be asked through console.
	 */
	public static void main(String... args) {
		try {
			Main module = new Main(args);
			long timeIn = System.currentTimeMillis();
			Result res = module.proceed();
			long timeOut = System.currentTimeMillis();

			System.out.println(LINE);

			System.out.println("Time: " + ((timeOut - timeIn) / 1000) + " sec.");
			System.out.println("Total files found: " + res.getResultFiles().size());
			System.out.println("Total files searched: " + res.getTotalFiles());
			System.out.println("Total folders searched: " + res.getTotalFolders());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Main(String... args) throws Exception {
		inputData = ArgumentUtils.getInputData(args);

		// readArguments(args);
		// if (args.length < 4)
		// askForMissingArguments();
		//
		// printArguments();
	}

	public Result proceed() {
		int nThreads = inputData.getThreads();
		boolean outToConsole = inputData.isOutToConsole();
		ScanManager scanManager = new ScanManager(nThreads, outToConsole);
		// ScanManager scanManager = new ScanManager(Integer.parseInt(arguments[INDEX_MAX_THREADS_AMOUNT].getValue()));

		try {
			// String rootPath = arguments[INDEX_ROOT_PATH].getValue();
			// String fileNamePattern = arguments[INDEX_FILE_NAME_MASK].getValue();
			// String textSearchPattern = arguments[INDEX_PATTERN].getValue();

			return scanManager.proceed(inputData.getTasks());
			// return scanManager.findFiles(rootPath, fileNamePattern, textSearchPattern);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scanManager.dispose();
			scanManager = null;
		}

		return Result.createBuilder().createResult();
	}

	private void readArguments(String... args) throws FileNotFoundException {
		InputData data = FormReader.readDataXML(new File(args[0]));

		if (args != null && args.length != 0)
			for (int i = 0, size = args.length; i < size; i++)
				arguments[i].setValue(args[i].trim());
	}

	private void askForMissingArguments() {
		Scanner in = new Scanner(System.in);

		for (int i = 0, size = arguments.length; i < size; i++)
			arguments[i].askArgument(in);

		in.close();
	}

	private void printArguments() {
		System.out.println("--- Using Arguments ---");

		for (int i = 0, size = arguments.length; i < size; i++)
			System.out.println(arguments[i]);

		System.out.println(LINE);
	}
}
