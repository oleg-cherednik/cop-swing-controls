package cop.swing.utils;

import java.util.Enumeration;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class ListProperties {
	public static void main(String args[]) throws Exception {
		UIManager.LookAndFeelInfo looks[] = UIManager.getInstalledLookAndFeels();
		
//		LookAndFeel laf = (LookAndFeel)Class.forName(UIManager.getSystemLookAndFeelClassName()).newInstance();
//		UIManager.setLookAndFeel(laf);
//		UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");

		for (UIManager.LookAndFeelInfo info : looks) {
			System.out.println();
			System.out.println("--------------------- " + info + " ---------------------");
			System.out.println();
			
			UIManager.setLookAndFeel(info.getClassName());

			UIDefaults defaults = UIManager.getDefaults();
			Enumeration newKeys = defaults.keys();

			while (newKeys.hasMoreElements()) {
				Object key = newKeys.nextElement();
				Object value = UIManager.get(key);
				
				if(key.toString().contains("ProgressBar") /* && value instanceof ColorUIResource*/)
					System.out.printf("%50s : %s\n", key, value);
			}
		}
	}
}
