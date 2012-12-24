package cop.swing.busymarker.icons;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.UIManager;

import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.plaf.BusyPaneUI;
import cop.swing.utils.ColorUtils;

/**
 * {@link BusyIcon} drawing a <strong>radial</strong> progress bar (circle) below the decorated icon<br>
 * <p>
 * Some methods can be used for customize the LaF of this icon:
 * <ul>
 * <li>{@link #setIcon(Icon)} - define the basic icon to draw</li>
 * <li>{@link #setProgressBarDiameter(int)} - define the diameter of the progress bar (the bar is centered on this icon)
 * </li>
 * <li>{@link #setProgressBarThickness(int)} - define the thickness of the progress bar</li>
 * <li>{@link #setProgressBarBackground(Paint)} - define the background color of the progress bar</li>
 * <li>{@link #setProgressBarForeground(Paint)} - define the foreground color of the progress bar</li>
 * </ul>
 * Theses methods defines some customization of the LaF's icon. Because some of theses define or depends of some
 * locations or size parameters, <strong>they should be set at initialization time</strong> and not be updated
 * elsewhere.<br>
 * <p>
 * The default LaF is installed when no customization is set.<br>
 * Theses defaults parameters can be overrided by subclasses for provide new ones:
 * <ul>
 * <li>{@link #installDefaults()}: Install default settings</li>
 * </ul>
 * <p>
 * If you need to change in a more depth the painting process, you can as the last step, to override our delegates
 * paint's methods:
 * <ul>
 * <li>{@link #paintDecoratedIcon(Component, Graphics, int, int) } : Paint the basic icon</li>
 * <li>{@link #paintProgressBar(Component, Graphics, boolean)} : Paint the <code>empty</code> progress bar</li>
 * <li>{@link #paintProgressBarAdvance(Component, Graphics, boolean, float, float)} : Paint the progress bar
 * <code>advance</code></li>
 * </ul>
 * 
 * @author Oleg Cherednik
 * @since 26.03.2012
 */
public class RadialBusyIcon extends BusyIconDecorator {

	/**
	 * Length of arc painted in determinate/undeterminate states
	 */
	private static final int DETERMINATE_ARC_LENGTH = 45;
	private static final int DETERMINATE_RADIAL_INSETS = 4;
	private static final int UNDETERMINATE_ARC_LENGTH = 360;
	private static final int UNDETERMINATE_RADIAL_INSETS = 0;

	private Paint backgroundPaint;
	private Paint foregroundPaint;
	private int diameter = 0;
	private int thickness = 0;
	private int delay = 200;

	private int undeterminateAdvanceLength = UIManager.getInt(BusyPaneUI.BI_UNDETERMINATE_ADVANCE_LENGTH);

	/**
	 * Default constructor with the specified decorated icon and specified insets.<br>
	 * 
	 * @param icon Decorated icon to use
	 */
	public RadialBusyIcon(Icon icon) {
		this(icon, null);
	}

	public RadialBusyIcon(Icon icon, BusyModel model) {
		super(icon);

		setInsets(7, 7, 7, 7);
		setDelay(1500);
		installDefaults();
		setModel(model);
	}
	
	public final void setUndeterminateAdvanceLength(int length) {
		undeterminateAdvanceLength = length;
	}

	/**
	 * Implement undeterminate rendering by drawing a cyclic turn of the progress bar advance.<br>
	 * This method is marked <code>final</code> and can't be overrided.<br>
	 * If you want customize the painting process, you should override theses methods:
	 * <ul>
	 * <li>{@link #paintDecoratedIcon(Component, Graphics, int, int) }
	 * <li>{@link #paintProgressBar(Component, Graphics, boolean)}
	 * <li>{@link #paintProgressBarAdvance(Component, Graphics, boolean, float, float)}
	 * </ul>
	 * <p>
	 * 
	 * @param c Component to paint on
	 * @param g Graphics to paint on
	 * @param x horizontal location to start painting this icon
	 * @param y vertical location to start painting this icon
	 * @param frame frame number to paint (cyclic undeterminate painting)
	 */
	@Override
	protected final void paintUndeterminate(Component c, Graphics g, int x, int y, int frame) {
		double start = frame / 36d;
		double end = start + undeterminateAdvanceLength / 360d;
		paintImpl(c, g, x, y, false, start, end);
	}

