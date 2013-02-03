package com.deutschebank.test.files;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Search result container
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class Result {
	/** Result set */
	private final Set<String> resultFiles;

	public static Builder createBuilder() {
		return new Builder();
	}

	private Result(Builder builder) {
		resultFiles = builder.getResultFiles();
	}

	public Set<String> getResultFiles() {
		return resultFiles;
	}

	// ========== Builder ==========

	public static class Builder {
		private final Set<String> resultFiles = Collections.synchronizedSet(new TreeSet<String>());

		private Builder() {}

		public Result createResult() {
			return new Result(this);
		}

		public void addFile(String file) {
			resultFiles.add(file);
		}

		private Set<String> getResultFiles() {
			return resultFiles.isEmpty() ? Collections.<String> emptySet() : Collections.unmodifiableSet(resultFiles);
		}
	}
}
