package cop.swing.busymarker;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cop.swing.busymarker.JBusyPane;
import cop.swing.busymarker.icons.BusyIcon;
import cop.swing.busymarker.icons.InfiniteBusyIcon;
import cop.swing.busymarker.icons.RadialBusyIcon;
import cop.swing.busymarker.models.BusyAction;
import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.models.DefaultBusyModel;
import cop.swing.busymarker.ui.DefaultBusyLockableUI;

class JBusyComponentTab extends JPanel implements ActionListener, ChangeListener, Runnable {
	private static final long serialVersionUID = 3248525737133098787L;

	public static final String TITLE = "JBusyComponent";
	private static final int SPACE = 2;
	private static final Icon iconPrinter = new ImageIcon(MainDemo.class.getResource("printer.png"));

	private JBusyPane busyPane;

	private final JSpinner durationSpinner = new JSpinner();
	private final JSpinner minDurationSpinner = new JSpinner();
	private final JCheckBox cancellable = new JCheckBox("Cancellable");
	private final JCheckBox determinate = new JCheckBox("Determinate");
	private final JCheckBox remainingTime = new JCheckBox("Remaining Time");
	private final JTextField description = new JTextField();
	private final JRadioButton basicForm = new JRadioButton("Basic");
	private final JRadioButton advancedForm = new JRadioButton("Advanced");
	private final JButton executeButton = new JButton("Execute");

	private static final BusyIcon advancedIcon = new RadialBusyIcon(iconPrinter);
	private static final BusyIcon basicIcon = new InfiniteBusyIcon();

	/**
	 * A flag that indicate or not if the sample task demo running when the component is busy was canceled or not by the
	 * user. This flag will serve to the dummy thread to stop this flag raise to <code>true</code.
	 */
	private AtomicBoolean printingCanceled = new AtomicBoolean(false);

	/**
	 * The {@link BusyModel} used by the JBusyComponent for control it's busy state. This model will be configured
	 * accordingly the user settings from the "sample task configuration" After what, when the task is running, this
	 * model will be used by the dummy thread for simulate a job
	 */
	private BusyModel model = new DefaultBusyModel();

	JBusyComponentTab() {
		super(new GridBagLayout());

		init();
		addListeners();
	}

	private void init() {
		add(busyPane = createTextPart(), createTextPartConstraints(MainDemo.SPACE));
		add(createControlPanel(), createControlPanelConstraints(0));

		cancellable.setSelected(true);
		determinate.setSelected(true);
		remainingTime.setSelected(true);

		model.setMinimum(0);
		model.setMaximum(1000000);

		busyPane.setBusyModel(model);

		minDurationSpinner.setValue(1);
		durationSpinner.setValue(10);
	}

	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;

		panel.add(createTimeControlPart(), gbc);
		panel.add(createPropertyControlPart(), gbc);
		panel.add(createDescriptionPart(), gbc);
		panel.add(createFormPart(), gbc);
		panel.add(createExecutePart(), gbc);

		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1;

		panel.add(Box.createVerticalGlue(), gbc);

