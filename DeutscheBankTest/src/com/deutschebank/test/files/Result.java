package com.deutschebank.test.files;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.deutschebank.test.xml.FoundTag;
import com.deutschebank.test.xml.OutputData;
import com.deutschebank.test.xml.PathTag;

/**
 * Search result container
 * 
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class Result {
	private final OutputData outputData;

	public static Builder createBuilder() {
		return new Builder();
	}

	private Result(Builder builder) {
		outputData = builder.getXml();
	}

	public OutputData getOutputData() {
		return outputData;
	}

	// ========== Builder ==========

	public static class Builder {
		private final Object lock = new Object();
		private final Map<String, Set<String>> map = new TreeMap<String, Set<String>>();

		private Builder() {}

		public Result createResult() {
			return new Result(this);
		}

		public void addFile(String textPattern, String file) {
			synchronized (lock) {
				textPattern = textPattern != null && !textPattern.trim().isEmpty() ? textPattern : "";

				Set<String> files = map.get(textPattern);

				if (files == null)
					map.put(textPattern, files = new TreeSet<String>());

				files.add(file);
			}
		}

		private OutputData getXml() {
			synchronized (lock) {
				FoundTag foundTag;
				OutputData outputData = new OutputData();

				for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
					outputData.addResult(foundTag = new FoundTag(entry.getKey()));

					for (String path : entry.getValue())
						foundTag.addPath(new PathTag(path));
				}

				return outputData;
			}
		}
	}
}
