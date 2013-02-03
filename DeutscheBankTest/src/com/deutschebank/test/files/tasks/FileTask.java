package com.deutschebank.test.files.tasks;

import com.deutschebank.test.Statistics;
import com.deutschebank.test.files.ResultStore;

/**
 * Abstract task of the file operations
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public abstract class FileTask implements Runnable {
	protected final String id;
	protected final ResultStore out;

	protected FileTask(String id, ResultStore out) {
		assert id != null;
		assert out != null;

		this.id = id;
		this.out = out;

		Statistics.getInstance().taskCreated(id);
		out.incRunningTasksAmount();
	}

	protected abstract void runImpl();

	// ========== Runnable ==========

	public final void run() {
		if (Thread.currentThread().isInterrupted())
			return;

		try {
			Statistics.getInstance().taskStarted(id);
			runImpl();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Statistics.getInstance().taskAccomplished(id);
			out.decRunningTasksAmount();
		}
	}
}
