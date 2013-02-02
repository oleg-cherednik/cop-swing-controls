package com.deutschebank.test.files.tasks;

import com.deutschebank.test.files.Result;

/**
 * Abstract task of the file operations
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
abstract class AbstractFileTask implements Runnable {
	/**
	 * Result store of the task.<br>
	 * {@link Result#inc()} is called in constructor to count runnable tasks in pool, {@link Result#dec()} is called at
	 * the end of {@link Runnable#run()}.
	 */
	protected final Result.Builder resultBuilder;

	protected AbstractFileTask(Result.Builder resultBuilder) {
		this.resultBuilder = resultBuilder;
		resultBuilder.inc();
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
			resultBuilder.dec();
		}
	}
}
