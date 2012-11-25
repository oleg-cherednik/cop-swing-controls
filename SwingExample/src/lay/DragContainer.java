package lay;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class DragContainer extends JPanel implements MouseMotionListener, MouseListener {
	public DragContainer() {
		// getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl C"), "Command");
		// getActionMap().put("Command", new MyActionListener("ctrl C"));
		//
		// addMouseMotionListener(this);
		// addMouseListener(this);
//		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

//	public void add(Component comp, Object constraints) {
//		super.add(comp, constraints);
//	}

//	protected void processMouseMotionEvent(MouseEvent evt) {
//		super.processMouseMotionEvent(evt);
//		System.out.println("DragContainer: processMouseMotionEvent");
//	}

	// public void actionPerformed(ActionEvent e) {
	// int a = 0;
	// a++;
	// }
	//
	// static class MyActionListener extends AbstractAction {
	// MyActionListener(String s) {
	// super(s);
	// }
	//
	// public void actionPerformed(ActionEvent e) {
	// System.out.println(getValue(Action.NAME));
	// }
	// }

	// ========== MouseMotionListener ==========

	public void mouseDragged(MouseEvent event) {
		System.out.println("DragContainer: mouseDragged");
	}

	public void mouseMoved(MouseEvent event) {
		System.out.println("DragContainer: mouseMoved");
	}

	// ========== MouseListener ==========

	public void mouseClicked(MouseEvent e) {
		System.out.println("DragContainer: mouseClicked");
	}

	public void mousePressed(MouseEvent e) {
		System.out.println("DragContainer: mousePressed");
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("DragContainer: mouseReleased");
	}

	public void mouseEntered(MouseEvent e) {
		System.out.println("DragContainer: mouseEntered");
	}

	public void mouseExited(MouseEvent e) {
		System.out.println("DragContainer: mouseExited");
	}

}
