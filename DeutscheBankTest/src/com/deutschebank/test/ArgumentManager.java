package com.deutschebank.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.deutschebank.test.files.Result;
import com.deutschebank.test.utils.IOUtils;
import com.deutschebank.test.xml.InputData;
import com.deutschebank.test.xml.OutputData;
import com.deutschebank.test.xml.TaskTag;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public final class ArgumentManager {
	private static final ArgumentManager INSTANCE = new ArgumentManager();

	private final List<String> arguments = new ArrayList<String>();
	private final Object lock = new Object();

	public static ArgumentManager getInstance() {
		return INSTANCE;
	}

	private ArgumentManager() {}

	public void setArgumens(String... args) {
		arguments.clear();

		if (args != null)
			for (String arg : args)
				arguments.add(arg);
	}

	public String getArgument(int pos) {
		return arguments.size() > pos ? arguments.get(pos) : null;
	}

	/**
	 * Read give input arguments and read input file or ask user via console.
	 * 
	 * @return {@link InputData} data
	 * @throws FileNotFoundException
	 */
	public InputData readInputData() throws FileNotFoundException {
		String arg = getArgument(0);
		return (arg == null) ? getDefaultInputData() : IOUtils.readInXML(new File(arg));
	}

	/**
	 * Read given input arguments and write give data to output file.
	 * 
	 * @param data output data
	 * @throws FileNotFoundException
	 */
	public void writeOutputData(OutputData data) throws FileNotFoundException {
		String arg = getArgument(1);

		if (arg == null)
			return;

		File file = new File(arg);

		synchronized (lock) {
			IOUtils.writeOutXML(data, file);
		}
	}

	public void writeOutputData(Result.Builder builder) throws FileNotFoundException {
		String arg = getArgument(1);

		if (arg == null)
			return;

		File file = new File(arg);

		synchronized (lock) {
			IOUtils.writeOutXML(builder.createResult().getOutputData(), file);
		}
	}

	// ========== static ==========

	private static InputData getDefaultInputData() {
		InputData data = new InputData();

		data.setThreads(8);
		data.setOutToConsole(true);
		data.addTask(new TaskTag("c:\\", "*.txt"));

		return data;
	}
}
