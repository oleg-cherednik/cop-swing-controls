package cop.swing.painters;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import cop.swing.painters.enums.FillStyle;
import cop.swing.utils.GraphicsUtils;

/**
 * A painter which paints square and rounded rectangles
 * 
 * @author joshua.marinacci@sun.com
 */
public class RectanglePainter<T extends Component> extends AbstractAreaPainter<T> {
	private boolean rounded;
	private int roundWidth = 20;
	private int roundHeight = 20;
	private int width;
	private int height;

	public RectanglePainter() {
		this(0, 0);
	}

	public RectanglePainter(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Indicates if the rectangle is rounded
	 * 
	 * @return if the rectangle is rounded
	 */
	public final boolean isRounded() {
		return rounded;
	}

	/**
	 * sets if the rectangle should be rounded
	 * 
	 * @param rounded if the rectangle should be rounded
	 */
	public void setRounded(boolean rounded) {
		if (this.rounded == rounded)
			return;

		this.rounded = rounded;
		setDirty(true);
	}

	/**
	 * gets the round width of the rectangle
	 * 
	 * @return the current round width
	 */
	public final int getRoundWidth() {
		return roundWidth;
	}

	/**
	 * sets the round width of the rectangle
	 * 
	 * @param roundWidth a new round width
	 */
	public void setRoundWidth(int roundWidth) {
		if (this.roundWidth == roundWidth)
			return;

		this.roundWidth = roundWidth;
		setDirty(true);
	}

	/**
	 * gets the round height of the rectangle
	 * 
	 * @return the current round height
	 */
	public final int getRoundHeight() {
		return roundHeight;
	}

	/**
	 * sets the round height of the rectangle
	 * 
	 * @param roundHeight a new round height
	 */
	public void setRoundHeight(int roundHeight) {
		if (this.roundHeight == roundHeight)
			return;

		this.roundHeight = roundHeight;
		setDirty(true);
	}

	protected RectangularShape getShape(int width, int height) {
		int x = insets.left;
		int y = insets.top;

		Rectangle bounds = calculateLayout(this.width, this.height, width, height);

		if (this.width != -1 && !isFillHorizontal()) {
			width = this.width;
			x = bounds.x;
		}

		if (this.height != -1 && !isFillVertical()) {
			height = this.height;
			y = bounds.y;
		}

		if (isFillHorizontal())
			width = width - insets.left - insets.right;
		if (isFillVertical())
			height = height - insets.top - insets.bottom;

		return createRectangularShape(x, y, width, height);
	}

	private RectangularShape createRectangularShape(double x, double y, double width, double height) {
		if (rounded)
			return new RoundRectangle2D.Double(x, y, width, height, roundWidth, roundHeight);
		return new RoundRectangle2D.Double(x, y, width, height, roundWidth, roundHeight);
	}

	private void drawBorder(Graphics2D g2d, RectangularShape shape, int width, int height) {
		Paint paint = getBorderPaint();

		if (isPaintStretched())
			paint = calculateSnappedPaint(paint, width, height);

		g2d.setPaint(paint);
		g2d.setStroke(getBorderStroke());
		g2d.draw(modifyShape(shape));
	}

	private void drawBackground(Graphics2D g2d, Shape shape, int width, int height) {
		Paint paint = getFillPaint();

		if (isPaintStretched())
			paint = calculateSnappedPaint(paint, width, height);

		g2d.setPaint(paint);
		g2d.fill(shape);
	}

	/*
	 * AbstractAreaPainter
	 */

	@Override
	protected void doPaint(Graphics2D g2d, T obj, int width, int height) {
		RectangularShape shape = getShape(width, height);

		if (getStyle() == FillStyle.BOTH || getStyle() == FillStyle.FILLED)
			drawBackground(g2d, shape, width, height);
		if (getStyle() == FillStyle.BOTH || getStyle() == FillStyle.OUTLINE)
			drawBorder(g2d, shape, width, height);

		GraphicsUtils.mergeClip(g2d, shape);
	}

	@Override
	public Shape provideShape(Graphics2D g, T comp, int width, int height) {
		return getShape(width, height);
	}

	/*
	 * static
	 */

	private static RectangularShape modifyShape(RectangularShape shape) {
		if (shape instanceof Rectangle2D)
			return getRectangle2DShape((Rectangle2D)shape);
		if (shape instanceof RoundRectangle2D)
			return getRoundRectangle2D((RoundRectangle2D)shape);
		return shape;
	}

	private static RectangularShape getRectangle2DShape(Rectangle2D shape) {
		return new Rectangle2D.Double(shape.getX(), shape.getY(), shape.getWidth() - 1, shape.getHeight() - 1);
	}

	private static RectangularShape getRoundRectangle2D(RoundRectangle2D shape) {
		double width = shape.getWidth() - 1;
		double height = shape.getHeight() - 1;
		double arcw = shape.getArcWidth();
		double arch = shape.getArcHeight();

		return new RoundRectangle2D.Double(shape.getX(), shape.getY(), width, height, arcw, arch);
	}
}
