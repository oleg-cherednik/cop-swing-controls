import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class Main extends JFrame {
	JPanel glass = new JPanel(new GridLayout(0, 1));
	JLabel padding = new JLabel();
	JProgressBar waiter = new JProgressBar(0, 100);

	public Main() {
		setSize(300, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel controlPane = new JPanel(new GridLayout(2, 1));
		controlPane.setOpaque(false);
		controlPane.add(new JLabel("Please wait..."));
		controlPane.add(waiter);
		glass.setOpaque(false);
		glass.add(padding);
		glass.add(new JLabel());
		glass.add(controlPane);
		glass.add(new JLabel());
		glass.add(new JLabel());
		glass.setSize(new Dimension(300, 300));

		setGlassPane(glass);

		JButton startB = new JButton("Start!");

		startB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent A) {
				glass.setVisible(true);
				padding.requestFocus();
			}
		});
		Container contentPane = getContentPane();
		contentPane.add(startB, BorderLayout.SOUTH);
	}

	public static void main(String[] args) {
		Main ge = new Main();
		ge.setVisible(true);
	}
}
