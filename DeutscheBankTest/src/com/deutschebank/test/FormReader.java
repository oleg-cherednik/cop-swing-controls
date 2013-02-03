package com.deutschebank.test;

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
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class FormReader {
	private FormReader() {}

	public static InputData readInXML(File file) throws FileNotFoundException {
		FileInputStream in = null;

		try {
			XStream xstream = FileType.IN.process(createParser());
			return (InputData)xstream.fromXML(in = new FileInputStream(file));
		} finally {
			close(in);
		}
	}

	public static void writeOutXML(OutputData data, File file) throws FileNotFoundException {
		FileOutputStream out = null;

		try {
			XStream xstream = FileType.OUT.process(createParser());
			xstream.toXML(data, out = new FileOutputStream(file));
		} finally {
			close(out);
		}
	}

	private static XStream createParser() {
		return new XStream(new DomDriver());
	}

	public static void close(Closeable obj) {
		if (obj == null)
			return;

		try {
			obj.close();
		} catch (Exception ignored) {}
	}
}
