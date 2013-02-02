package com.deutschebank.test.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
@XStreamAlias(InputData.TITLE)
public class InputData {
	public static final String TITLE = "input";

	public static final String THREADS = "threads";

	@XStreamAlias(THREADS)
	@XStreamAsAttribute
	private int threads;

	@XStreamImplicit(itemFieldName = TaskTag.TITLE)
	private List<TaskTag> tasks;

	public int getThreads() {
		return Math.max(1, threads);
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public void addTask(TaskTag task) {
		if (tasks == null)
			tasks = new ArrayList<TaskTag>();
		tasks.add(task);
	}

	public List<TaskTag> getTasks() {
		return (tasks == null || tasks.isEmpty()) ? Collections.<TaskTag> emptyList() : tasks;
	}

	// ========== static ==========

	public static void process(XStream xstream) {
		xstream.processAnnotations(InputData.class);
		TaskTag.process(xstream);
	}
}
