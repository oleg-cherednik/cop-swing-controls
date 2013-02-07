package cop.db;

import java.io.File;
import java.security.CodeSource;

public class MainModule {
	// private MainModule(int i) {
	// if (i == 0) {
	// for (int j = 1; j <= 1; j++) {
	// try {
	// ProcessBuilder builder = new ProcessBuilder("d:\\db\\run.bat");
	// // builder.command("" + j);
	// final Process process = builder.start();
	//
	// new Thread() {
	// public void run() {
	// byte[] buf;
	//
	// while (true) {
	// try {
	// if (process.getInputStream().available() != 0) {
	// buf = new byte[process.getInputStream().available()];
	// int total = process.getInputStream().read(buf);
	// System.out.println(new String(buf));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// }
	// }.start();
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// try {
	// while (true) {
	// Thread.sleep(1000);
	// System.out.println("id: 0, time: " + System.currentTimeMillis() / 1000);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// ========== static ==========

	public static void main(String[] args) throws Exception {
		CodeSource codeSource = MainModule.class.getProtectionDomain().getCodeSource();
		File jarFile = new File(codeSource.getLocation().toURI().getPath());
		String jarDir = jarFile.getPath();
		
		System.out.println(jarDir);
		
		System.out.println("MainModule: " + args[0]);
		// The batch file to execute
		final File batchFile = new File("d:\\db\\run.bat");// "batch\\process.bat");

		// The output file. All activity is written to this file
		final File outputFile = new File(String.format("d:\\db\\out.txt", /*
																		 * "output\\output_%tY%<tm%<td_%<tH%<tM%<tS.txt"
																		 * ,
																		 */
				System.currentTimeMillis()));

		// The argument to the batch file.
		final String argument = "Albert Attard";

		// Create the process
		// final ProcessBuilder processBuilder = new
		// ProcessBuilder(batchFile.getAbsolutePath(), argument);
		final ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "d:\\db\\db.jar", "1");// batchFile.getAbsolutePath(),
																										// argument);
		// Redirect any output (including error) to a file. This avoids
		// deadlocks
		// when the buffers get full.
		processBuilder.redirectErrorStream(true);
		processBuilder.redirectOutput(outputFile);

		// Add a new environment variable
		processBuilder.environment().put("message", "Example of process builder");
		processBuilder.environment().put("id", "1");

		// Set the working directory. The batch file will run as if you are in
		// this
		// directory.
		processBuilder.directory(new File("d:\\db"));

		// Start the process and wait for it to finish.
		final Process process = processBuilder.start();
		final int exitStatus = process.waitFor();
		System.out.println("Processed finished with status: " + exitStatus);
	}

}
