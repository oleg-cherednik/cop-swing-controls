package com.deutschebank.test.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(TaskTag.TITLE)
public class TaskTag {
	public static final String TITLE = "task";
	public static final String ROOT = "root";
	public static final String FILE_PATTERN = "file_pattern";
	public static final String TEXT_PATTERN = "text_pattern";

	@XStreamAlias(ROOT)
	@XStreamAsAttribute
	private String toot;

	@XStreamAlias(FILE_PATTERN)
	@XStreamAsAttribute
	private String filePattern;

	@XStreamAlias(TEXT_PATTERN)
	@XStreamAsAttribute
	private String textPattern;

	public String getRoot() {
		return toot;
	}

	public void setRoot(String root) {
		this.toot = root;
	}

	public String getFilePattern() {
		return filePattern;
	}

	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}

	public String getTextPattern() {
		return textPattern;
	}

	public void setTextPattern(String textPattern) {
		this.textPattern = textPattern;
	}

	// ========== static ==========

	public static void process(XStream xstream) {
		xstream.processAnnotations(TaskTag.class);
	}
}
