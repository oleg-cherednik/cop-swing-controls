package com.deutschebank.test.files.tasks;

import java.io.File;

import com.deutschebank.test.Statistics;
import com.deutschebank.test.concurence.ThreadPool;
import com.deutschebank.test.files.ResultStore;

/**
 * Task to read directory and search file by given regex pattern. Each directory is scanned in separate thread. If file
 * is matched with given regex, then new task is executed in new thread to check whether it contains string, that
 * matches with {@link FindFileTask#textSearchRegex}, or not.
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class FindFileTask extends FileTask {
	/**
	 * Root path. If it's field with valid name or directory then this task will be run in parallel thread.
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
	public FindFileTask(String rootPath, String fileNameRegex, String textSearchRegex, ThreadPool pool, ResultStore out) {
		super(getId(rootPath, fileNameRegex), out);

		assert rootPath != null && !rootPath.isEmpty();

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
			pool.execute(new FindTextInFileTask(file, textSearchRegex, out));
	}

	/** Add given file to result store */
	private void addFileToResult(File file) {
		out.addFile(null, file.getAbsolutePath());
	}

	// ========== AbstractFileTask ==========

	protected void runImpl() {
		File[] list = new File(rootPath).listFiles();

		if (list == null || list.length == 0)
			return;

		int folders = 0;
		int files = 0;

		for (File file : list) {
			if (file.isDirectory()) {
				// run directory working in other thread
				folders++;

				String rootPath = file.getAbsolutePath();
				pool.execute(new FindFileTask(rootPath, fileNameRegex, textSearchRegex, pool, out));
			} else {
				// check file name and if it's OK, run search pattern in file in other thread
				files++;
				checkFile(file);
			}
		}

		Statistics.getInstance().addScannedFiles(files);
		Statistics.getInstance().addScannedFolders(folders);
	}

	// ========== static ==========

	private static final String getId(String root, String filePattern) {
		return FindFileTask.class.getSimpleName() + ':' + root + ':' + filePattern;
	}
}