	/**
	 * Implement determinate rendering by drawing a particular progress bar advance.<br>
	 * This method is marked <code>final</code> and can't be overrided.<br>
	 * If you want customize the painting process, you should override theses methods:
	 * <ul>
	 * <li>{@link #paintDecoratedIcon(java.awt.Component, java.awt.Graphics, int, int) }
	 * <li>{@link #paintProgressBar(java.awt.Component, java.awt.Graphics, boolean)}
	 * <li>{@link #paintProgressBarAdvance(java.awt.Component, java.awt.Graphics, boolean, float, float)}
	 * </ul>
	 * <p>
	 * 
	 * @param c Component to paint on
	 * @param g Graphics to paint on
	 * @param x horizontal location to start painting this icon
	 * @param y vertical location to start painting this icon
	 * @param ratio progress bar advance to paint
	 */
	@Override
	protected final void paintDeterminate(Component c, Graphics g, int x, int y, double ratio) {
		paintImpl(c, g, x, y, true, 0, ratio);
	}

	/**
	 * Paint the <strong>empty</strong> progress bar using specified bounds location.
	 * 
	 * @param c Component to paint on
	 * @param g Graphics to paint on
	 * @param determinate Indicate if the progress bar is in a determinate state or not
	 */
	protected void paintProgressBar(Component c, Graphics g, boolean determinate) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int arc_length = DETERMINATE_ARC_LENGTH;
		int insets = DETERMINATE_RADIAL_INSETS;
		if (!determinate) {
			arc_length = UNDETERMINATE_ARC_LENGTH;
			insets = UNDETERMINATE_RADIAL_INSETS;
		}

		/**
		 * Compute the upper left corner where draw the progress bar We compute it in regards to center the progress bar
		 * inside this icon
		 */
		int upperLeftX = (getIconWidth() - getProgressBarDiameter()) / 2;
		int upperLeftY = (getIconHeight() - getProgressBarDiameter()) / 2;

		g2d.setPaint(getBackgroundPaint());
		for (int i = insets; i < 360; i = i + arc_length) {
			int start = i;
			int angle = arc_length - (insets * 2);

			g2d.fillArc(upperLeftX, upperLeftY, getProgressBarDiameter(), getProgressBarDiameter(),
					translateAngle(start), angle * -1);
		}

