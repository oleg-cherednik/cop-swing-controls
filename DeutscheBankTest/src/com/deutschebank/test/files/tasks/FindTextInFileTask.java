package com.deutschebank.test.files.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import com.deutschebank.test.files.ResultStore;

/**
 * This runnable task opens given file and search text in it according given regex pattern.
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
final class FindTextInFileTask extends FileTask {
	/** File reference */
	private final File file;
	/** Regex pattern to search */
	private final String regex;

	/**
	 * @param file file reference
	 * @param regex search string pattern
	 * @param resultBuilder result store
	 * @throws IllegalArgumentException if {@link #file} is not a file
	 */
	public FindTextInFileTask(File file, String regex, ResultStore out) {
		super(getId(file, regex), out);

		assert file != null && file.isFile();
		assert regex != null && !regex.trim().isEmpty();
		assert out != null;

		this.file = file;
		this.regex = regex;
	}

	// ========== AbstractFileTask ==========

	protected void runImpl() {
		LineNumberReader in = null;
		String str = null;

		try {
			// read file line by line and search regex in it
			in = new LineNumberReader(new BufferedReader(new FileReader(file)));

			while (true) {
				str = in.readLine();

				if (str == null)
					break;
				if (str.indexOf(regex) == -1)
					continue;

				// file is good. print path to console and add it to result store
				out.addFile(regex, file.getAbsolutePath());

				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {}
			}
		}
	}

	// ========== static ==========

	private static final String getId(File file, String textPattern) {
		return FindTextInFileTask.class.getSimpleName() + ':' + file.getAbsolutePath() + ':' + textPattern;
	}
}
