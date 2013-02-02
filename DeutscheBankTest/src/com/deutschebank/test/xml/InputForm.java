package com.deutschebank.test.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
@XStreamAlias(InputForm.TITLE)
public class InputForm {
	public static final String TITLE = "input";

	@XStreamImplicit(itemFieldName = TaskTag.TITLE)
	private List<TaskTag> tasks;

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
		xstream.processAnnotations(InputForm.class);
		TaskTag.process(xstream);
	}
}
