package com.deutschebank.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.deutschebank.test.files.ScanManager;
import com.deutschebank.test.files.Result;
import com.deutschebank.test.utils.IOUtils;
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
			Result res = new Main(args).proceed();

			System.out.println(LINE);

			System.out.println("Time: " + Statistics.getInstance().getTotalWorkTime() + " sec.");
			System.out.println("\nResults (textPattern - files amount):");

			for (Map.Entry<String, Set<String>> entry : res.getData().entrySet()) {
				String textPattern = entry.getKey();
				Set<String> files = entry.getValue();

				System.out.print(Statistics.isEmpty(textPattern) ? "<no pattern>" : textPattern);
				System.out.println(" - " + files.size());
			}

			System.out.println("\nTotal files searched: " + Statistics.getInstance().getScannedFiles());
			System.out.println("Total folders searched: " + Statistics.getInstance().getScannedFolders());
			System.out.println("Average task delay: " + Statistics.getInstance().getAverageDelay() + " sec.");
			System.out.println("Average task work: " + Statistics.getInstance().getAverageWork() + " sec.");
			
			ArgumentUtils.writeOutputData(res.getXml(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Main(String... args) throws Exception {
		inputData = ArgumentUtils.readInputData(args);

		// readArguments(args);
		// if (args.length < 4)
		// askForMissingArguments();
		//
		// printArguments();
	}

	public Result proceed() {
		Statistics.getInstance().started();

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
			Statistics.getInstance().accomplished();
			scanManager.dispose();
			scanManager = null;
		}

		return Result.createBuilder().createResult();
	}

	private void readArguments(String... args) throws FileNotFoundException {
		InputData data = IOUtils.readInXML(new File(args[0]));

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
