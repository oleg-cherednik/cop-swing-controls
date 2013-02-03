package com.deutschebank.test;

import com.deutschebank.test.xml.InputData;
import com.deutschebank.test.xml.OutputData;
import com.thoughtworks.xstream.XStream;

/**
 * @author Oleg Cherednik
 * @since 02.03.2013
 */
public enum FileType {
	IN {
		@Override
		public XStream process(XStream xstream) {
			InputData.process(xstream);
			return xstream;
		}
	},
	OUT {
		@Override
		public XStream process(XStream xstream) {
			OutputData.process(xstream);
			return xstream;
		}
	};

	public abstract XStream process(XStream xstream);
}