		{
			g2d.setComposite(AlphaComposite.Clear);
			int holeDiameter = getProgressBarDiameter() - getProgressBarThickness();
			upperLeftX = (getIconWidth() - holeDiameter) / 2;
			upperLeftY = (getIconHeight() - holeDiameter) / 2;

			g2d.fillOval(upperLeftX, upperLeftY, holeDiameter, holeDiameter);
			g2d.setPaintMode();
		}
	}

	/**
	 * Paint the progress bar <strong>advance</strong> of this icon.
	 * 
	 * @param c Component to paint on
	 * @param g Graphics to paint on
	 * @param determinate Indicate if the progress bar is in a determinate state or not
	 * @param start Start of the advance in a ratio (0f = at the start of the progress bar, 0.5f = at the midle of the
	 *            progress bar, 1.0f = at the end of the progress bar)
	 * @param end End of the advance in a ratio (0f = at the start of the progress bar, 0.5f = at the midle of the
	 *            progress bar, 1.0f = at the end of the progress bar)
	 */
	protected void paintProgressBarAdvance(Component c, Graphics g, boolean determinate, double start, double end) {
		int arc_length = DETERMINATE_ARC_LENGTH;
		int insets = DETERMINATE_RADIAL_INSETS;
		if (!determinate) {
			arc_length = UNDETERMINATE_ARC_LENGTH;
			insets = UNDETERMINATE_RADIAL_INSETS;
		}

		/**
		 * Configure the graphics
		 */
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(getForegroundPaint());

		/**
		 * Compute the upper left corner where draw the progress bar We compute it in regards to center the progress bar
		 * inside this icon
		 */
		int upperLeftX = (getIconWidth() - getProgressBarDiameter()) / 2;
		int upperLeftY = (getIconHeight() - getProgressBarDiameter()) / 2;

		/**
		 * Arc to paint
		 */
		int startAngle = (int)Math.round(start * 360);
		int endAngle = (int)Math.round(end * 360);

		boolean started = false;
		for (int i = insets; i < 360 + arc_length; i = i + arc_length) {
			int myArcLength = (arc_length - (insets * 2));
			int myArcStart = i;
			int myArcEnd = myArcStart + myArcLength;

			if (!started) {
				if ((i - insets <= startAngle) && (startAngle < myArcEnd))
					started = true;
				else
					continue; // not an arc to paint with an advance status
			}

			if (endAngle < myArcStart)
				break;

			myArcStart = Math.max(startAngle, myArcStart);
			myArcEnd = Math.min(endAngle, myArcEnd);

			g2d.fillArc(upperLeftX, upperLeftY, getProgressBarDiameter(), getProgressBarDiameter(),
					translateAngle(myArcStart), (myArcEnd - myArcStart) * -1);
		}

		{
			g2d.setComposite(AlphaComposite.Clear);
			int holeDiameter = getProgressBarDiameter() - getProgressBarThickness();
			upperLeftX = (getIconWidth() - holeDiameter) / 2;
			upperLeftY = (getIconHeight() - holeDiameter) / 2;

			g2d.fillOval(upperLeftX, upperLeftY, holeDiameter, holeDiameter);
			g2d.setPaintMode();
		}
	}

	@Override
	protected float getSignificantRatioOffset() {
		return 1f / 360f;
	}

	/**
	 * Define the delay (in milliseconds) for the <strong>undeterminate capsule</strong> to move a complete turn inside
	 * the progress bar
	 * 
	 * @param delay delay (in milliseconds) for the <strong>undeterminate capsule</strong> to move a complete turn
	 *            inside the progress bar
	 */
	public void setDelay(int delay) {
		this.setUndeterminateFrameRate(Math.round(delay / 36f), 36);
		this.delay = delay;
	}

	/**
	 * Retrieve the delay (in milliseconds) for the <strong>undeterminate capsule</strong> to move a complete turn
	 * inside the progress bar.
	 * 
	 * @return delay (in milliseconds) for the <strong>undeterminate capsule</strong> to move a complete turn inside the
	 *         progress bar.
	 */
	public int getDelay() {
		return this.delay;
	}

	/**
	 * Define the radial progress bar diameter.<br>
	 * The progress bar has a radial form centered inside this icon (accordingly to insets that can provide a shift)
	 * 
	 * @param diameter New diameter of the progress bar of this icon
	 */
	protected void setProgressBarDiameter(int diameter) {
		int old = getProgressBarDiameter();
		this.diameter = diameter;
		if (old != getProgressBarDiameter())
			repaint(true);
	}

	/**
	 * Return the radial progress bar diameter.<
	 * 
	 * @return diameter of the progress bar of this icon
	 */
	protected int getProgressBarDiameter() {
		return this.diameter;
	}

	/**
	 * Define the radial progress bar thickness.
	 * 
	 * @param thickness New thickness of the radial progress bar of this icon.
	 */
	protected void setProgressBarThickness(int thickness) {
		int old = getProgressBarThickness();
		this.thickness = thickness;
		if (old != getProgressBarThickness())
			repaint(true);
	}

	/**
	 * Retrieve the radial progress bar thickness
	 * 
	 * @return Thickness of the radial progress bar of this icon.
	 */
	protected int getProgressBarThickness() {
		return this.thickness;
	}

	/**
	 * Define the progress bar background color.<br>
	 * This method requires a {@link Paint} object that can be a simple {@link Color}
	 */
	public void setProgressBarBackground(Paint backgroundPaint) {
		if (this.backgroundPaint == backgroundPaint)
			return;

		this.backgroundPaint = backgroundPaint;
		repaint(true);
	}

	/**
	 * Retrieve the progress bar background color.
	 * 
	 * @return Progress bar background color
	 */
	public final Paint getBackgroundPaint() {
		return backgroundPaint;
	}

	/**
	 * Define the progress bar foreground color.<br>
	 * This method requires a {@link Paint} object that can be a simple {@link Color}
	 */
	public void setProgressBarForeground(Paint foregroundPaint) {
		if (this.foregroundPaint == foregroundPaint)
			return;

		this.foregroundPaint = foregroundPaint;
		repaint(true);
	}

	/**
	 * Retrieve the progress bar foreground color.
	 * 
	 * @return Progress bar foreground color
	 */
	public final Paint getForegroundPaint() {
		return foregroundPaint;
	}

	/**
	 * Install default settinfs for the progress bar (diameter,thickness,colors)
	 */
	protected void installDefaults() {
		int diameter = Math.min(getIconWidth(), getIconHeight());
		int thick = Math.round(diameter * 0.50f);

		setProgressBarDiameter(diameter);
		setProgressBarThickness(thick);
		setProgressBarBackground(createPaintUI(Color.gray, true));
		setProgressBarForeground(createPaintUI(ColorUtils.getColor(117, 205, 78), true));
	}

	/**
	 * Private paint implementation that deleguate all paint business to:
	 * <ul>
	 * <li>{@link #paintProgressBar(java.awt.Component, java.awt.Graphics, boolean)}</li>
	 * <li>{@link #paintProgressBarAdvance(java.awt.Component, java.awt.Graphics, boolean, float, float)}</li>
	 * <li>{@link #paintDecoratedIcon(java.awt.Component, java.awt.Graphics, int, int)}</li>
	 * </ul>
	 */
	private void paintImpl(Component c, Graphics g, int x, int y, boolean determinate, double start, double end) {
		/**
		 * Paint the empty progress bar
		 */
		Graphics2D g2d = (Graphics2D)g.create();
		try {
			g2d.translate(x, y);
			paintProgressBar(c, g, determinate);
		} finally {
			g2d.dispose();
		}

		/**
		 * Paint the advance
		 */
		g2d = (Graphics2D)g.create();
		try {
			g2d.translate(x, y);
			paintProgressBarAdvance(c, g, determinate, start, end);
		} finally {
			g2d.dispose();
		}

		/**
		 * Paint the decorated icon
		 */
		g2d = (Graphics2D)g.create();
		try {
			paintDecoratedIcon(g, x, y);
		} finally {
			g2d.dispose();
		}
	}

	/**
	 * Create a Paint-UI
	 */
	private Paint createPaintUI(Color color, boolean withShading) {
		if (!withShading)
			return color;

		int holeDiameter = getProgressBarDiameter() - getProgressBarThickness();
		float holeFraction = (float)holeDiameter / (float)diameter;

		int xBar = ((getIconWidth() - diameter) / 2) + (diameter / 2);
		int yBar = ((getIconHeight() - diameter) / 2) + (diameter / 2);

		float[] fractions = { holeFraction, 1.0f };
		Color[] colors = { ColorUtils.brighter(color, 0.4f), color };

		return new RadialGradientPaint(xBar, yBar, Math.round(diameter / 2), fractions, colors);
	}

	/*
	 * static
	 */

	/**
	 * By default the degree #0 is a 3 o'clock and a positive angle go in a counter clockwise. This method translate the
	 * degree #0 at 12 o'clock and a positive angle is now in the clockwise.
	 */
	private static int translateAngle(int degree) {
		return 90 - degree;
	}
}
