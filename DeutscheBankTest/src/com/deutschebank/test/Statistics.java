package com.deutschebank.test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.deutschebank.test.concurence.AtomicCounter;
import com.deutschebank.test.utils.IOUtils;
import com.deutschebank.test.xml.InputData;

/**
 * Statistics module. It collects statistics information. Thread safe.
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class Statistics {
	private static final Statistics INSTANCE = new Statistics();

	/** Started time (ms) */
	private long started;
	/** Accomplished time (ms) */
	private long accomplished;

	/** Amount of scanned file */
	private final AtomicCounter scannedFiles = new AtomicCounter();
	/** Amount of scanned folders */
	private final AtomicCounter scannedFolders = new AtomicCounter();
	/** Each task (or thread) is placed in this map */
	private final Map<String, Entry> entries = Collections.synchronizedMap(new HashMap<String, Entry>());

	public static Statistics getInstance() {
		return INSTANCE;
	}

	private Statistics() {}

	public void started() {
		started = System.currentTimeMillis();
	}

	public void accomplished() {
		accomplished = System.currentTimeMillis();
	}

	/**
	 * Returns total work time of all process
	 * 
	 * @return time in sec.
	 */
	public double getTotalWorkTime() {
		return round3((accomplished - started) / 1000);
	}

	public void addScannedFiles(int scannedFiles) {
		this.scannedFiles.add(scannedFiles);
	}

	/**
	 * Returns total scanned files
	 * 
	 * @return scanned files amount
	 */
	public int getScannedFiles() {
		return scannedFiles.getValue();
	}

	public void addScannedFolders(int scannedFolders) {
		this.scannedFolders.add(scannedFolders);
	}

	/**
	 * Returns total scanned folders
	 * 
	 * @return scanned folders amount
	 */
	public int getScannedFolders() {
		return scannedFolders.getValue();
	}

	/**
	 * Return averaged task delay time. This time is the period between task started and task created times.
	 * 
	 * @return average task delay time in sec.
	 */
	public double getAverageDelay() {
		double sum = 0;

		for (Entry entry : entries.values())
			sum += entry.started - entry.created;

		return round3(sum / entries.size() / 1000);
	}

	/**
	 * Return averaged task work time. This time is the period between task accomplished and task started times.
	 * 
	 * @return average task work time in sec.
	 */
	public double getAverageWork() {
		double sum = 0;

		for (Entry entry : entries.values())
			sum += entry.accomplished - entry.started;

		return round3(sum / entries.size() / 1000);
	}

	public void taskCreated(String id) {
		entries.put(id, new Entry());
	}

	public void taskStarted(String id) {
		Entry entry = entries.get(id);

		if (entry != null)
			entry.started = System.currentTimeMillis();
	}

	public void taskAccomplished(String id) {
		Entry entry = entries.get(id);

		if (entry != null)
			entry.accomplished = System.currentTimeMillis();
	}

	public void print(InputData inputData) {
		System.out.println("-----------------------");

		System.out.println(IOUtils.toXML(inputData));
		System.out.println("\nTime: " + getTotalWorkTime() + " sec.");
		System.out.println("Total files searched: " + getScannedFiles());
		System.out.println("Total folders searched: " + getScannedFolders());
		System.out.println("Average task delay: " + getAverageDelay() + " sec.");
		System.out.println("Average task work: " + getAverageWork() + " sec.");
	}

	// ========== static ==========

	private static double round3(double value) {
		return (Math.floor(value * 1000) + 1) / 1000;
	}

	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static boolean isEmpty(Collection<?> obj) {
		return obj == null || obj.isEmpty();
	}

	// ========== inner class ==========

	private static class Entry {
		/** Created time (ms) */
		public final long created = System.currentTimeMillis();
		/** Start time (ms) */
		public long started;
		/** Accomplished time (ms) */
		public long accomplished;
	}
}
