package lay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class Section extends JPanel implements MouseMotionListener, MouseListener {
	private final JTextArea text = new JTextArea("input");
	private final JLabel label = new JLabel("Drag me somewhere!", SwingConstants.CENTER);
	private final JButton button = new JButton("Some custom button");
	private final Color color;
	private final Border redBorder = BorderFactory.createLineBorder(Color.red, 3);
	private final Border grayBorder = BorderFactory.createLineBorder(Color.gray, 1);
	private boolean selected;
	private final SectionViewer viewer;

	public Section(SectionViewer viewer, String name, Color color) {
		super(new BorderLayout(4, 4));
		
		this.viewer = viewer;

		setName(name);

		label.setText(name);
		init();

		setPreferredSize(new Dimension(200, 100));
		setBackground(this.color = color);
		// addMouseMotionListener(this);
		addMouseListener(this);

	}

	private void init() {
		setBorder(grayBorder);

		add(text, BorderLayout.NORTH);
		add(label, BorderLayout.CENTER);
		add(button, BorderLayout.SOUTH);
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		setBorder(selected ? redBorder : grayBorder);
	}

	public void onMouseEvent(MouseEvent event) {
		System.out.println(this + " - onMouseEvent");
	}

	// public void onKeyEvent(KeyEvent event) {
	//
	// }

	// ========== MouseMotionListener ==========

	public void mouseDragged(MouseEvent event) {
		System.out.println(getName() + ": mouseDragged");
	}

	public void mouseMoved(MouseEvent event) {
		System.out.println(getName() + ": mouseMoved");
	}

	// ========== MouseListener ==========

	public void mouseClicked(MouseEvent e) {
		// System.out.println(getName() + ": mouseClicked");
	}

	public void mousePressed(MouseEvent event) {
		if (selected) {
			viewer.setGlassPaneVisible(false);
			setBackground(Color.orange);
		}
		// System.out.println(getName() + ": mousePressed");
	}

	public void mouseReleased(MouseEvent event) {
		// if (event.getSource() != this)
		// return;

		setBackground(color);
		viewer.setGlassPaneVisible(true);

		// System.out.println(getName() + ": mouseReleased");
	}

	public void mouseEntered(MouseEvent e) {
		// System.out.println(getName() + ": mouseEntered");
	}

	public void mouseExited(MouseEvent e) {
		// System.out.println(getName() + ": mouseExited");
	}

	// ========== Object ==========

	public String toString() {
		return getName();
	}
}
