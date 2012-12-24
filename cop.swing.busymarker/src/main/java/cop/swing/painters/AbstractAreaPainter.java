package cop.swing.painters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import cop.swing.painters.enums.FillStyle;
import cop.swing.utils.PaintUtils;

/**
 * The abstract base class for all painters that fill a vector path area. This includes Shapes, Rectangles, Text, and
 * the MattePainter which fills in the entire background of a component. The defining feature of AbstractAreaPainter
 * subclasses is that they implement the {@link #provideShape(Graphics2D, Component, int, int)} method which returns the
 * outline shape of the area that this painter will fill. Subclasses must implement the
 * {@link #provideShape(Graphics2D, Component, int, int)} method. The {@link AbstractAreaPainter} provides support for
 * the following common painting properties
 * <ul>
 * <li>fillPaint</li>
 * <li>paintStretched</li>
 * <li>borderPaint</li>
 * <li>borderWidth</li>
 * <li>style</li>
 * </ul>
 * The {@link AbstractAreaPainter} also provides support for path effects like dropshadows and glows.
 * 
 * @author joshua@marinacci.org
 */
public abstract class AbstractAreaPainter<T extends Component> extends AbstractLayoutPainter<T> {
	private static final FillStyle DEF_FILL_STYLE = FillStyle.BOTH;
	private static final Paint DEF_FILL_PAINT = Color.red;
	private static final float DEF_BORDER_WIDTH = 1;

	// controls if the paint should be stretched to fill the available area
	private boolean stretchPaint;

	private FillStyle fillStyle = DEF_FILL_STYLE;
	/**
	 * The stroke width to use when painting. If null, the default Stroke for the Graphics2D is used
	 */
	private float borderWidth = DEF_BORDER_WIDTH;

	private Stroke borderStroke = new BasicStroke(borderWidth);

	/**
	 * The paint to use when filling the shape
	 */
	private Paint fillPaint;

	/**
	 * The Paint to use when stroking the shape (drawing the outline). If null, then the component foreground color is
	 * used
	 */
	private Paint borderPaint;

	/**
	 * Creates a new instance of AbstractAreaPainter
	 */
	public AbstractAreaPainter() {
		this(DEF_FILL_PAINT);
	}

	/**
	 * Creates a new instance of AbstractAreaPainter
	 * 
	 * @param paint the default paint to fill this area painter with
	 */
	public AbstractAreaPainter(Paint paint) {
		this.fillPaint = (paint != null) ? paint : DEF_FILL_PAINT;
	}

	/**
	 * Indicates if the paint will be snapped. This means that the paint will be scaled and aligned along the 4 axis of
	 * (horizontal, vertical, and both diagonals). Snapping allows the paint to be stretched across the component when
	 * it is drawn, even if the component is resized. This setting is only used for gradient paints. It will have no
	 * effect on Color or Texture paints.
	 * 
	 * @return the current value of the snapPaint property
	 */
	public final boolean isPaintStretched() {
		return stretchPaint;
	}

	/**
	 * Specifies whether this Painter should attempt to resize the Paint to fit the area being painted. For example, if
	 * true, then a gradient specified as (0, 0), (1, 0) would stretch horizontally such that the beginning of the
	 * gradient is on the left edge of the painted region, and the end of the gradient is at the right edge of the
	 * painted region. Specifically, if true, the resizePaint method will be called to perform the actual resizing of
	 * the Paint
	 * 
	 * @param stretchPaint true if the paint should be stretched, false otherwise.
	 */
	public void setPaintStretched(boolean stretchPaint) {
		if (this.stretchPaint == stretchPaint)
			return;

		this.stretchPaint = stretchPaint;
		setDirty(true);
	}

