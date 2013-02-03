package com.deutschebank.test.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
@XStreamAlias(TaskTag.TITLE)
public class TaskTag {
	public static final String TITLE = "task";
	public static final String ROOT = "root";
	public static final String FILE_PATTERN = "filePattern";
	public static final String TEXT_PATTERN = "textPattern";

	public TaskTag() {}

	public TaskTag(String root, String filePattern) {
		setRoot(root);
		setFilePattern(filePattern);
	}

	@XStreamAlias(ROOT)
	@XStreamAsAttribute
	private String root;

	@XStreamAlias(FILE_PATTERN)
	@XStreamAsAttribute
	private String filePattern;

	@XStreamAlias(TEXT_PATTERN)
	@XStreamAsAttribute
	private String textPattern;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getFilePattern() {
		return convertToJavaPattern(filePattern);
	}

	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}

	public String getTextPattern() {
		return convertToJavaPattern(textPattern);
	}

	public void setTextPattern(String textPattern) {
		this.textPattern = textPattern;
	}

	// ========== static ==========

	public static void process(XStream xstream) {
		xstream.processAnnotations(TaskTag.class);
	}

	/**
	 * Convert Windows pattern to Java pattern
	 * 
	 * @param winMask windows pattern
	 * @return java pattern
	 */
	private static String convertToJavaPattern(String winMask) {
		if (winMask == null || winMask.isEmpty())
			return null;
		return winMask.replaceAll("\\.", "\\\\.").replaceAll("\\?", ".{1}").replaceAll("\\*", ".*");
	}
}
