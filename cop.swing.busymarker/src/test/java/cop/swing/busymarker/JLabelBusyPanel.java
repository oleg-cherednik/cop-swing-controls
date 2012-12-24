package cop.swing.busymarker;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cop.swing.busymarker.icons.BusyIcon;
import cop.swing.busymarker.icons.DefaultBusyIcon;
import cop.swing.busymarker.icons.InfiniteBusyIcon;
import cop.swing.busymarker.icons.RadialBusyIcon;
import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.models.DefaultBusyModel;
import cop.swing.busymarker.plaf.BusyPaneUI;

/**
 * @author Oleg Cherednik
 * @since 29.09.2012
 */
class JLabelBusyPanel extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 7088288070891170926L;

	private static final Icon iconMonitor = new ImageIcon(MainDemo.class.getResource("system.png"));
	private static final Icon iconBattery = new ImageIcon(MainDemo.class.getResource("klaptopdaemon.png"));
	private static final Icon iconSearch = new ImageIcon(MainDemo.class.getResource("xmag.png"));

	private final JLabel obj = new JLabel("process");

	private final JSpinner undeterminateAdvanceLengthSpinner = new JSpinner();
	private final JSpinner delaySpinner = new JSpinner();

	private final JRadioButton formRadialButton = new JRadioButton("radial");
	private final JRadioButton formSquareButton = new JRadioButton("square");
	private final JRadioButton formCamomileButton = new JRadioButton("camomile");

	private final JRadioButton iconMonitorButton = new JRadioButton("monitor");
	private final JRadioButton iconBatteryButton = new JRadioButton("battery");
	private final JRadioButton iconSearchButton = new JRadioButton("search");
	private final JRadioButton iconNoneButton = new JRadioButton("none");

	private final JCheckBox determinateButton = new JCheckBox("determinate");
	private final JCheckBox busyButton = new JCheckBox("busy");
	private final JSlider slider = new JSlider();

	private final BusyModel model = new DefaultBusyModel();
	private final RadialBusyIcon radialBusyIcon = new RadialBusyIcon(iconMonitor, model);
	private final DefaultBusyIcon squareBusyIcon = new DefaultBusyIcon(iconMonitor, model);
	private final BusyIcon basicIcon = new InfiniteBusyIcon(model);
	private final DefaultBusyIcon lineBusyIcon = new DefaultBusyIcon(iconMonitor, model);

	JLabelBusyPanel() {
		super(new GridBagLayout());

		init();
		addListeners();
		update();
	}

	private void init() {
		add(createMainPart(), createMainPartConstraints());
		add(createStatePart(), createPartConstraints(false));
		add(createFormPart(), createPartConstraints(false));
		add(createIconPart(), createPartConstraints(true));
		add(createSliderPart(), createSliderPartConstraints());

		setBorder(BorderFactory.createEtchedBorder());

		busyButton.setSelected(true);
		determinateButton.setSelected(true);
		iconMonitorButton.setSelected(true);
		formRadialButton.setSelected(true);

		undeterminateAdvanceLengthSpinner.setValue(UIManager.getInt(BusyPaneUI.BI_UNDETERMINATE_ADVANCE_LENGTH));
		delaySpinner.setValue(UIManager.getInt(BusyPaneUI.BI_DELAY));
	}

	private void addListeners() {
		formRadialButton.addActionListener(this);
		formSquareButton.addActionListener(this);
		formCamomileButton.addActionListener(this);

		iconMonitorButton.addActionListener(this);
		iconBatteryButton.addActionListener(this);
		iconSearchButton.addActionListener(this);
		iconNoneButton.addActionListener(this);

		slider.addChangeListener(this);

		busyButton.addActionListener(this);
		determinateButton.addActionListener(this);

		undeterminateAdvanceLengthSpinner.addChangeListener(this);
		delaySpinner.addChangeListener(this);
	}

	private JLabel createMainPart() {
		obj.setIcon(getSelectedBusyIcon());
		// obj.setBackground(Color.white);
		obj.setFont(MainDemo.FONT);
		obj.setForeground(Color.blue);
		obj.setHorizontalAlignment(SwingConstants.CENTER);
		obj.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "JLabel"));
		obj.setOpaque(true);

		return obj;
	}

	private JPanel createStatePart() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		panel.add(busyButton, gbc);
		panel.add(determinateButton, gbc);
		panel.add(undeterminateAdvanceLengthSpinner, gbc);
		panel.add(delaySpinner, gbc);
		panel.add(Box.createVerticalGlue(), BusyIconTab.createGlueConstraints());

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "State"));

		((JSpinner.DefaultEditor)undeterminateAdvanceLengthSpinner.getEditor()).getTextField().setColumns(3);

		SpinnerNumberModel model = (SpinnerNumberModel)undeterminateAdvanceLengthSpinner.getModel();
		model.setMinimum(0);
		model.setMaximum(360);

		model = (SpinnerNumberModel)delaySpinner.getModel();
		model.setMinimum(0);
		model.setStepSize(500);

		return panel;
	}

	private JPanel createFormPart() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		panel.add(formRadialButton, gbc);
		panel.add(formSquareButton, gbc);
		panel.add(formCamomileButton, gbc);
		panel.add(Box.createVerticalGlue(), BusyIconTab.createGlueConstraints());

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Form"));

		return panel;
	}

	private JPanel createIconPart() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		panel.add(iconMonitorButton, gbc);
		panel.add(iconBatteryButton, gbc);
		panel.add(iconSearchButton, gbc);
		panel.add(iconNoneButton, gbc);
		panel.add(Box.createVerticalGlue(), BusyIconTab.createGlueConstraints());

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Icon"));

		return panel;
	}

	private JSlider createSliderPart() {
		return slider;
	}

	private BusyIcon getSelectedBusyIcon() {
		if (formRadialButton.isSelected())
			return radialBusyIcon;
		if (formSquareButton.isSelected())
			return squareBusyIcon;
		if (formCamomileButton.isSelected())
			return basicIcon;

		return radialBusyIcon;
	}

	private Icon getSelectedBaseIcon() {
		if (iconMonitorButton.isSelected())
			return iconMonitor;
		if (iconBatteryButton.isSelected())
			return iconBattery;
		if (iconSearchButton.isSelected())
			return iconSearch;

		return null;
	}

	private void update() {
		model.setBusy(busyButton.isSelected());
		model.setDeterminate(determinateButton.isSelected());
		model.setMinimum(slider.getMinimum());
		model.setMaximum(slider.getMaximum());
		model.setValue(slider.getValue());

		determinateButton.setEnabled(busyButton.isSelected());
		slider.setEnabled(busyButton.isSelected() && determinateButton.isSelected());

		radialBusyIcon.setIcon(getSelectedBaseIcon());
		squareBusyIcon.setIcon(getSelectedBaseIcon());
		lineBusyIcon.setIcon(getSelectedBaseIcon());

		radialBusyIcon.setUndeterminateAdvanceLength((Integer)undeterminateAdvanceLengthSpinner.getValue());
		radialBusyIcon.setDelay((Integer)delaySpinner.getValue());
		// squareBusyIcon.setIcon(getSelectedBaseIcon());
		// lineBusyIcon.setIcon(getSelectedBaseIcon());

		obj.setIcon(getSelectedBusyIcon());
	}

	private void updateFormGroup(JRadioButton button) {
		formRadialButton.setSelected(formRadialButton == button);
		formSquareButton.setSelected(formSquareButton == button);
		formCamomileButton.setSelected(formCamomileButton == button);

		iconMonitorButton.setEnabled(formCamomileButton != button);
		iconBatteryButton.setEnabled(formCamomileButton != button);
		iconSearchButton.setEnabled(formCamomileButton != button);
		iconNoneButton.setEnabled(formCamomileButton != button);

		update();
	}

	private void updateIconGroup(JRadioButton button) {
		iconMonitorButton.setSelected(iconMonitorButton == button);
		iconBatteryButton.setSelected(iconBatteryButton == button);
		iconSearchButton.setSelected(iconSearchButton == button);
		iconNoneButton.setSelected(iconNoneButton == button);

		update();
	}

	// ========== ActionListener ==========

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == formRadialButton)
			updateFormGroup(formRadialButton);
		else if (event.getSource() == formSquareButton)
			updateFormGroup(formSquareButton);
		else if (event.getSource() == formCamomileButton)
			updateFormGroup(formCamomileButton);
		else if (event.getSource() == iconMonitorButton)
			updateIconGroup(iconMonitorButton);
		else if (event.getSource() == iconBatteryButton)
			updateIconGroup(iconBatteryButton);
		else if (event.getSource() == iconSearchButton)
			updateIconGroup(iconSearchButton);
		else if (event.getSource() == iconNoneButton)
			updateIconGroup(iconNoneButton);
		else if (event.getSource() == determinateButton)
			update();
		else if (event.getSource() == busyButton)
			update();
	}

	// ========== ChangeListener ==========

	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == slider)
			update();
		if (event.getSource() == undeterminateAdvanceLengthSpinner)
			update();
		if (event.getSource() == delaySpinner)
			update();
	}

	// ========== static ==========

	private static GridBagConstraints createPartConstraints(boolean last) {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = last ? GridBagConstraints.REMAINDER : 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1;

		return gbc;
	}

	private static GridBagConstraints createMainPartConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridheight = 2;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;

		return gbc;
	}

	private static GridBagConstraints createSliderPartConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;

		return gbc;
	}
}
