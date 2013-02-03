package com.deutschebank.test.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.deutschebank.test.xml.InputData;
import com.deutschebank.test.xml.OutputData;

/**
 * Utilities to work with command line and input argument array.
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class ArgumentUtils {
	private ArgumentUtils() {}

	/**
	 * Read give input arguments and read input file or ask user via console.
	 * 
	 * @param args input program arguments
	 * @return {@link InputData} data
	 * @throws FileNotFoundException
	 */
	public static InputData readInputData(String... args) throws FileNotFoundException {
		if (args != null && args.length >= 1) {
			File file = new File(args[0]);

			System.out.println("Read input file: " + file);

			return IOUtils.readInXML(file);
		}

		System.out.println("Input file not found");

		return askForInputData();
	}

	/**
	 * Read given input arguments and write give data to output file.
	 * 
	 * @param data output data
	 * @param args input program arguments
	 * @throws FileNotFoundException
	 */
	public static void writeOutputData(OutputData data, String... args) throws FileNotFoundException {
		if (args != null && args.length >= 2) {
			File file = new File(args[1]);

			System.out.println("Write output file: " + file);
			IOUtils.writeOutXML(data, file);
		} else
			System.err.println("Output file not set");
	}

	private static InputData askForInputData() {
		Scanner in = null;

		try {
			in = new Scanner(System.in);

			// for (int i = 0, size = arguments.length; i < size; i++)
			// arguments[i].askArgument(in);

			return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			in.close();
		}

		return null;
	}
}
