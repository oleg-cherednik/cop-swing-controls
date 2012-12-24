package cop.swing.busymarker.icons;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

import cop.swing.painters.Painter;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public class PainterIcon<T extends Component> implements Icon {
	private final int width;
	private final int height;

	private Painter<T> painter;

	public PainterIcon(int width, int height, Painter<T> painter) {
		this.width = width;
		this.height = height;
		this.painter = painter;
	}

	public Painter<T> getPainter() {
		return painter;
	}

	public void setPainter(Painter<T> painter) {
		this.painter = painter;
	}

	/*
	 * Icon
	 */

	@SuppressWarnings("unchecked")
	public void paintIcon(Component obj, Graphics g, int x, int y) {
		if (painter == null || !(g instanceof Graphics2D))
			return;

		g = g.create();

		try {
			g.translate(x, y);
			painter.paint((Graphics2D)g, (T)obj, width, height);
			g.translate(-x, -y);
		} finally {
			g.dispose();
		}
	}

	public final int getIconWidth() {
		return width;
	}

	public final int getIconHeight() {
		return height;
	}
}
