package com.deutschebank.test.concurence;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Thread pool implementation with fixed threads amount
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
final class FixedThreadPool extends ThreadPool {
	/** Pool threads */
	private final TaskThread[] threads;
	/** Threads group. All threads are in it. */
	private final ThreadGroup group = new ThreadGroup(FixedThreadPool.class.getSimpleName() + "Group");
	/** Task queue */
	private final Queue<Runnable> queue = new LinkedList<Runnable>();

	/** @param nThreads number of therads in pool */
	public FixedThreadPool(int nThreads) {
		threads = new TaskThread[Math.max(1, nThreads)];

		// create exactly nThreads threads
		for (int i = 0; i < nThreads; i++) {
			TaskThread thread = new TaskThread(group, FixedThreadPool.class.getSimpleName() + ".task:" + i, queue);

			threads[i] = thread;
			thread.start();
		}
	}

	// ========== ThreadPool ==========

	public void execute(Runnable task) {
		synchronized (queue) {
			queue.add(task);
			queue.notify();
		}
	}

	public boolean isEmpty() {
		if (queue.isEmpty())
			for (TaskThread thread : threads)
				if (!thread.isEmpty())
					return false;

		return true;
	}

	public void stop() {
		group.interrupt();
	}
}
