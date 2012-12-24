package cop.swing.painters;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * A Painter implementation that uses a Paint to fill the entire background area. For example, if I wanted to paint the
 * entire background of a panel green, I would:
 * 
 * <code>
 * panel.setBackgroundPainter(new MattePainter(Color.green));
 * </code>
 * <p>
 * Since it accepts a Paint, it is also possible to paint a texture or use other more exotic Paint implementations. To
 * paint a BufferedImage texture as the background:
 * 
 * <pre>
 * Rectangle2D anchor = new Rectangle2D.Double(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
 * TexturePaint paint = new TexturePaint(bufferedImage, anchor);
 * panel.setBackgroundPainter(new MattePainter(paint));
 * </pre>
 * 
 * If no paint is specified, then nothing is painted
 * <p>
 * 
 * @author rbair
 */
public class MattePainter<T extends Component> extends AbstractAreaPainter<T> {
	public MattePainter() {}

	/**
	 * Create a new MattePainter for the given Paint. This can be a GradientPaint (the gradient will not grow when the
	 * component becomes larger unless you use the paintStretched boolean property), TexturePaint, Color, or other Paint
	 * instance.
	 * 
	 * @param paint Paint to fill with
	 */
	public MattePainter(Paint paint) {
		super(paint);
	}

	/**
	 * Create a new MattePainter for the given Paint. This can be a GradientPaint (the gradient will not grow when the
	 * component becomes larger unless you use the paintStretched boolean property), TexturePaint, Color, or other Paint
	 * instance.
	 * 
	 * @param paint Paint to fill with
	 * @param paintStretched indicates if the paint should be stretched
	 */
	public MattePainter(Paint paint, boolean paintStretched) {
		super(paint);
		setPaintStretched(paintStretched);
	}

	/*
	 * AbstractPainter
	 */

	@Override
	protected void doPaint(Graphics2D g2d, T obj, int width, int height) {
		Paint paint = getFillPaint();

		if (paint == null)
			return;
		if (isPaintStretched())
			paint = calculateSnappedPaint(paint, width, height);

		g2d.setPaint(paint);
		g2d.fill(provideShape(g2d, obj, width, height));
	}

	/*
	 * AbstractAreaPainter
	 */

	@Override
	protected Shape provideShape(Graphics2D g2d, T obj, int width, int height) {
		return new Rectangle(0, 0, width, height);
	}

}
