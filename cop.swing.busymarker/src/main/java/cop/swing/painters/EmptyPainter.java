package cop.swing.painters;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Paint;

/**
 * @author Oleg Cherednik
 * @since 27.03.2012
 */
public final class EmptyPainter implements LayoutPainter<Component> {
	private static final Painter<Component> INSTANCE = new EmptyPainter();

	private EmptyPainter() {}

	@SuppressWarnings("unchecked")
	public static final <T extends Component> LayoutPainter<T> getInstance() {
		return (LayoutPainter<T>)INSTANCE;
	}

	// ========== Painter ==========

	public void paint(Graphics2D g, Component obj, int width, int height) {}

	// ========== LayoutPainer ==========

	public Paint getFillPaint() {
		return null;
	}

	public void setFillPaint(Paint paint) {}
}
