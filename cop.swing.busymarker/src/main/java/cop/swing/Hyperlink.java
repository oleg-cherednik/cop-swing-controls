/**
 * <b>License</b>: <a href="http://www.gnu.org/licenses/lgpl.html">GNU Leser General Public License</a>
 * <b>Copyright</b>: <a href="mailto:abba-best@mail.ru">Oleg Cherednik</a>
 *
 * $Id$
 * $HeadURL$
 */
package cop.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * @author Oleg Cherednik
 * @since 29.02.2012
 */
public class Hyperlink extends JLabel implements MouseListener {
	private static final long serialVersionUID = -4224135897275414253L;

	private static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	private static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();

	private String actionCommand;
	private String text;
	private String selectedText;

	public Hyperlink() {
		this("");
	}

	public Hyperlink(String text) {
		addMouseListener(this);

		setHorizontalAlignment(SwingConstants.CENTER);
		setHorizontalTextPosition(SwingConstants.CENTER);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setForeground(Color.blue);
		setText(text);
	}

	public synchronized void addActionListener(ActionListener listener) {
		if (listener != null)
			listenerList.add(ActionListener.class, listener);
	}

	public synchronized void removeActionListener(ActionListener listener) {
		if (listener != null)
			listenerList.remove(ActionListener.class, listener);
	}

	private ActionEvent createActionEvent(MouseEvent event) {
		int id = ActionEvent.ACTION_PERFORMED;
		String command = getActionCommand();
		return new ActionEvent(this, id, command, event.getWhen(), event.getModifiers());
	}

	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}

	public String getActionCommand() {
		return (actionCommand != null) ? actionCommand : text;
	}

	// ========== JLabel ==========

	@Override
	public final void setText(String text) {
		if (text == null)
			text = "";

		super.setText(text);

		this.text = text;
		this.selectedText = getSelectedText(text);
	}

	// ========== MouseListener ==========

	public void mouseClicked(MouseEvent event) {
		ActionEvent action = createActionEvent(event);

		for (ActionListener listener : listenerList.getListeners(ActionListener.class))
			listener.actionPerformed(action);
	}

	public void mousePressed(MouseEvent event) {}

	public void mouseReleased(MouseEvent event) {}

	public void mouseEntered(MouseEvent event) {
		super.setText(selectedText);
		setCursor(HAND_CURSOR);
	}

	public void mouseExited(MouseEvent event) {
		super.setText(text);
		setCursor(DEFAULT_CURSOR);
	}

	// ========== static ==========

	private static String getSelectedText(String text) {
		return text == null || text.length() == 0 ? text : ("<html><u>" + text + "</u></html>");
	}
}
