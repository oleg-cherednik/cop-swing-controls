package cop.swing.busymarker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class testFrame extends JFrame {
    private static final long serialVersionUID = 6509072918114493428L;

	public testFrame() {
		/*
		 * ������� ������� �����. � ���� ��������� ������ � BorderLayout ������� ����� ��������� ������. � �����������
		 * ������� ��� ���� ������
		 */
		setTitle("test");
		setSize(500, 500);
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
		JButton but = new JButton("�������� ������");
		lab = new JLabel("��� �����");
		/**
		 * ��� ������� �� ������ � ����� ��� �� ���������� ����������� ������� mainPanel ���� ��������!
		 */
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ������� �������� ����������. �� �������� � ������ ���� �� �����.
				if (current != null)
					mainPanel.remove(current);
				current = getPanel2();
				mainPanel.add(current, BorderLayout.CENTER);
				mainPanel.revalidate();
			}
		});
		JPanel top = new JPanel();
		top.add(but);
		mainPanel.add(top, BorderLayout.NORTH);
		// ��������� � ����������� ������� ������ ������
		current = getPanel1();
		mainPanel.add(current, BorderLayout.CENTER);
		add(mainPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	private static JPanel getPanel1() {
		// ���������� ������ ������� ������������ ��� ������� ������ � ����������� �������.
		JPanel panel = new JPanel();
		panel.add(new JLabel("text"));
		return panel;
	}

	private static JPanel getPanel2() {
		// ���������� ������ ������� ������ ����������� � ����������� �������
		// ����� ������� �� ������
		JPanel panel = new JPanel();
		panel.add(new JButton("��� ������"));
		return panel;
	}

	private JPanel mainPanel;
	private JLabel lab;
	private JPanel current;

	public static void main(String[] args) {
		new testFrame().setVisible(true);
	}
}
