package com.deutschebank.test;

import java.io.File;
import java.util.Collection;

import com.deutschebank.test.concurence.AtomicCounter;
import com.deutschebank.test.concurence.ThreadPool;
import com.deutschebank.test.files.Result;
import com.deutschebank.test.files.ResultStore;
import com.deutschebank.test.files.tasks.FindFileTask;
import com.deutschebank.test.xml.TaskTag;

/**
 * File manager. It contains methods to search file with giving name pattern ({@link #fileNameRegex}) or/and text in
 * file pattern ({@link #textSearchRegex}).
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class ScanManager implements ResultStore {
	/** Thread pool */
	private final ThreadPool pool;
	private final boolean outToConsole;
	/** Result store */
	private final Result.Builder builder = Result.createBuilder();
	/**
	 * Amount of running threads. It uses only for internal usage.<br>
	 * Id <tt>count == 0</tt> then all task are accomplished.
	 */
	private final AtomicCounter runningThreadsAmount = new AtomicCounter();

	/** @param nThreads thread amount (more than zero) */
	public ScanManager(int nThreads, boolean outToConsole) {
		pool = ThreadPool.newFixedThreadPool(nThreads);
		this.outToConsole = outToConsole;
	}

	/** Dispose this object. Stop used thread pool. */
	public void dispose() {
		pool.stop();
	}

	public Result proceed(Collection<TaskTag> tasks) throws Exception {
		if (tasks == null || tasks.isEmpty())
			return Result.createBuilder().createResult();

		for (TaskTag task : tasks)
			proceed(task);

		waitForAllTaskComplete();

		return builder.createResult();
	}

	private void waitForAllTaskComplete() {
		while (!pool.isEmpty() || runningThreadsAmount.getValue() != 0) {}
	}

	private void proceed(TaskTag task) throws Exception {
		if (task == null)
			return;

		File root = new File(task.getRoot());

		checkRoot(root);

		String rootPath = root.getAbsolutePath();
		String fileNameRegex = task.getFilePattern();
		String textSearchRegex = task.getTextPattern();

		pool.execute(new FindFileTask(rootPath, fileNameRegex, textSearchRegex, pool, this));
		Statistics.getInstance().addScannedFolders(1);
	}

	// ========== ResultListener ==========

	public void incRunningTasksAmount() {
		runningThreadsAmount.inc();
	}

	public void decRunningTasksAmount() {
		runningThreadsAmount.dec();
	}

	public void addFile(String textPattern, String file) {
		if (outToConsole)
			System.out.println((Statistics.isEmpty(textPattern) ? "" : textPattern + " - ") + file);

		builder.addFile(textPattern, file);
	}

	// ========== static ==========

	private static void checkRoot(File root) throws Exception {
		if (!root.exists())
			throw new Exception("Root '" + root + "' is not exist");
		if (!root.isDirectory())
			throw new Exception("Root '" + root + "' is not directory");
	}
}
