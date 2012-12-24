package cop.swing.busymarker;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JPanel;

class BusyIconTab extends JPanel {
	private static final long serialVersionUID = -1158656511797704667L;

	public static final String TITLE = "BusyIcon";

	BusyIconTab() {
		super(new GridBagLayout());

		init();
	}

	private void init() {
		add(new JLabelBusyPanel(), createConstraints());
		add(new JLabelBusyPanel(), createConstraints());
		add(Box.createVerticalGlue(), createGlueConstraints());
	}
	
	// ========== static ==========

	private static GridBagConstraints createConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets.left = MainDemo.SPACE;
		gbc.insets.right = MainDemo.SPACE;
		gbc.insets.top = MainDemo.SPACE;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;

		return gbc;
	}

	static GridBagConstraints createGlueConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;

		return gbc;
	}
}
