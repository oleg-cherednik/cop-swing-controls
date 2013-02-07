package cop.db;

import java.io.File;
import java.net.URI;
import java.net.URL;
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
		int id = Integer.parseInt(args[0]);

		System.out.println("id: " + id + " - start main module");
		CodeSource codeSource = MainModule.class.getProtectionDomain().getCodeSource();
		File jarFile = new File(codeSource.getLocation().toURI().getPath());
		URL url = MainModule.class.getResource("MainModule.class");
		File f = new File(url.toURI());
		File parentFile = f.getParentFile();
		boolean isFile = jarFile.isFile();

		String classPath = jarFile.getPath();
		String className = MainModule.class.getName();
		
		String jarDir = isFile ? jarFile.getPath() : f.getPath();

		System.out.println(jarDir);

		if (Integer.parseInt(args[0]) != 0) {
			System.out.println("id: " + id + " - stop main module");
			return;
		}

		// The batch file to execute
		// final File batchFile = new File("d:\\db\\run.bat");//
		// "batch\\process.bat");

		// The output file. All activity is written to this file

		// java -classpath my_class.class

		final File outputFile = new File(String.format("d:\\db\\out.txt", System.currentTimeMillis()));

		// The argument to the batch file.
		// final String argument = "Albert Attard";
		
		

		// Create the process
		// final ProcessBuilder processBuilder = new
		// ProcessBuilder(batchFile.getAbsolutePath(), argument);
		// final ProcessBuilder processBuilder = new ProcessBuilder("java",
		// "-jar", jarDir, "" + (id + 1));
//		final ProcessBuilder processBuilder = new ProcessBuilder("java", "-classpath", jarDir, "" + (id + 1));
		final ProcessBuilder processBuilder = new ProcessBuilder("java", "-classpath", classPath, className, "" + (id + 1));
		// Redirect any output (including error) to a file. This avoids
		// deadlocks
		// when the buffers get full.
		processBuilder.redirectErrorStream(true);
		processBuilder.redirectOutput(outputFile);

		// Add a new environment variable
		// processBuilder.environment().put("message",
		// "Example of process builder");
		// processBuilder.environment().put("id", "1");

		// Set the working directory. The batch file will run as if you are in
		// this
		// directory.
		processBuilder.directory(jarFile.getParentFile());

		System.out.println("id: " + id + " - start new process with id=" + (id + 1));
		// Start the process and wait for it to finish.
		final Process process = processBuilder.start();
		final int exitStatus = process.waitFor();
		System.out.println("id: " + id + " - Processed finished with status: " + exitStatus);
		System.out.println("Processed finished with status: " + exitStatus);
	}

}
