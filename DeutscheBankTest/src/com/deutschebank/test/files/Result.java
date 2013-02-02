package com.deutschebank.test.files;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.deutschebank.test.concurence.AtomicCounter;

/**
 * Search result container
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class Result {
	/** Result set */
	private final Set<String> resultFiles;
	/** Total scanned files */
	private final int totalFiles;
	/** Total scanned folders */
	private final int totalFolders;

	public static Builder createBuilder() {
		return new Builder();
	}

	private Result(Builder builder) {
		resultFiles = builder.getResultFiles();
		totalFiles = builder.totalFiles.getValue();
		totalFolders = builder.totalFolders.getValue();
	}

	public Set<String> getResultFiles() {
		return resultFiles;
	}

	public int getTotalFiles() {
		return totalFiles;
	}

	public int getTotalFolders() {
		return totalFolders;
	}

	// ========== Builder ==========

	public static class Builder {
		private final Set<String> resultFiles = Collections.synchronizedSet(new TreeSet<String>());
		private final AtomicCounter totalFiles = new AtomicCounter();
		private final AtomicCounter totalFolders = new AtomicCounter();

		/**
		 * Amount of running threads. It uses only for internal usage.<br>
		 * Id <tt>count == 0</tt> then all task accomplished.
		 */
		private final AtomicCounter count = new AtomicCounter();

		private Builder() {}

		public Result createResult() {
			return new Result(this);
		}

		public void inc() {
			count.inc();
		}

		public void dec() {
			count.dec();
		}

		boolean isDone() {
			return count.getValue() <= 0;
		}

		public void addFile(String file) {
			resultFiles.add(file);
		}

		public void addTotalFiles(int totalFiles) {
			this.totalFiles.add(totalFiles);
		}

		public void addTotalFolders(int totalFolders) {
			this.totalFolders.add(totalFolders);
		}

		private Set<String> getResultFiles() {
			return resultFiles.isEmpty() ? Collections.<String> emptySet() : Collections.unmodifiableSet(resultFiles);
		}
	}
}
