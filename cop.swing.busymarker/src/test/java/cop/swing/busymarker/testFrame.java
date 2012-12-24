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
		 * Создаем простой фрейм. К нему добавляем панель с BorderLayout Верхнюю часть добавляем кнопку. В центральной
		 * области еще одна панель
		 */
		setTitle("test");
		setSize(500, 500);
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
		JButton but = new JButton("Изменить панель");
		lab = new JLabel("Это текст");
		/**
		 * При нажатии на кнопку я яхочу что бы содержимое центральной области mainPanel было заменено!
		 */
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Попытка заменить содержимое. Не работает и ошибок нету ни каких.
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
		// добавляем в центральную область первую панель
		current = getPanel1();
		mainPanel.add(current, BorderLayout.CENTER);
		add(mainPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	private static JPanel getPanel1() {
		// Возвращает панель которая отображается при запуске фрейма в центральной области.
		JPanel panel = new JPanel();
		panel.add(new JLabel("text"));
		return panel;
	}

	private static JPanel getPanel2() {
		// Возвращает панель которая должна отобразится в центральной области
		// после нажатия на кнопку
		JPanel panel = new JPanel();
		panel.add(new JButton("Это кнопка"));
		return panel;
	}

	private JPanel mainPanel;
	private JLabel lab;
	private JPanel current;

	public static void main(String[] args) {
		new testFrame().setVisible(true);
	}
}
