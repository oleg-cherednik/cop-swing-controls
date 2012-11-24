/**
 * <b>License</b>: <a href="http://www.gnu.org/licenses/lgpl.html">GNU Leser General Public License</a>
 * <b>Copyright</b>: <a href="mailto:abba-best@mail.ru">Oleg Cherednik</a>
 * 
 * $Id$
 * $HeadURL$
 */
package shape.tos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Oleg Cherednik
 * @since 08.03.2012
 */
public class RegionShell extends Shell implements Listener {
	private int x;
	private int y;

	public RegionShell(Display display, Region region, int width, int height) {
		super(display, SWT.NO_TRIM);

		super.setRegion(region);
		super.setSize(width, height);

		addListeners();
	}

	private void addListeners() {
		addListener(SWT.KeyDown, this);
		addListener(SWT.MouseDown, this);
		addListener(SWT.MouseMove, this);
	}

	protected void onMouseMove(int x, int y) {
		Point point = toDisplay(x, y);

		point.x -= this.x;
		point.y -= this.y;

		setLocation(point);
	}

	protected void onMouseDown(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/*
	 * Control
	 */

	@Override
	@Deprecated
	@SuppressWarnings("unused")
	public void setSize(int width, int height) {}

	@Override
	@SuppressWarnings("unused")
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int border = getBorderWidth();
		Rectangle bounds = getBounds();
		return new Point(bounds.width + border * 2, bounds.height + border * 2);
	}

	/*
	 * Shell
	 */

	@Override
	@Deprecated
	@SuppressWarnings("unused")
	public void setRegion(Region region) {}

	/*
	 * Widget
	 */

	@Override
	public void dispose() {
		getRegion().dispose();
		super.dispose();
	}

	/*
	 * Decorations
	 */

	@Override
	protected void checkSubclass() {}

	/*
	 * Listener
	 */

	public void handleEvent(Event e) {
		if (e.type == SWT.KeyDown && e.character == SWT.ESC)
			dispose();
		else if (e.type == SWT.MouseDown && e.button == 1)
			onMouseDown(e.x, e.y);
		else if (e.type == SWT.MouseMove && (e.stateMask & SWT.BUTTON1) != 0)
			onMouseMove(e.x, e.y);
	}
}
