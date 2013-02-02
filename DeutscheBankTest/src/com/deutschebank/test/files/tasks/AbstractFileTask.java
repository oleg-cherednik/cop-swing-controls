package com.deutschebank.test.files.tasks;

import com.deutschebank.test.files.ResultStore;

/**
 * Abstract task of the file operations
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
abstract class AbstractFileTask implements Runnable {
	protected final ResultStore out;

	protected AbstractFileTask(ResultStore out) {
		assert out != null;

		this.out = out;
		out.incRunningTasksAmount();
	}

	protected abstract void runImpl();

	// ========== Runnable ==========

	public final void run() {
		if (Thread.currentThread().isInterrupted())
			return;

		try {
			runImpl();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.decRunningTasksAmount();
		}
	}
}
