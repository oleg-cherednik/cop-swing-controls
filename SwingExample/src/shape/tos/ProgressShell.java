/**
 * <b>License</b>: <a href="http://www.gnu.org/licenses/lgpl.html">GNU Leser General Public License</a>
 * <b>Copyright</b>: <a href="mailto:abba-best@mail.ru">Oleg Cherednik</a>
 * 
 * $Id$
 * $HeadURL$
 */
package shape.tos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * @author Oleg Cherednik
 * @since 08.03.2012
 */
public class ProgressShell extends ImageShell implements PaintModel {
	private static final Color RED = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	private static final Color YELLOW = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);

	private Integer selection;
	private PaintModel paintModel = this;

	public ProgressShell(Display display, Region region, int width, int height) {
		super(display, region, width, height);
	}

	public ProgressShell(Display display, ImageData imageData) {
		super(display, imageData);
	}

	public void setSelection(Integer selection) {
		if (this.selection == selection)
			return;

		this.selection = selection;
		redraw();
	}

	public void setPaintModel(PaintModel paintModel) {
		if (this.paintModel != paintModel) {
			this.paintModel = (paintModel != null) ? paintModel : this;
			redraw();
		}
	}

	/*
	 * ImageShell
	 */

	@Override
	protected void onPaint(Event e) {
		if (selection == null)
			super.onPaint(e);
		else {
			Color foreground = e.gc.getForeground();
			Color background = e.gc.getBackground();

			paintModel.paint(e, getLength(selection, e.width));

			e.gc.setForeground(foreground);
			e.gc.setBackground(background);
		}
	}

	/*
	 * PaintModel
	 */

	public void paint(Event e, int length) {
		e.gc.setForeground(RED);
		e.gc.setBackground(YELLOW);

		e.gc.fillGradientRectangle(0, 0, length, e.height, false);
		// e.gc.fillRectangle(0, 0, length, height);
	}

	/*
	 * static
	 */

	private static int getLength(int selection, int width) {
		if (selection <= 0)
			return 0;
		if (selection >= 100)
			return width;
		return (width * selection) / 100;
	}
}
