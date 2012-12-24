package cop.swing.busymarker;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;


import cop.swing.busymarker.JBusyPane;
import cop.swing.busymarker.icons.DefaultBusyIcon;
import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.ui.DefaultBusyLayerUI;

class BusySwingWorkerTab extends JPanel implements ActionListener {
	private static final long serialVersionUID = -6898514008918178990L;

	public static final String TITLE = "BusySwingWorker";
	private static final Icon iconRun = new ImageIcon(MainDemo.class.getResource("run.png"));

	private final JButton jButtonFillTable = new JButton(
			"Start a long task with a BusySwingWorker that fill 500 records");
	private final JBusyPane jBusyTable = new JBusyPane();
	private final JScrollPane jScrollPaneBusyTable = new JScrollPane();
	private final JTable table = new JTable();

	BusySwingWorkerTab() {
		init();
		addListeners();
	}

	private void init() {
		setLayout(createMainLayout());

		jScrollPaneBusyTable.setViewportView(table);
		table.setModel(createTableModel());
		jBusyTable.setView(jScrollPaneBusyTable);

		BusyModel model = jBusyTable.getBusyModel();
		model.setCancellable(true);
		model.setDeterminate(true);

		DefaultBusyLayerUI ui = (DefaultBusyLayerUI)jBusyTable.getBusyLayerUI();
		ui.setRemainingTimeVisible(true);
		ui.setBusyIcon(new DefaultBusyIcon(iconRun));
	}

	private LayoutManager createMainLayout() {
		GroupLayout layout = new GroupLayout(this);

		layout.setHorizontalGroup(createHorizontalGroup(layout));
		layout.setVerticalGroup(createVerticalGroup(layout));

		setLayout(layout);

		return layout;
	}

	private void addListeners() {
		jButtonFillTable.addActionListener(this);
	}

	private Group createGroup1(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);

		group.addComponent(jButtonFillTable, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 784,
				Short.MAX_VALUE);
		group.addComponent(jBusyTable, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE);

		return group;
	}

	private Group createGroup4(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addContainerGap();
		group.addGroup(createGroup1(layout));

		return group;
	}

	private Group createGroup5(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addContainerGap();
		group.addComponent(jBusyTable, GroupLayout.PREFERRED_SIZE, 355, GroupLayout.PREFERRED_SIZE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jButtonFillTable);
		group.addContainerGap(25, Short.MAX_VALUE);

		return group;
	}

	private Group createHorizontalGroup(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addGroup(GroupLayout.Alignment.TRAILING, createGroup4(layout));

		return group;
	}

	private Group createVerticalGroup(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addGroup(createGroup5(layout));

		return group;
	}

	private void jButtonFillTableActionPerformed() {
		// Create a worker
		SwingWorkerDemo worker = new SwingWorkerDemo(jBusyTable.getBusyModel(), (DefaultTableModel)table.getModel());
		// Start the worker
		worker.execute();
	}

	/*
	 * ActionListener
	 */

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == jButtonFillTable)
			jButtonFillTableActionPerformed();
	}

	/*
	 * static
	 */

	private static TableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel(0, 0);

		model.addColumn("N�");
		model.addColumn("Name");
		model.addColumn("Color");
		model.addColumn("Age");

		return model;
	}
}