	/**
	 * The Paint to use for stroking the shape (painting the outline). Can be a Color, GradientPaint, TexturePaint, or
	 * any other kind of Paint. If null, the component foreground is used.
	 * 
	 * @param borderPaint the Paint to use for stroking the shape. May be null.
	 */
	public void setBorderPaint(Paint borderPaint) {
		if (this.borderPaint == borderPaint || (this.borderPaint != null && this.borderPaint.equals(borderPaint)))
			return;

		this.borderPaint = borderPaint;
		setDirty(true);
	}

	/**
	 * Gets the current Paint to use for stroking the shape (painting the outline). Can be a Color, GradientPaint,
	 * TexturePaint, or any other kind of Paint. If null, the component foreground is used.
	 * 
	 * @return the Paint used when stroking the shape. May be null
	 */
	public final Paint getBorderPaint() {
		return borderPaint;
	}

	/**
	 * The shape can be filled or simply stroked (outlined), or both or none. By default, the shape is both filled and
	 * stroked. This property specifies the strategy to use.
	 * 
	 * @param fillStyle the Style to use. If null, Style.BOTH is used
	 */
	public void setStyle(FillStyle fillStyle) {
		if (fillStyle == null)
			fillStyle = DEF_FILL_STYLE;
		if (this.fillStyle == fillStyle)
			return;

		this.fillStyle = fillStyle;
		setDirty(true);
	}

	/**
	 * Gets the current Style. The shape can be filled or simply stroked (outlined), or both or none. By default, the
	 * shape is both filled and stroked. This property specifies the strategy to use.
	 * 
	 * @return the Style used
	 */
	public final FillStyle getStyle() {
		return fillStyle;
	}

	/**
	 * Sets the border width to use for painting. If null, then the default Graphics2D stroke will be used. The stroke
	 * will be centered on the actual shape outline.
	 * 
	 * @param borderWidth the Stroke to fillPaint with
	 */
	public void setBorderWidth(float borderWidth) {
		if (Float.compare(this.borderWidth, borderWidth) == 0)
			return;

		this.borderWidth = borderWidth;
		this.borderStroke = new BasicStroke(borderWidth);
		setDirty(true);
	}

	protected final Stroke getBorderStroke() {
		return borderStroke;
	}

	/**
	 * Gets the current border width.
	 * 
	 * @return the Stroke to use for painting
	 */
	public final float getBorderWidth() {
		return borderWidth;
	}

	/**
	 * Resizes the given Paint. By default, only Gradients, LinearGradients, and RadialGradients are resized in this
	 * method. If you have special resizing needs, override this method. This method is mainly used to make gradient
	 * paints resize with the component this painter is attached to. This method is internal to the painter api and
	 * should not be called elsewhere. It is used by the paintStretched property and painter subclasses. In the future
	 * it may be made public for use by other classes. If this happens it should probably be turned into a static
	 * utility method.
	 */
	@SuppressWarnings("static-method")
	public Paint calculateSnappedPaint(Paint paint, int width, int height) {
		return PaintUtils.resizeGradient(paint, width, height);
	}

	/**
	 * Returns the outline shape of this painter. Subclasses must implement this method. This shape will be used for
	 * filling, stroking, and clipping.
	 * 
	 * @return the outline shape of this painter
	 * @param g graphics
	 * @param comp The Object this painter will be painted on.
	 * @param width the width to paint
	 * @param height the height to paint
	 */
	protected abstract Shape provideShape(Graphics2D g, T comp, int width, int height);

	/*
	 * LayoutPainter
	 */

	/**
	 * Gets the current fill paint. This is the Paint object that will be used to fill the path area.
	 * 
	 * @return Gets the Paint being used. May be null
	 */
	public final Paint getFillPaint() {
		return fillPaint;
	}

	/**
	 * Sets the Paint to use. This is the Paint object that will be used to fill the path area. If null, nothing is
	 * painted
	 * 
	 * @param fillPaint the Paint to use
	 */
	public void setFillPaint(Paint fillPaint) {
		if (this.fillPaint == fillPaint || (this.fillPaint != null && this.fillPaint.equals(fillPaint)))
			return;

		this.fillPaint = fillPaint;
		setDirty(true);
	}
}
