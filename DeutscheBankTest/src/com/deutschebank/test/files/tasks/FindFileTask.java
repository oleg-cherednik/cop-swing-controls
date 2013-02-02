package com.deutschebank.test.files.tasks;

import java.io.File;

import com.deutschebank.test.concurence.ThreadPool;
import com.deutschebank.test.files.Result;

/**
 * Task to read directory and search file by given regex pattern. Each directory is scanned in separate thread. If file
 * is matched with given regex, then new task is executed in new thread to check whether it contains string, that
 * matches with {@link FindFileTask#textSearchRegex}, or not.
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class FindFileTask extends AbstractFileTask {
	/**
	 * Root path. If it's fild with valid name or directory then this task will be run in parallel thread.
	 */
	private final String rootPath;
	/** File name regex */
	private final String fileNameRegex;
	/** Test search in file regex */
	private final String textSearchRegex;
	/** Thread pool */
	private final ThreadPool pool;

	/**
	 * @param rootPath root path
	 * @param fileNameRegex file name regex
	 * @param textSearchRegex text search regex
	 * @param resultBuilder result store
	 * @param pool thread pool to add new tasks
	 */
	public FindFileTask(String rootPath, String fileNameRegex, String textSearchRegex, Result.Builder resultBuilder,
			ThreadPool pool) {
		super(resultBuilder);

		this.rootPath = rootPath;
		this.fileNameRegex = (fileNameRegex == null || fileNameRegex.isEmpty()) ? null : fileNameRegex;
		this.textSearchRegex = (textSearchRegex == null || textSearchRegex.isEmpty()) ? null : textSearchRegex;
		this.pool = pool;
	}

	/**
	 * Check file for {@link #fileNameRegex} pattern
	 * 
	 * @param file
	 */
	private void checkFile(File file) {
		if (!file.getName().matches(fileNameRegex))
			return;

		// if textSearchRegex is not set, then add file to result store
		if (textSearchRegex == null)
			addFileToResult(file);
		else
			pool.execute(new FindTextInFileTask(file, textSearchRegex, resultBuilder));
	}

	/** Add given file to result store */
	private void addFileToResult(File file) {
		System.out.println(file.getAbsolutePath());
		resultBuilder.addFile(file.getAbsolutePath());
	}

	// ========== AbstractFileTask ==========

	protected void runImpl() {
		File root = new File(rootPath);

		if (!root.exists()) {
			System.err.println("Root '" + rootPath + "' is not exist");
			return;
		}

		File[] list = root.listFiles();

		if (list == null || list.length == 0)
			return;

		int folders = 0;
		int files = 0;

		for (File file : list) {
			if (file.isDirectory()) {
				// run directory working in other thread
				folders++;

				String rootPath = file.getAbsolutePath();
				pool.execute(new FindFileTask(rootPath, fileNameRegex, textSearchRegex, resultBuilder, pool));
			} else {
				// check file name and if it's OK, run search pattern in file in other thread
				files++;
				checkFile(file);
			}
		}

		// store statistics
		resultBuilder.addTotalFiles(files);
		resultBuilder.addTotalFolders(folders);
	}
}
