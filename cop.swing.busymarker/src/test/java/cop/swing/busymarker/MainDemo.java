package cop.swing.busymarker;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * @author Oleg Cherednik
 * @since 30.09.2012
 */
public class MainDemo extends JPanel {
	private static final long serialVersionUID = -8694712606492281527L;

	static final int SPACE = 2;
	static final Font FONT = new Font("Arial", 1, 18);

	public MainDemo() {
		super(new GridBagLayout());

		add(createTitleLabel(), createTitleLabelConstraints());
		add(createTabbedPane(), createTabbedPaneConstraints());
	}

	// ========== static ==========

	private static JTabbedPane createTabbedPane() {
		JTabbedPane tabPane = new JTabbedPane();

		tabPane.addTab(JBusyComponentTab.TITLE, new JBusyComponentTab());
		tabPane.addTab(BusyIconTab.TITLE, new BusyIconTab());
		tabPane.addTab(HubTab.TITLE, new HubTab());
		tabPane.addTab(BusySwingWorkerTab.TITLE, new BusySwingWorkerTab());

		return tabPane;
	}

	private static JLabel createTitleLabel() {
		JLabel label = new JLabel("<html>LGPL <u>JBusyPane</u> demonstration</html>");

		label.setFont(FONT);
		label.setForeground(Color.blue);
		label.setHorizontalAlignment(SwingConstants.CENTER);

		return label;
	}

	private static GridBagConstraints createTitleLabelConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.insets.left = SPACE;
		gbc.insets.right = SPACE;
		gbc.insets.bottom = SPACE;
		gbc.insets.top = SPACE;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		return gbc;
	}

	private static GridBagConstraints createTabbedPaneConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets.left = SPACE;
		gbc.insets.right = SPACE;
		gbc.insets.bottom = SPACE;
		gbc.fill = GridBagConstraints.BOTH;

		return gbc;
	}

	public static void main(String args[]) {
		// java.awt.EventQueue.invokeLater(new Runnable() {
		// public void run() {
		try {
			// LookAndFeel laf = (LookAndFeel)Class.forName(UIManager.getSystemLookAndFeelClassName()).newInstance();
			// UIManager.setLookAndFeel(laf);
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
			JFrame frame = new JFrame();

			frame.setTitle("JBusyPane Demo");
			frame.getContentPane().add(new MainDemo());
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// frame.setIconImage( Toolkit.getDefaultToolkit().createImage(
			// getClass().getResource("/busy-logo.png") ) );
			frame.setVisible(true);
			// }
			// });

			// UIManager.getString("OptionPane.cancelButtonText")
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
