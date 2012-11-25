package lay;

import java.awt.AWTEvent;
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

import com.alee.gui.AWTUtilitiesWrapper;

public class Section extends JPanel implements FocusListener, MouseMotionListener, MouseListener {
	private final JTextArea text = new JTextArea("input");
	private final JLabel label = new JLabel("Drag me somewhere!", SwingConstants.CENTER);
	private final JButton button = new JButton("Some custom button");
	private final Color color;

	public Section(String name, Color color) {
		super(new BorderLayout(4, 4));

		setName(name);

		label.setText(name);
		init();

		setPreferredSize(new Dimension(200, 100));

		setBackground(this.color = color);
		setFocusable(true);

//		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);

//		addFocusListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
//		setFocusable(true);
	}

	private void init() {
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
				BorderFactory.createEmptyBorder(4, 4, 4, 4)));

		add(text, BorderLayout.NORTH);
		add(label, BorderLayout.CENTER);
		add(button, BorderLayout.SOUTH);
	}

	protected void processMouseMotionEvent(MouseEvent evt) {
		super.processMouseMotionEvent(evt);
		System.out.println(getName() + ": processMouseMotionEvent");
	}
	
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		System.out.println(getName() + ": processMouseEvent");
	}

	// ========== FocusListener ==========

	public void focusGained(FocusEvent event) {
		System.out.println(getName() + ": focusGained");
	}

	public void focusLost(FocusEvent event) {
		System.out.println(getName() + ": focusLost");
	}

	// ========== MouseMotionListener ==========

	public void mouseDragged(MouseEvent event) {
		System.out.println(getName() + ": mouseDragged");
	}

	public void mouseMoved(MouseEvent event) {
		System.out.println(getName() + ": mouseMoved");
	}

	// ========== MouseListener ==========

	public void mouseClicked(MouseEvent e) {
		System.out.println(getName() + ": mouseClicked");
	}

	public void mousePressed(MouseEvent event) {
		if(event.getSource() != this || !event.isControlDown())
			return;
		
		setBackground(Color.orange);
		System.out.println(getName() + ": mousePressed");
	}

	public void mouseReleased(MouseEvent event) {
		if(event.getSource() != this)
			return;
		
		setBackground(color);
		System.out.println(getName() + ": mouseReleased");
	}

	public void mouseEntered(MouseEvent e) {
		System.out.println(getName() + ": mouseEntered");
	}

	public void mouseExited(MouseEvent e) {
		System.out.println(getName() + ": mouseExited");
	}
}
