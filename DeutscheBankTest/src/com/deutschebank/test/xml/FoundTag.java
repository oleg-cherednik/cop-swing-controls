package com.deutschebank.test.xml;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.deutschebank.test.Statistics;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
@XStreamAlias(FoundTag.TITLE)
public class FoundTag implements Comparable<FoundTag> {
	public static final String TITLE = "found";
	public static final String SUBSTRING = "substring";

	@XStreamAlias(SUBSTRING)
	@XStreamAsAttribute
	private String substring;

	@XStreamImplicit(itemFieldName = PathTag.TITLE)
	private Set<PathTag> paths;

	public FoundTag() {}

	public FoundTag(String substring) {
		setSubstring(substring);
	}

	public String getSubstring() {
		return substring;
	}

	public void setSubstring(String substring) {
		this.substring = substring;
	}

	public void addPath(PathTag path) {
		if (paths == null)
			paths = new TreeSet<PathTag>();
		paths.add(path);
	}

	public Set<PathTag> getTasks() {
		return Statistics.isEmpty(paths) ? Collections.<PathTag> emptySet() : paths;
	}

	// ========== Comparable ==========

	@Override
	public int compareTo(FoundTag obj) {
		if (obj == null)
			return -1;
		if (Statistics.isEmpty(substring))
			return Statistics.isEmpty(obj.substring) ? 0 : -1;
		return substring.compareToIgnoreCase(obj.substring);
	}

	// ========== static ==========

	public static void process(XStream xstream) {
		xstream.processAnnotations(FoundTag.class);
		PathTag.process(xstream);
	}
}
