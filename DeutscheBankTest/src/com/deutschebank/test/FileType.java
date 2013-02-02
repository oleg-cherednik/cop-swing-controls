package com.deutschebank.test;

import com.deutschebank.test.xml.DataForm;
import com.thoughtworks.xstream.XStream;

/**
 * @author Oleg Cherednik
 * @since 02.03.2013
 */
public enum FileType {
	IN("a\\d+\\.xml") {
		@Override
		protected String getUidStr(String name) {
			return name.substring(1, name.length() - 4);
		}

		@Override
		public XStream process(XStream xstream) {
			DataForm.process(xstream);
			return xstream;
		}
	},
	OUT("d\\d+\\.xml") {
		@Override
		protected String getUidStr(String name) {
			return name.substring(1, name.length() - 4);
		}

		@Override
		public XStream process(XStream xstream) {
			//DictionaryTag.process(xstream);
			return xstream;
		}
	};

	private final String regex;

	FileType(String regex) {
		this.regex = regex;
	}

	public final boolean matches(String fileName) {
		return fileName.matches(regex);
	}

	public final Long getUid(String fileName) {
		return matches(fileName) ? Long.valueOf(getUidStr(fileName)) : null;
	}

	public final Long getUid1(String fileName) {
		return matches(fileName) ? Long.valueOf(getUid1Str(fileName)) : null;
	}

	protected abstract String getUidStr(String name);

	protected String getUid1Str(String name) {
		return null;
	}

	public abstract XStream process(XStream xstream);
}
