package shape.tos;

import java.awt.Color;
import java.awt.Event;

public final class ThinkOrSwimPaintModel implements PaintModel {
	public static final ThinkOrSwimPaintModel INCTANCE = new ThinkOrSwimPaintModel();

	private static final Color LIGHT_GREEN = new Color(0, 182, 37);
	private static final Color DARK_GREEN = new Color(21, 61, 34);

	private static final int X1 = 114;
	private static final int X2 = 163;
	private static final int X3 = 201;
	private static final int Y = 117;

	private ThinkOrSwimPaintModel() {}

	/*
	 * PaintModel
	 */

	@Override
	public void paint(Event e, int length) {
		e.gc.setBackground(LIGHT_GREEN);
		e.gc.fillRectangle(0, 0, length, e.height);

		if (length > X1) {
			e.gc.setBackground(DARK_GREEN);
			e.gc.fillRectangle(X1, 0, length - X1, e.height);
		}
		if (length > X2) {
			e.gc.setBackground(DARK_GREEN);
			e.gc.fillRectangle(X1, 0, length - X2, Y);

			e.gc.setBackground(LIGHT_GREEN);
			e.gc.fillRectangle(X2, Y, length - X2, e.height - Y);
		}
		if (length > X3) {
			e.gc.setBackground(DARK_GREEN);
			e.gc.fillRectangle(X3, 0, length - X3, e.height);
		}
	}
}
