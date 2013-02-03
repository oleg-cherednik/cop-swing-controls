package com.deutschebank.test;

import com.deutschebank.test.files.Result;
import com.deutschebank.test.xml.InputData;

/**
 * @author Oleg Cherednik
 * @since 02.02.2013
 */
public class Main {
	private final InputData inputData;

	/**
	 * Command line arguments:
	 * <ul>
	 * <li><b>1.</b> input xml file
	 * <li><b>2.</b> output xml file
	 * </ul>
	 */
	public static void main(String... args) {
		try {
			Main mainModule = new Main(args);
			Result res = mainModule.proceed();

			ArgumentManager.getInstance().writeOutputData(res.getOutputData());
			Statistics.getInstance().print(mainModule.inputData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Main(String... args) throws Exception {
		ArgumentManager.getInstance().setArgumens(args);
		inputData = ArgumentManager.getInstance().readInputData();

		if (inputData == null)
			throw new IllegalArgumentException("Input data is not set");
	}

	public Result proceed() {
		Statistics.getInstance().started();

		int nThreads = inputData.getThreads();
		boolean outToConsole = inputData.isOutToConsole();
		boolean upToDateOutput = inputData.isUpToDateOutput();
		ScanManager scanManager = new ScanManager(nThreads, outToConsole, upToDateOutput);

		try {
			return scanManager.proceed(inputData.getTasks());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Statistics.getInstance().accomplished();
			scanManager.dispose();
			scanManager = null;
		}

		return Result.createBuilder().createResult();
	}
}
