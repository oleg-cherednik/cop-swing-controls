package com.deutschebank.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.deutschebank.test.utils.IOUtils;
import com.deutschebank.test.xml.InputData;
import com.deutschebank.test.xml.OutputData;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
final class ArgumentUtils {
	private ArgumentUtils() {}

	public static InputData readInputData(String... args) throws FileNotFoundException {
		if (args != null && args.length >= 1) {
			File file = new File(args[0]);

			System.out.println("Read input file: " + file);

			return IOUtils.readInXML(file);
		}

		System.out.println("Input file not found");

		return askForInputData();
	}

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
