package com.deutschebank.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.deutschebank.test.files.Result;
import com.deutschebank.test.utils.IOUtils;
import com.deutschebank.test.xml.InputData;

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
			Main mainModule = new Main(args);
			Result res = mainModule.proceed();

			System.out.println(LINE);

			System.out.println(IOUtils.toXML(mainModule.inputData));
			System.out.println("\nTime: " + Statistics.getInstance().getTotalWorkTime() + " sec.");
			System.out.println("Total files searched: " + Statistics.getInstance().getScannedFiles());
			System.out.println("Total folders searched: " + Statistics.getInstance().getScannedFolders());
			System.out.println("Average task delay: " + Statistics.getInstance().getAverageDelay() + " sec.");
			System.out.println("Average task work: " + Statistics.getInstance().getAverageWork() + " sec.");

			// ArgumentUtils.writeOutputData(res.getXml(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Main(String... args) throws Exception {
		ArgumentManager.getInstance().setArgumens(args);
		inputData = ArgumentManager.getInstance().readInputData();
	}

	public Result proceed() {
		Statistics.getInstance().started();

		int nThreads = inputData.getThreads();
		boolean outToConsole = inputData.isOutToConsole();
		boolean upToDateOutput = inputData.isUpToDateOutput();
		ScanManager scanManager = new ScanManager(nThreads, outToConsole, upToDateOutput);

		try {
			return scanManager.proceed(inputData.getTasks());
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
