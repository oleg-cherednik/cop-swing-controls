package com.deutschebank.test.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.deutschebank.test.xml.InputData;
import com.deutschebank.test.xml.OutputData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Utilities for file input/output.
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class IOUtils {
	private IOUtils() {}

	/**
	 * Read give file as input data.
	 * 
	 * @param file xml file
	 * @return {@link InputData}
	 * @throws FileNotFoundException
	 */
	public static InputData readInXML(File file) throws FileNotFoundException {
		FileInputStream in = null;

		try {
			XStream xstream = createParser();
			InputData.process(xstream);
			return (InputData)xstream.fromXML(in = new FileInputStream(file));
		} finally {
			close(in);
		}
	}

	/**
	 * Save give output data to the give file.
	 * 
	 * @param data {@link OutputData} output data
	 * @param file output file to write
	 * @throws FileNotFoundException
	 */
	public static void writeOutXML(OutputData data, File file) throws FileNotFoundException {
		assert data != null;
		assert file != null;

		FileOutputStream out = null;

		try {
			XStream xstream = createParser();
			OutputData.process(xstream);
			xstream.toXML(data, out = new FileOutputStream(file));
		} finally {
			close(out);
		}
	}

	private static XStream createParser() {
		return new XStream(new DomDriver());
	}

	public static void close(Closeable obj) {
		if (obj != null) {
			try {
				obj.close();
			} catch (Exception ignored) {}
		}
	}
}
