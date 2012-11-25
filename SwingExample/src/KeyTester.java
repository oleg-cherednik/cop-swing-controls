import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

public class KeyTester {
	static class MyActionListener extends AbstractAction {
		MyActionListener(String s) {
			super(s);
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println(getValue(Action.NAME));
		}
	}

	public static void main(String args[]) {
		String actionKey = "theAction";
		JFrame f = new JFrame("Key Tester");
		JButton jb1 = new JButton("<html><center>B<br>Focused/Typed");
		JButton jb2 = new JButton("<html><center>Ctrl-C<br>Window/Pressed");
		JButton jb3 = new JButton("<html><center>Shift-D<br>Ancestor/Released");
		Container pane = f.getContentPane();
		pane.add(jb1, BorderLayout.NORTH);
		pane.add(jb2, BorderLayout.CENTER);
		pane.add(jb3, BorderLayout.SOUTH);

		KeyStroke stroke = KeyStroke.getKeyStroke("typed B");
		Action action = new MyActionListener("Action Happened");
		// Defaults to JComponent.WHEN_FOCUSED map
		InputMap inputMap = jb1.getInputMap();
		inputMap.put(stroke, actionKey);
		ActionMap actionMap = jb1.getActionMap();
		actionMap.put(actionKey, action);

		stroke = KeyStroke.getKeyStroke("ctrl C");
		action = new MyActionListener("Action Didn't Happen");
		inputMap = jb2.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, actionKey);
		actionMap = jb2.getActionMap();
		actionMap.put(actionKey, action);

		stroke = KeyStroke.getKeyStroke("shift released D");
		action = new MyActionListener("What Happened?");
		inputMap = jb3.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(stroke, actionKey);
		actionMap = jb3.getActionMap();
		actionMap.put(actionKey, action);

		f.setSize(200, 200);
		f.show();
	}
}
