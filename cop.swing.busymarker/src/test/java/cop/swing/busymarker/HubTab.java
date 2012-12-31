package cop.swing.busymarker;

import java.awt.Color;
import java.awt.LayoutManager;
import java.text.DecimalFormat;

import javax.swing.BoundedRangeModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cop.swing.busymarker.icons.BusyIcon;
import cop.swing.busymarker.icons.RadialBusyIcon;
import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.models.BusyModelHub;
import cop.swing.busymarker.models.DefaultBusyModel;

class HubTab extends JPanel implements ChangeListener {
	private static final long serialVersionUID = -7701777042658548414L;

	public static final String TITLE = "Hub";
	private static final Icon iconJava = new ImageIcon(MainDemo.class.getResource("java-icon.png"));

	private final JLabel jLabelHub = new JLabel();
	private final JLabel jLabel3 = new JLabel("#1");
	private final JLabel jLabel5 = new JLabel("#2");
	private final JLabel jLabel7 = new JLabel("#3");

	private final JSpinner jSpinnerModel1 = new JSpinner();
	private final JSpinner jSpinnerModel2 = new JSpinner();
	private final JSpinner jSpinnerModel3 = new JSpinner();

	private final JSlider jSliderModel1 = new JSlider();
	private final JSlider jSliderModel2 = new JSlider();
	private final JSlider jSliderModel3 = new JSlider();

	private final JLabel jLabelHelp1 = new JLabel(" = xxx %");
	private final JLabel jLabelHelp2 = new JLabel(" = xxx %");
	private final JLabel jLabelHelp3 = new JLabel(" = xxx %");

	private final BusyModel busyModel = new DefaultBusyModel();
	private final BusyModelHub busyModelSet = new BusyModelHub(busyModel);
	private final BusyModel busyModel1 = busyModelSet.createModel(10);
	private final BusyModel busyModel2 = busyModelSet.createModel(10);
	private final BusyModel busymodel3 = busyModelSet.createModel(10);

	private final BusyIcon iconHub = new RadialBusyIcon(iconJava);

	HubTab() {
		init();
		addListeners();
	}

	private void init() {
		setLayout(createMainLayout());

		busyModel.setDeterminate(true);
		busyModel.setBusy(true);

		busyModel.setMinimum(0);
		busyModel.setMaximum(5000);
		iconHub.setModel(busyModel);

		busyModel1.setMinimum(jSliderModel1.getMinimum());
		busyModel2.setMinimum(jSliderModel2.getMinimum());
		busymodel3.setMinimum(jSliderModel3.getMinimum());

		busyModel1.setMaximum(jSliderModel1.getMaximum());
		busyModel2.setMaximum(jSliderModel2.getMaximum());
		busymodel3.setMaximum(jSliderModel3.getMaximum());

		jSpinnerModel1.setValue(40);
		jSpinnerModel2.setValue(60);
		jSpinnerModel3.setValue(20);

		jLabelHub.setIcon(iconHub);
		jLabelHub.setBackground(Color.white);
		jLabelHub.setHorizontalAlignment(SwingConstants.CENTER);
		jLabelHub.setText("<html>a <code>BoundedRangeModelHub</code> compute a <b>master</b> model from any"
				+ " changes of sub-models.<p>Each sub-models have a <b>weight</b> that determine theses"
				+ " proportions of the master model.<br>Theses models are completely <b>independents</b>"
				+ " from each others.</html>");
		jLabelHub.setOpaque(true);

		jLabelHelp1.setForeground(Color.blue);
		jLabelHelp2.setForeground(Color.blue);
		jLabelHelp3.setForeground(Color.blue);

		updateHubModels();
	}

	private void addListeners() {
		jSliderModel1.addChangeListener(this);
		jSliderModel2.addChangeListener(this);
		jSliderModel3.addChangeListener(this);

		jSpinnerModel1.addChangeListener(this);
		jSpinnerModel2.addChangeListener(this);
		jSpinnerModel3.addChangeListener(this);
	}

