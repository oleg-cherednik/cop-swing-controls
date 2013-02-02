package com.deutschebank.test.concurence;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class AtomicCounter {
	private int value = 0;

	public synchronized int inc() {
		return ++value;
	}

	public synchronized int add(int delta) {
		return value += delta;
	}

	public synchronized int dec() {
		return --value;
	}

	public synchronized int getValue() {
		return value;
	}
}
