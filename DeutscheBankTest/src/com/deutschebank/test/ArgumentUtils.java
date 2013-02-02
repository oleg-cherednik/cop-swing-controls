package com.deutschebank.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.deutschebank.test.xml.InputData;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
final class ArgumentUtils {
	private ArgumentUtils() {}

	public static InputData getInputData(String... args) throws FileNotFoundException {
		if (args != null && args.length == 1) {
			File file = new File(args[0]);

			System.out.println("Read input file: " + file);

			return FormReader.readDataXML(file);
		}

		System.out.println("Input file not found");

		return askForInputData();
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