		return panel;
	}

	private JPanel createTimeControlPart() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.EAST;

		panel.add(new JLabel("Task duration"), gbc);
		gbc.insets.left = SPACE;
		gbc.gridwidth = 1;
		panel.add(durationSpinner, gbc);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.WEST;

		panel.add(new JLabel("seconds"), gbc);

		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets.left = 0;
		panel.add(new JLabel("Minimum task duration for showing"), gbc);
		gbc.insets.left = SPACE;
		panel.add(minDurationSpinner, gbc);
		panel.add(new JLabel("seconds"), gbc);

		((JSpinner.DefaultEditor)durationSpinner.getEditor()).getTextField().setColumns(2);
		((JSpinner.DefaultEditor)minDurationSpinner.getEditor()).getTextField().setColumns(2);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Durations"));

		return panel;
	}

	private JPanel createPropertyControlPart() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = createConstraints(GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, true);

		panel.add(cancellable, gbc);
		gbc.gridwidth = 1;
		panel.add(determinate, gbc);
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(remainingTime, gbc);

		remainingTime.setHorizontalTextPosition(SwingConstants.LEFT);
		remainingTime.setHorizontalAlignment(SwingConstants.TRAILING);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Properties"));

		return panel;
	}

	private JPanel createDescriptionPart() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = createConstraints(GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, true);

		panel.add(description, gbc);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Description"));

		return panel;
	}

	private JPanel createFormPart() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = createConstraints(GridBagConstraints.NONE, GridBagConstraints.WEST, false);

		panel.add(basicForm, gbc);
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(advancedForm, gbc);

		basicForm.setSelected(true);
		advancedForm.setHorizontalTextPosition(SwingConstants.LEADING);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Form"));

		return panel;
	}

	private JPanel createExecutePart() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = createConstraints(GridBagConstraints.NONE, GridBagConstraints.EAST, true);

		panel.add(executeButton, gbc);

		return panel;
	}

	private void addListeners() {
		basicForm.addActionListener(this);
		advancedForm.addActionListener(this);
		executeButton.addActionListener(this);
		cancellable.addActionListener(this);
		description.addActionListener(this);
		determinate.addChangeListener(this);
		model.addActionListener(this);
	}

	private void onExecuteButton() {
		String str = description.getText();

		if (str.trim().isEmpty())
			str = null;

		DefaultBusyLockableUI ui = busyPane.getBusyLayerUI();

		ui.setIcon(basicForm.isSelected() ? basicIcon : advancedIcon);
		ui.setRemainingTimeVisible(remainingTime.isSelected());
		ui.setMillisToDecideToPopup((Integer)minDurationSpinner.getValue() * 1000);

		model.setDescription(str);
		model.setCancellable(cancellable.isSelected());
		model.setDeterminate(determinate.isSelected());
		model.setBusy(true);

		setCtrlEnabled(false);

		new Thread(this).start();
	}

	private void setCtrlEnabled(boolean enabled) {
		executeButton.setEnabled(enabled);
		basicForm.setEnabled(enabled);
		advancedForm.setEnabled(enabled);
		description.setEnabled(enabled);
		durationSpinner.setEnabled(enabled);
		minDurationSpinner.setEnabled(enabled);
		cancellable.setEnabled(enabled);
		determinate.setEnabled(enabled);
		remainingTime.setEnabled(enabled && determinate.isSelected());
	}

	private void onDetermibateCheckBox() {
		remainingTime.setEnabled(determinate.isSelected());

		if (!determinate.isSelected())
			remainingTime.setSelected(false);
	}

	private void onCancellableCheckBox() {}

	private void onDescriptionTextField() {}

	private void onCancelButton() {
		printingCanceled.set(true);
		JOptionPane.showMessageDialog(this, "Lorem Ipsum printing task canceled", "Task canceled",
				JOptionPane.WARNING_MESSAGE);
	}

	private void printingModelActionPerformed(ActionEvent event) {
		if (BusyAction.CANCEL.checkEvent(event))
			onCancelButton();
	}

	// ========== Runnable ==========

	public final void run() {
		printingCanceled.set(false);

		float step = model.getRange() / ((Integer)durationSpinner.getValue()).floatValue() / 10; // 100ms refresh delay

		model.setValue(model.getMinimum());

		while (printingCanceled.get() == false && model.getValue() < model.getMaximum()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ie) {}

			model.setValue(model.getValue() + (int)step);
		}

		model.setBusy(false);
		setCtrlEnabled(true);
	}

	// ========== ActionListener ==========

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == model)
			printingModelActionPerformed(event);
		else if (event.getSource() == executeButton)
			onExecuteButton();
		else if (event.getSource() == cancellable)
			onCancellableCheckBox();
		else if (event.getSource() == description)
			onDescriptionTextField();
		else if (event.getSource() == basicForm)
			advancedForm.setSelected(!basicForm.isSelected());
		else if (event.getSource() == basicForm)
			advancedForm.setSelected(!basicForm.isSelected());
		else if (event.getSource() == advancedForm)
			basicForm.setSelected(!advancedForm.isSelected());

	}

	// ========== ChangeListener ==========

	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == determinate)
			onDetermibateCheckBox();
	}

	// ========== static ==========

	private static GridBagConstraints createConstraints(int fill, int anchor, boolean remainder) {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = fill;
		gbc.anchor = anchor;
		gbc.weightx = 1;
		gbc.gridwidth = remainder ? GridBagConstraints.REMAINDER : 1;

		return gbc;
	}

	private static GridBagConstraints createTextPartConstraints(int insetsLeft) {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.NORTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets.left = insetsLeft;
		gbc.insets.right = MainDemo.SPACE;
		gbc.insets.bottom = MainDemo.SPACE;
		gbc.insets.top = MainDemo.SPACE;
		gbc.fill = GridBagConstraints.BOTH;

		return gbc;
	}

	private static GridBagConstraints createControlPanelConstraints(int insetsLeft) {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.insets.left = insetsLeft;
		gbc.insets.right = MainDemo.SPACE;
		gbc.insets.bottom = MainDemo.SPACE;
		gbc.insets.top = MainDemo.SPACE;
		gbc.fill = GridBagConstraints.VERTICAL;

		return gbc;
	}

	private static JBusyPane createTextPart() {
		JTextArea textArea = new JTextArea();

		textArea.setBackground(Color.orange);
		textArea.setColumns(20);
		textArea.setFont(new Font("Arial", 0, 18));
		textArea.setLineWrap(true);
		textArea.setRows(5);
		textArea.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor"
				+ " incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation"
				+ " ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in"
				+ " voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non"
				+ " proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

		JBusyPane busyPane = new JBusyPane(new JScrollPane(textArea));

		busyPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Busy pane"));

		return busyPane;
	}
}
