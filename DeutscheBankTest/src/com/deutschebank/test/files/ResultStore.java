package com.deutschebank.test.files;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public interface ResultStore {
	void incRunningTasksAmount();

	void decRunningTasksAmount();

	void addFile(String regex, String file);

	void addTotalFiles(int totalFiles);

	void addTotalFolders(int totalFolders);
}
