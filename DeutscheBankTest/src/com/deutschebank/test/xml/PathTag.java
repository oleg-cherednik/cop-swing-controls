package com.deutschebank.test.xml;

import com.deutschebank.test.Statistics;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
@XStreamAlias(PathTag.TITLE)
@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "path" })
public class PathTag implements Comparable<PathTag> {
	public static final String TITLE = "path";
	public static final String PATH = "path2";

	// @XStreamAlias(PATH)
	// @XStreamConverter(StringConverter.class)
	private String path;

	public PathTag() {}

	public PathTag(String path) {
		setPath(path);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	// ========== Comparable ==========

	@Override
	public int compareTo(PathTag obj) {
		if (obj == null)
			return -1;
		if (Statistics.isEmpty(path))
			return Statistics.isEmpty(obj.path) ? 0 : -1;
		return path.compareToIgnoreCase(obj.path);
	}

	// ========== static ==========

	public static void process(XStream xstream) {
		xstream.processAnnotations(PathTag.class);
	}
}