	private LayoutManager createMainLayout() {
		GroupLayout layout = new GroupLayout(this);
		JPanel panel1 = createPanel1();
		JPanel panel2 = createPanel2();
		JPanel panel3 = createPanel3();

		layout.setHorizontalGroup(createMainHorizontalGroup(layout, panel1, panel2, panel3));
		layout.setVerticalGroup(createMainVerticalGroup(layout, panel1, panel2, panel3));

		return layout;
	}

	private JPanel createPanel1() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);

		layout.setHorizontalGroup(createHorizontalGroup1(layout));
		layout.setVerticalGroup(createVerticalGroup1(layout));

		panel.setLayout(layout);
		panel.setBackground(Color.white);
		panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
		panel.setOpaque(false);

		return panel;
	}

	private JPanel createPanel2() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);

		layout.setHorizontalGroup(createHorizontalGroup2(layout));
		layout.setVerticalGroup(createVerticalGroup2(layout));

		panel.setLayout(layout);
		panel.setBackground(Color.white);
		panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
		panel.setOpaque(false);

		return panel;
	}

	private JPanel createPanel3() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);

		layout.setHorizontalGroup(createHorizontalGroup3(layout));
		layout.setVerticalGroup(createVerticalGroup3(layout));

		panel.setLayout(layout);
		panel.setBackground(Color.white);
		panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
		panel.setOpaque(false);

		return panel;
	}

	private Group createGroup1(GroupLayout layout, JPanel panel1, JPanel panel2, JPanel panel3) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		group.addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		group.addComponent(jLabelHub, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE);
		group.addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

		return group;
	}

	private Group createGroup2(GroupLayout layout, JPanel panel1, JPanel panel2, JPanel panel3) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addContainerGap();
		group.addGroup(createGroup1(layout, panel1, panel2, panel3));
		group.addContainerGap();

		return group;
	}

	private Group createGroup3(GroupLayout layout, JPanel panel1, JPanel panel2, JPanel panel3) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addContainerGap();
		group.addComponent(jLabelHub, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		group.addContainerGap(173, Short.MAX_VALUE);

		return group;
	}

	private Group createGroup4(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addContainerGap();
		group.addComponent(jLabel7, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addComponent(jSpinnerModel3, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addComponent(jSliderModel3, GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addComponent(jLabelHelp3, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE);

		return group;
	}

	private Group createGroup5(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		group.addComponent(jLabel7, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE);
		group.addComponent(jSpinnerModel3, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE);
		group.addComponent(jLabelHelp3);

		return group;
	}

	private Group createGroup6(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addGap(11, 11, 11);
		group.addGroup(createGroup5(layout));

		return group;
	}

	private Group createGroup7(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addContainerGap();
		group.addComponent(jSliderModel3, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE);

		return group;
	}

	private Group createGroup8(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);

		group.addGroup(GroupLayout.Alignment.LEADING, createGroup7(layout));
		group.addGroup(createGroup6(layout));

		return group;
	}

	private Group createGroup9(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addGroup(createGroup8(layout));
		group.addGap(11, 11, 11);

		return group;
	}

	private Group createGroup10(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addComponent(jSliderModel1, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE);
		group.addContainerGap(203, Short.MAX_VALUE);

		return group;
	}

	private Group createGroup11(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);

		group.addGroup(createGroup10(layout));
		group.addComponent(jLabelHelp1, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE);

		return group;
	}

	private Group createGroup12(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addContainerGap();
		group.addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addComponent(jSpinnerModel1, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addGroup(createGroup11(layout));

		return group;
	}

	private Group createGroup13(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		group.addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE);
		group.addComponent(jSpinnerModel1, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE);
		group.addComponent(jLabelHelp1);

		return group;
	}

	private Group createGroup14(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addGap(11, 11, 11);
		group.addGroup(createGroup13(layout));

		return group;
	}

	private Group createGroup15(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addContainerGap();
		group.addComponent(jSliderModel1, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE);

		return group;
	}

	private Group createGroup16(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);

		group.addGroup(GroupLayout.Alignment.LEADING, createGroup15(layout));
		group.addGroup(createGroup14(layout));

		return group;
	}

	private Group createGroup17(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addGroup(createGroup16(layout));
		group.addContainerGap();

		return group;
	}

	private Group createGroup18(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addContainerGap();
		group.addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addComponent(jSpinnerModel2, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addComponent(jSliderModel2, GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE);
		group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		group.addComponent(jLabelHelp2, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE);

		return group;
	}

	private Group createGroup19(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		group.addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE);
		group.addComponent(jSpinnerModel2, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE);
		group.addComponent(jLabelHelp2);

		return group;
	}

	private Group createGroup20(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addGap(11, 11, 11);
		group.addGroup(createGroup19(layout));

		return group;
	}

	private Group createGroup21(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addContainerGap();
		group.addComponent(jSliderModel2, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE);

		return group;
	}

	private Group createGroup22(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		group.addGroup(GroupLayout.Alignment.LEADING, createGroup21(layout));
		group.addGroup(createGroup20(layout));

		return group;
	}

	private Group createGroup23(GroupLayout layout) {
		SequentialGroup group = layout.createSequentialGroup();

		group.addGroup(createGroup22(layout));
		group.addContainerGap();

		return group;
	}

	private Group createMainHorizontalGroup(GroupLayout layout, JPanel panel1, JPanel panel2, JPanel panel3) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addGroup(createGroup2(layout, panel1, panel2, panel3));

		return group;
	}

	private Group createHorizontalGroup1(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addGroup(createGroup12(layout));

		return group;
	}

	private Group createVerticalGroup1(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addGroup(GroupLayout.Alignment.TRAILING, createGroup17(layout));

		return group;
	}

	private Group createHorizontalGroup2(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addGroup(createGroup18(layout));

		return group;
	}

	private Group createVerticalGroup2(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addGroup(GroupLayout.Alignment.TRAILING, createGroup23(layout));

		return group;
	}

	private Group createHorizontalGroup3(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addGroup(createGroup4(layout));

		return group;
	}

	private Group createVerticalGroup3(GroupLayout layout) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addGroup(GroupLayout.Alignment.TRAILING, createGroup9(layout));

		return group;
	}

	private Group createMainVerticalGroup(GroupLayout layout, JPanel panel1, JPanel panel2, JPanel panel3) {
		ParallelGroup group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		group.addGroup(createGroup3(layout, panel1, panel2, panel3));

		return group;
	}

	private void updateHubModels() {
		busyModelSet.setWeight(busyModel1, (Integer)jSpinnerModel1.getValue());
		busyModelSet.setWeight(busyModel2, (Integer)jSpinnerModel2.getValue());
		busyModelSet.setWeight(busymodel3, (Integer)jSpinnerModel3.getValue());

		busyModel1.setValue(jSliderModel1.getValue());
		busyModel2.setValue(jSliderModel2.getValue());
		busymodel3.setValue(jSliderModel3.getValue());

		updateHubHelps(busyModel1, jLabelHelp1);
		updateHubHelps(busyModel2, jLabelHelp2);
		updateHubHelps(busymodel3, jLabelHelp3);
	}

	private void updateHubHelps(BusyModel subModel, JLabel component) {
		DecimalFormat df = new DecimalFormat("#00.00");
		float prc_current = (float)subModel.getValue() / (float)subModel.getRange() * 100f;
		float prc_total = (float)this.busyModelSet.getWeight(subModel) / this.busyModelSet.getWeight() * 100f;
		component.setText(df.format(prc_current) + " % of " + df.format(prc_total) + " %");
	}
	
	// ========== ChangeListener ==========

	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == jSliderModel3)
			updateHubModels();
		else if (event.getSource() == jSpinnerModel3)
			updateHubModels();
		else if (event.getSource() == jSliderModel1)
			updateHubModels();
		else if (event.getSource() == jSpinnerModel1)
			updateHubModels();
		else if (event.getSource() == jSliderModel2)
			updateHubModels();
		else if (event.getSource() == jSpinnerModel2)
			updateHubModels();
	}
}
