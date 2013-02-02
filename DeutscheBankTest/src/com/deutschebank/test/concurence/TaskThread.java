package com.deutschebank.test.concurence;

import java.util.Queue;

/**
 * Task runnable implementation
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
final class TaskThread extends Thread {
	/** Task queue for the thread */
	private final Queue<Runnable> queue;

	public TaskThread(ThreadGroup group, String name, Queue<Runnable> queue) {
		super(group, name);
		this.queue = queue;
	}

	/** Returns <tt>true</tt> if task queue is empty */
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	// ========== Runnable ==========

	public void run() {
		Runnable task;

		// if queue has task then run it
		while (true) {
			synchronized (queue) {
				while (queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
						return;
					}
				}

				task = queue.remove();
			}

			// If we don't catch RuntimeException, the pool could leak threads
			try {
				task.run();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
}
