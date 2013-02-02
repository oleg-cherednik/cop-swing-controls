package com.deutschebank.test.concurence;

/**
 * Thread pool base implementation with factory methods to create concrete instances.
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public abstract class ThreadPool {
	public static ThreadPool newFixedThreadPool(int nThreads) {
		return new FixedThreadPool(nThreads);
	}

	protected ThreadPool() {}

	/**
	 * Add new task to the pool. It will be executed as soon as possible.
	 * 
	 * @param task runnable task
	 */
	public abstract void execute(Runnable task);

	/**
	 * Returns <tt>true</tt> if all task in this poll are accomplished
	 * 
	 * @return
	 */
	public abstract boolean isEmpty();

	/** Stop all threads in the pool */
	public abstract void stop();
}
