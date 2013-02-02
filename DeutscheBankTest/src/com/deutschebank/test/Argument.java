package com.deutschebank.test;

import java.util.Scanner;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class Argument {
	private static final String DEFAULT_MARKER = "null";
	private static final char LIMIT_MARKER = '\'';
	/**
	 * Argument name
	 */
	private final String name;
	/**
	 * Argument value
	 */
	private String value;
	/**
	 * It value pattern or not. If it's <tt>true</tt> then value will be converted to the Java Pattern
	 */
	private boolean pattern;

	public Argument(String name) {
		this.name = name.trim();
	}

	public Argument(String name, String value) {
		this(name);
		this.value = value;
	}

	public Argument(String name, String value, boolean pattern) {
		this(name, value);
		this.pattern = pattern;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return pattern ? convertToJavaPattern(value) : value;
	}

	public void setValue(String value) {
		if (value == null || value.isEmpty())
			this.value = "";
		else if (value.charAt(0) == LIMIT_MARKER && value.charAt(value.length() - 1) == LIMIT_MARKER)
			this.value = value.substring(1, value.length() - 1);
		else
			this.value = value;
	}

	public void askArgument(Scanner in) {
		setValue(askArgument(name, value, in));
	}

	/**
	 * Convert Windows pattern to Java pattern
	 * 
	 * @param winMask windows pattern
	 * @return java pattern
	 */
	private static String convertToJavaPattern(String winMask) {
		if (winMask == null || winMask.isEmpty())
			return "";

		return winMask.replaceAll("\\.", "\\\\.").replaceAll("\\?", ".{1}").replaceAll("\\*", ".*");
	}

	/**
	 * Giving {@link Scanner} to ask user for arguments' value
	 * 
	 * @param name argument's name
	 * @param defaultValue argument's default value
	 * @param in {@link Scanner}
	 * @return new argument's value
	 */
	private static String askArgument(String name, String defaultValue, Scanner in) {
		System.out.println("=> Set value: '" + name + "'");
		System.out.println("name: " + name);

		if (defaultValue != null && defaultValue.length() != 0)
			System.out.println("default value (leave blank): " + defaultValue);

		System.out.print("value (type '" + DEFAULT_MARKER + "' to ignore): ");

		String res = in.nextLine().trim();

		if (defaultValue != null && defaultValue.length() != 0 && res.isEmpty())
			return defaultValue;

		if (DEFAULT_MARKER.equals(res))
			return "";

		return res;
	}

	/*
	 * Object
	 */

	public String toString() {
		return name + ": " + value;
	}
}
