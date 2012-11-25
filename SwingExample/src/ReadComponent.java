import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ReadComponent {
	private boolean SELECT_MODE = true;
	private JLabel statusBar;
	private TextComponent tc = null;

	public static void main(String[] args) {
		ReadComponent rc = new ReadComponent();
	}

	public ReadComponent() {
		// Create and set up the window.
		JFrame frame = new JFrame("ReadComponent");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = frame.getContentPane();
		statusBar = new JLabel();
		tc = new TextComponent(2, 4, c);
		c.add(tc, BorderLayout.NORTH);
		c.add(statusBar, BorderLayout.SOUTH);
		MouseTracker mt = new MouseTracker();
		c.addMouseListener(mt);
		c.addMouseMotionListener(mt);
		frame.setSize(300, 200);
		frame.setVisible(true);
	}

	class MouseTracker implements MouseListener, MouseMotionListener {
		public void mouseClicked(MouseEvent e) {
			statusBar.setText("Clicked at [" + e.getX() + ", " + e.getY() + "]");
		}

		public void mouseEntered(MouseEvent e) {
			statusBar.setText("Entered at [" + e.getX() + ", " + e.getY() + "]");
		}

		public void mouseExited(MouseEvent e) {
			statusBar.setText("Exited at [" + e.getX() + ", " + e.getY() + "]");
		}

		public void mousePressed(MouseEvent e) {
			statusBar.setText("Pressed at [" + e.getX() + ", " + e.getY() + "]");
		}

		public void mouseReleased(MouseEvent e) {
			statusBar.setText("Released at [" + e.getX() + ", " + e.getY() + "]");
		}

		public void mouseDragged(MouseEvent e) {
			statusBar.setText("Dragged at [" + e.getX() + ", " + e.getY() + "]");
		}

		public void mouseMoved(MouseEvent e) {
			statusBar.setText("Moved at [" + e.getX() + ", " + e.getY() + "]");
		}
	}

	class TextComponent extends JTextArea {
		private Component parent;

		public TextComponent(int rows, int columns, Component parent) {
			super(rows, columns);
			this.parent = parent;
			enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
		}

		public void processMouseMotionEvent(MouseEvent evt) {
			if (SELECT_MODE) {
				parent.dispatchEvent(evt); // <<This isn't doing what I had hoped
				System.out.println("parent.getName(): " + parent.getName());
			} else {
				super.processMouseEvent(evt);
			}
		}
	}
}
