package lay;

import static java.awt.AWTEvent.KEY_EVENT_MASK;
import static java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JRootPane;

public class SectionViewer extends JRootPane implements MouseMotionListener, MouseListener, AWTEventListener {
	private static Toolkit toolkit = Toolkit.getDefaultToolkit();
	private static long eventMask = MOUSE_MOTION_EVENT_MASK /* | MOUSE_EVENT_MASK */| KEY_EVENT_MASK;

	private final GlassPane glassPane = new GlassPane();

	private boolean draggable = true;

	public SectionViewer() {
		getRootPane().setGlassPane(glassPane);
		// addMouseMotionListener(this);
		// addMouseListener(this);

		//
		// glassPane.addMouseMotionListener(new MouseMotionListener() {
		//
		// public void mouseMoved(MouseEvent e) {
		// System.out.println("glass: mouseMoved - " + e.getModifiers() + " - " + e.getModifiersEx());
		//
		// }
		//
		// public void mouseDragged(MouseEvent e) {
		// System.out.println("glass: mouseDragged");
		//
		// }
		// });

		toolkit.addAWTEventListener(this, eventMask);
	}

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

	// ========== AWTEventListener ==========

	public void eventDispatched(AWTEvent event) {
		if (!isEventEnabled(event))
			return;

		if (event instanceof MouseEvent)
			onMouseEvent((MouseEvent)event);
		else if (event instanceof KeyEvent)
			onKeyEvent((KeyEvent)event);
	}

	private Section prvSection;
	private Section mousePosition;

	private void onMouseEvent(MouseEvent event) {
		mousePosition = getSection(getRelativePoint(event));

		if (!event.isControlDown()) {
			if (prvSection != null)
				prvSection.setSelected(false);
			prvSection = null;
			return;
		}

		if (mousePosition == prvSection)
			return;
		if (prvSection != null)
			prvSection.setSelected(false);
		if (mousePosition != null)
			mousePosition.setSelected(true);
		prvSection = mousePosition;

		// if (section != null)
		// section.onMouseEvent(event);
	}

	public void setGlassPaneVisible(boolean visible) {
		glassPane.setVisible(visible);
	}

	private final Point point = new Point(0, 0);

	private Point getRelativePoint(MouseEvent event) {
		Point viewerLocation = getLocationOnScreen();

		point.x = event.getXOnScreen() - viewerLocation.x;
		point.y = event.getYOnScreen() - viewerLocation.y;

		return point;
	}

	private void onKeyEvent(KeyEvent event) {
		if (!event.isControlDown()) {
			if (prvSection != null)
				prvSection.setSelected(false);
			prvSection = null;
			return;
		}

		if (mousePosition != null) {
			mousePosition.setSelected(true);
			prvSection = mousePosition;

		}
	}

	private boolean isEventEnabled(AWTEvent event) {
		if (!draggable)
			return false;

		return true;
	}

	private Section getSection(Point point) {
		for (Component component : getComponents())
			if (component instanceof Section && component.getBounds().contains(point))
				return (Section)component;

		return null;
	}

}
