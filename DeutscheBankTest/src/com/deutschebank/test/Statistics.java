package com.deutschebank.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.deutschebank.test.concurence.AtomicCounter;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class Statistics {
	private static final Statistics INSTANCE = new Statistics();

	private long started;
	private long accomplished;
	private final AtomicCounter scannedFiles = new AtomicCounter();
	private final AtomicCounter scannedFolders = new AtomicCounter();
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

	// in sec.
	public double getTotalWorkTime() {
		return round3((accomplished - started) / 1000);
	}

	public void addScannedFiles(int scannedFiles) {
		this.scannedFiles.add(scannedFiles);
	}

	public int getScannedFiles() {
		return scannedFiles.getValue();
	}

	public void addScannedFolders(int scannedFolders) {
		this.scannedFolders.add(scannedFolders);
	}

	public int getScannedFolders() {
		return scannedFolders.getValue();
	}

	// in sec.
	public double getAverageDelay() {
		double sum = 0;

		for (Entry entry : entries.values())
			sum += entry.started - entry.created;

		return round3(sum / entries.size() / 1000);
	}

	// in sec.
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

	// ========== static ==========

	private static double round3(double value) {
		return (Math.floor(value * 1000) + 1) / 1000;
	}

	// ========== inner class ==========

	private static class Entry {
		public final long created = System.currentTimeMillis();
		public long started;
		public long accomplished;
	}
}
