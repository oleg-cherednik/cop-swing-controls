import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.ComboBoxEditor;
import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

import combo.TListModel;
import combo.TSmartComboBox;
import combo.interfaces.NameProvider;
import directorychooser.DirectoryChooser;

import java.awt.event.*;

public class ComboBoxes extends JApplet implements ActionListener
{
	private static final long serialVersionUID = 2962378292273674853L;

	private JTextField textField = new JTextField(15);

	private static final NameProvider<AccountType> nameProvider = new AccountTypeNameProvider();

	private final TSmartComboBox<AccountType> comboBox = new TSmartComboBox<AccountType>(nameProvider);
	private final JCheckBox item0 = new JCheckBox("0", true);
	private final JCheckBox item1 = new JCheckBox("1", true);
	private final JCheckBox item2 = new JCheckBox("2", true);
	private final JCheckBox item3 = new JCheckBox("3", true);
	private final JCheckBox item4 = new JCheckBox("cur", false);
	private final TListModel<AccountType> model = comboBox.getModel();

	public void init()
	{
		textField.setEditable(false);
		comboBox.setEmptyValueEnabled(true);

		// comboBox.addItem(AccountType.DEMO);
		// comboBox.addItem(AccountType.TDA_REAL);
		// comboBox.addItem(AccountType.DEMO);
		// comboBox.addItem(AccountType.TDA_REAL);
		// comboBox.addItem(AccountType.DEMO);
		// comboBox.addItem(AccountType.TDA_REAL);
		// comboBox.addItem(AccountType.DEMO);
		// comboBox.addItem(AccountType.TDA_REAL);

		// comboBox.setSelectedItem(AccountType.DEMO)
		item4.setEnabled(false);

		for(AccountType type : AccountType.values())
			comboBox.addItem(type);

		// comboBox.setItemEnabled(AccountType.TDA_REAL, false, "due to access restriction");

		// model.setItemEnabled(AccountType.DEMO, false, "due to access restriction");
		// model.setItemEnabled(AccountType.TDA_REAL, false, "available only for test accounts");

		comboBox.setSelectedItem(AccountType.DEMO);

		item0.addActionListener(this);
		item1.addActionListener(this);
		item2.addActionListener(this);
		item3.addActionListener(this);

		comboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				textField.setText(comboBox.getSelectedIndex() + ": " + comboBox.getSelectedItemName());
				// item4.setSelected(comboBox.isSelectedItemEnabled());
			};
		});

		Container cp = getContentPane();
		cp.setLayout(new FlowLayout());
		cp.add(new DirectoryChooser());
		cp.add(textField);
		cp.add(comboBox);
		cp.add(item0);
		cp.add(item1);
		cp.add(item2);
		cp.add(item3);
		cp.add(item4);
	}

	public static void main(String[] args)
	{
		run(new ComboBoxes(), 500, 100);
	}

	public static void run(JApplet applet, int width, int height)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(applet);
		frame.setSize(width, height);
		applet.init();
		applet.start();
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == comboBox)
			textField.setText(comboBox.getSelectedItemName());
		else if(event.getSource() == item0)
			comboBox.setItemEnabled(0, item0.isSelected());
		else if(event.getSource() == item1)
			comboBox.setItemEnabled(1, item1.isSelected());
		else if(event.getSource() == item2)
			comboBox.setItemEnabled(2, item2.isSelected());
		else if(event.getSource() == item3)
			comboBox.setItemEnabled(3, item3.isSelected());
	}
}
