package cop.swing.painters;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import cop.swing.painters.enums.HorizontalAlignment;
import cop.swing.painters.enums.VerticalAlignment;

/**
 * An abstract base class for any painter which can be positioned. This means the painter has some intrinsic size to
 * what it is drawing and can be stretched or aligned both horizontally and vertically.<br>
 * <ul>
 * The AbstractLayoutPainter class provides the following configurable properties:
 * <li><i>horizonalAlignment</i> - the horizontal alignment (left, center, and right)
 * <li><i>verticalAlignment</i> - the verticalAlignment alignment (top, center, and bottom)
 * <li><i>fillHorizontal</i> - indicates if the painter should stretch to fill the available space horizontally
 * <li><i>fillVertical</i> - indicates if the painter should stretch to fill the available space vertically
 * <li><i>insets</i> - whitespace on the top, bottom, left, and right.
 * </ul>
 * <br>
 * By combining these five properties any AbstractLayoutPainter subclass can position it's content within the paintable
 * area. For example, an ImagePainter has an intrinsic size based on the image it is painting. If you wanted to paint
 * the image in the lower right hand corner of the paintable area, but inset by 5 pixels, you could do the following:
 * 
 * <pre>
 * ImagePainter p = new ImagePainter(null);
 * p.setVerticalAlignment(AbstractLayoutPainter.VerticalAlignment.BOTTOM);
 * p.setHorizontalAlignment(AbstractLayoutPainter.HorizontalAlignment.RIGHT);
 * p.setInsets(new Insets(0, 0, 5, 5));
 * </pre>
 * 
 * For something which is resizable, like a RectanglePainter, you can use the fill properties to make it resize along
 * with the paintable area. For example, to make a rectangle with 20 px rounded corners, and which resizes with the
 * paintable area but is inset by 10 pixels on all sides, you could do the following:
 * 
 * <pre>
 * RectanglePainter p = new RectanglePainter();
 * p.setRoundHeight(20);
 * p.setRoundWidth(20);
 * p.setInsets(new Insets(10, 10, 10, 10));
 * p.setFillHorizontal(true);
 * p.setFillVertical(true);
 * </pre>
 * 
 * @author joshua@marinacci.org
 */
public abstract class AbstractLayoutPainter<T extends Component> extends AbstractPainter<T> implements LayoutPainter<T> {
	public static final String PROP_INSETS = "insets";
	public static final String PROP_VERTICAL_ALIGNMENT = "verticalAlignment";
	public static final String PROP_HORIZONTAL_ALIGNMENT = "horizobtalAlignment";
	public static final String PROP_FILL_VERTICAL = "fillVertical";
	public static final String PROP_FILL_HORIZONTAL = "fillHorizontal";

	private static final VerticalAlignment DEF_VERTICAL_ALLIGNMENT = VerticalAlignment.CENTER;
	private static final HorizontalAlignment DEF_HORIZONTAL_ALLIGNMENT = HorizontalAlignment.CENTER;

	protected final Insets insets = new Insets(0, 0, 0, 0);

	/**
	 * Specifies how to draw the image, i.e. what kind of Style to use when drawing
	 */
	private VerticalAlignment verticalAlignment = DEF_VERTICAL_ALLIGNMENT;
	private HorizontalAlignment horizontalAlignment = DEF_HORIZONTAL_ALLIGNMENT;

	private boolean fillVertical;
	private boolean fillHorizontal;

	/**
	 * Gets the current horizontalAlignment alignment.
	 * 
	 * @return the current horizontalAlignment alignment
	 */
	public final HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	/**
	 * Gets the current whitespace insets.
	 * 
	 * @return the current insets
	 */
	public final Insets getInsets() {
		return new Insets(insets.top, insets.left, insets.bottom, insets.right);
	}

	/**
	 * gets the current verticalAlignment alignment
	 * 
	 * @return current verticalAlignment alignment
	 */
	public final VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * indicates if the painter content is stretched horizontally
	 * 
	 * @return the current horizontalAlignment stretch value
	 */
	public final boolean isFillHorizontal() {
		return fillHorizontal;
	}

	/**
	 * indicates if the painter content is stretched vertically
	 * 
	 * @return the current verticalAlignment stretch value
	 */
	public final boolean isFillVertical() {
		return fillVertical;
	}

	/**
	 * Sets a new horizontalAlignment alignment
	 * 
	 * @param horizontalAlignment new horizontalAlignment alignment
	 */
	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
		if (horizontalAlignment == null)
			horizontalAlignment = DEF_HORIZONTAL_ALLIGNMENT;
		if (this.horizontalAlignment == horizontalAlignment)
			return;

		this.horizontalAlignment = horizontalAlignment;
		setDirty(true);
	}

	/**
	 * Sets if the content should be stretched horizontally to fill all available horizontalAlignment space (minus the
	 * left and right insets).
	 * 
	 * @param fillHorizontal new horizontal stretch value
	 */
	public void setFillHorizontal(boolean fillHorizontal) {
		if (this.fillHorizontal == fillHorizontal)
			return;

		this.fillHorizontal = fillHorizontal;
		setDirty(true);
	}

	/**
	 * Sets if the content should be stretched vertically to fill all available verticalAlignment space (minus the top
	 * and bottom insets).
	 * 
	 * @param fillVertical new verticalAlignment stretch value
	 */
	public void setFillVertical(boolean fillVertical) {
		if (this.fillVertical == fillVertical)
			return;

		this.fillVertical = fillVertical;
		setDirty(true);
	}

	/**
	 * Sets the current whitespace insets.
	 * 
	 * @param insets new insets
	 */
	public void setInsets(int top, int left, int bottom, int right) {
		if (insets.top == top && insets.left == left && insets.bottom == bottom && insets.right == right)
			return;

		this.insets.set(top, left, bottom, right);
		setDirty(true);
	}

	/**
	 * Sets a new verticalAlignment alignment
	 * 
	 * @param verticalAlignment new verticalAlignment alignment
	 */
	public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
		if (verticalAlignment == null)
			verticalAlignment = DEF_VERTICAL_ALLIGNMENT;
		if (this.verticalAlignment == verticalAlignment)
			return;

		this.verticalAlignment = verticalAlignment;
		setDirty(true);
	}

	/**
	 * Calculates final position of the content. This will position the content using the {@link #fillHorizontal},
	 * {@link #fillVertical}, {@link #horizontalAlignment} and {@link #verticalAlignment} properties. This method is
	 * typically called by subclasses in their {@link #doPaint(Graphics2D, Component, int, int)} method.
	 * 
	 * @param contentWidth The width of the content to be painted
	 * @param contentHeight The height of the content to be painted
	 * @param width the width of the area that the content will be positioned in
	 * @param height the height of the area that the content will be positioned in
	 * @return the rectangle for the content to be painted in
	 */
	protected final Rectangle calculateLayout(int contentWidth, int contentHeight, int width, int height) {
		Rectangle rect = new Rectangle();

		rect.width = contentWidth;
		rect.height = contentHeight;

		if (fillHorizontal)
			rect.width = width - insets.left - insets.right;
		if (fillVertical)
			rect.height = height - insets.top - insets.bottom;

		rect.x = horizontalAlignment.getX(rect.width, width, insets.left, insets.right);
		rect.y = verticalAlignment.getY(rect.height, height, insets.top, insets.bottom);

		return rect;
	}
}
