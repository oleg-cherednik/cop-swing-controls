package com.deutschebank.test;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.deutschebank.test.xml.InputData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public final class FormReader {
	private FormReader() {}

	public static InputData readDataXML(File file) throws FileNotFoundException {
		FileInputStream in = null;
		XStream xstream = FileType.IN.process(createParser());

		try {
			return (InputData)xstream.fromXML(in = new FileInputStream(file));
		} finally {
			close(in);
		}
	}

//	protected static ResultForm readResultFormXML(File file) throws FileNotFoundException {
//		FileInputStream fis = null;
//		XStream xstream = FileType.RESULT.process(createParser());
//
//		try {
//			fis = new FileInputStream(file);
//			return (ResultForm)xstream.fromXML(fis);
//		} finally {
//			close(fis);
//		}
//	}

	private static XStream createParser() {
		return new XStream(new DomDriver());
	}

	public static void close(Closeable obj) {
		if (obj == null)
			return;

		try {
			obj.close();
		} catch (Exception ignored) {
		}
	}

//	public static Map<Long, File> getQuestionnaires(File in) {
//		Long uid;
//		Map<Long, File> map = new HashMap<Long, File>();
//
//		for (File file : getFiles(in))
//			if ((uid = FileType.QUESTIONNAIRE.getUid(file.getName())) != null)
//				map.put(uid, file);
//
//		return map.isEmpty() ? Collections.<Long, File>emptyMap() : Collections.<Long, File>unmodifiableMap(map);
//	}
//
//	private static void addFile(File file, Long uid, Map<Long, Set<File>> map) {
//		Set<File> res = map.get(uid);
//
//		if (res == null)
//			map.put(uid, res = new HashSet<File>());
//
//		res.add(file);
//	}
//
//	public static Map<Long, Set<File>> getResults(File in) {
//		Map<Long, Set<File>> map = new TreeMap<Long, Set<File>>();
//		Long uid;
//
//		for (File file : getFiles(in))
//			if ((uid = FileType.RESULT.getUid(file.getName())) != null)
//				addFile(file, uid, map);
//
//		return map.isEmpty() ? Collections.<Long, Set<File>>emptyMap() : Collections.unmodifiableMap(map);
//	}
//
//	private static Set<File> getFiles(File file) {
//		Set<File> files = new HashSet<File>();
//
//		if (file.isDirectory()) {
//			for (File inFile : file.listFiles())
//				if (inFile.isFile())
//					files.add(inFile);
//		} else
//			files.add(file);
//
//		return Collections.unmodifiableSet(files);
//	}
//
//	public static File getDirectory(File file) {
//		return file.isDirectory() ? file : file.getParentFile();
//	}
//
//	public static URI getRelativePath(File path, File base) {
//		return base.toURI().relativize(path.toURI());
//	}
}
