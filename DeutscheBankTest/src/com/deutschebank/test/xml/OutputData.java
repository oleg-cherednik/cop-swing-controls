package com.deutschebank.test.xml;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
@XStreamAlias(OutputData.TITLE)
public class OutputData {
	public static final String TITLE = "output";

	@XStreamImplicit(itemFieldName = FoundTag.TITLE)
	private Set<FoundTag> results;

	public void addResult(FoundTag result) {
		if (results == null)
			results = new TreeSet<FoundTag>();
		results.add(result);
	}

	public Set<FoundTag> getResults() {
		return (results == null || results.isEmpty()) ? Collections.<FoundTag> emptySet() : results;
	}

	// ========== static ==========

	public static void process(XStream xstream) {
		xstream.processAnnotations(OutputData.class);
		FoundTag.process(xstream);
	}
}
