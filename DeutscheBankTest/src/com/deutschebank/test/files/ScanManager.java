package com.deutschebank.test.files;

import java.io.File;

import com.deutschebank.test.concurence.ThreadPool;
import com.deutschebank.test.files.tasks.FindFileTask;

/**
 * File manager. It contains methods to search file with giving name pattern ({@link #fileNameRegex}) or/and text in
 * file pattern ({@link #textSearchRegex}).
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class ScanManager {
	/** Thread pool */
	private final ThreadPool pool;
	/** Result store */
	private final Result.Builder builder = Result.createBuilder();

	/** @param nThreads thread amount (more than zero) */
	public ScanManager(int nThreads) {
		pool = ThreadPool.newFixedThreadPool(nThreads);
	}

	/** Dispose this object. Stop used thread pool. */
	public void dispose() {
		pool.stop();
	}

	/**
	 * Finds files starting from giving <tt>rootPath</tt> with giving file name pattern <tt>fileNamePattern</tt> and/or
	 * text search string in file pattern (<tt>textSearchPattern</tt>).<br>
	 * Each directory task and search text in file task is run on separate thread.
	 * 
	 * @param rootPath root path (<tt>!null</tt>)
	 * @param fileNamePattern file name pattern (<tt>!null</tt>)
	 * @param textSearchPattern pattern for text search in file
	 * @throws Exception in case of errors
	 * @return {@link Result} with search result
	 */
	public Result findFiles(String rootPath, String fileNamePattern, String textSearchPattern) throws Exception {
		if (rootPath == null || fileNamePattern == null)
			throw new Exception("rootPath == null || mask == null");

		File root = new File(rootPath);

		if (!root.exists())
			throw new Exception("Root '" + rootPath + "' is not exist");

		// run recursively search file in separate threads starting from root folder
		pool.execute(new FindFileTask(root.getAbsolutePath(), fileNamePattern, textSearchPattern, builder, pool));
		builder.addTotalFolders(1);

		// wait for all task complete
		while (!pool.isEmpty() || !builder.isDone()) {}

		return builder.createResult();
	}
}
