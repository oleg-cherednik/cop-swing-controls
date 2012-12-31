package cop.swing.busymarker.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.UIManager;

import cop.swing.busymarker.models.BusyModel;
import cop.swing.painters.Painter;
import cop.swing.painters.RectanglePainter;
import cop.swing.utils.ColorUtils;

/**
 * Default {@link BusyIcon} implementation that paint an icon and draw on overlay a <b>small progress bar</b>.
 * <p>
 * Some methods can be used for customize the LaF of this icon:
 * <ul>
 * <li>{@link #setBackgroundPainter(Painter)} and {@link #setBackgroundColor(Color)} : The background of this icon</li>
 * <li>{@link #setBackgroundPainted(boolean)} : <code>true</code> for paint the background, <code>false</code> for no
 * background</li>
 * <li>{@link #setIcon(Icon)} : Define the basic icon to draw</li>
 * <li>{@link #setProgressBarBounds(Rectangle)} : Define the location of the progress bar inside this icon</li>
 * <li>{@link #setProgressBarBackground(Paint)} and {@link #setProgressBarForeground(Paint)} : Define colors of the
 * progress bar</li>
 * </ul>
 * Theses methods defines some customization of the LaF's icon. Because some of theses define or depends of some
 * locations or size parameters, <strong>they should be set at initialization time</strong> and not be updated
 * elsewhere.<br>
 * <p>
 * A default LaF is installed when no customization is set.<br>
 * Theses defaults parameters can be overrided by subclasses for provide new oness:
 * <ul>
 * <li>{@link #createDefaultBackgroundPainter(Color)}: Install the default background painter</li>
 * <li>{@link #installDefaultProgressBarBounds()} : Install the default location and size of the progress bar</li>
 * <li>{@link #installDefaultProgressBarColors()} : Install default colors of the progress bar</li>
 * </ul>
 * <p>
 * If you need to change in a more depth the painting process, you can as the last step, to override our delegates
 * paint's methods:
 * <ul>
 * <li>{@link #paintBackground(Component, Graphics)} : Paint the background</li>
 * <li>{@link #paintDecoratedIcon(Component, Graphics, int, int) } : Paint the basic icon</li>
 * <li>{@link #paintProgressBar(Component, Graphics, Rectangle, boolean)} : Paint the <code>empty</code> progress bar</li>
 * <li>
 * {@link #paintProgressBarAdvance(Component, Graphics, Rectangle, boolean, float, float)} : Paint the progress bar
 * <code>advance</code></li>
 * </ul>
 * 
 * @author Oleg Cherednik
 * @since 08.04.2012
 */
public class DefaultBusyIcon extends BusyIconDecorator {
	/**
	 * The number of frames required in an undeterminate mode to move from the start to the end of the progression
	 * (cylic move)
	 */
	private static final int FRAMES = 20; // >> 1/20 advance progression by each frame

	/**
	 * This is the ratio of the progress bar length shifted between each frames
	 */
	private static final float FRAME_STEP_RATIO = (1f / FRAMES);

	/**
	 * The number of frames extends (determine the length of the progress bar advance drawn in an undeterminate mode)
	 */
	private static final int FRAME_EXTENDS = Math.round((0.33f / FRAME_STEP_RATIO));

	private boolean backgroundPainted = true;
	private Painter<?> backgroundPainter;

	private Paint barBackground;
	private Paint barForeground;
	private Rectangle barBounds = new Rectangle(0, 0, 10, 10);

	private int delay = 1000;

	public DefaultBusyIcon(Icon icon, BusyModel model) {
		this(icon);
		setModel(model);
	}

	public DefaultBusyIcon(Icon icon) {
		this(icon, Color.black);
	}

	public DefaultBusyIcon(Icon icon, Color backgroundColor) {
		this(icon, null, backgroundColor);
	}

	private DefaultBusyIcon(Icon icon, Painter<?> painter, Color color) {
		super(icon);

		setDelay(500);
		installDefaultProgressBarBounds();
		setBackgroundPainter(painter != null ? painter : createDefaultBackgroundPainter(color));
		installDefaultProgressBarColors();
	}

	/**
	 * Implement undeterminate rendering by drawing a cyclic move of the progress bar advance.<br>
	 * This method is marked <code>final</code> and can't be overrided.<br>
	 * If you want customize the painting process, you should override theses methods:
	 * <ul>
	 * <li>{@link #paintBackground(java.awt.Component, java.awt.Graphics)}</li>
	 * <li>{@link #paintDecoratedIcon(java.awt.Component, java.awt.Graphics, int, int) }
	 * <li>{@link #paintProgressBar(java.awt.Component, java.awt.Graphics, java.awt.Rectangle, boolean)}
	 * <li>
	 * {@link #paintProgressBarAdvance(java.awt.Component, java.awt.Graphics, java.awt.Rectangle, boolean, float, float)}
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
		/**
		 * Exemple: - FRAMES = 5 , FRAME_EXTENDS = 2 - FramesCount = (FRAMES - FRAME_EXTENDS) * 2 == (5-2) * 2 == 6 -
		 * FramesRange = [0 ~ 5 ] For Frames [ 0 , 1 , 2 ] : Foreward way >> frame #0 : [ X X - - - ] >> start = 0 ==
		 * #frame >> frame #1 : [ - X X - - ] >> start = 1 == #frame >> frame #2 : [ - - X X - ] >> start = 2 == #frame
		 * For Frames [ 3 , 4 , 5 ] : Backward way >> frame #3 : [ - - - X X ] >> start = 3 == Abs( #frame - FramesCount
		 * ) == 3 >> frame #4 : [ - - X X - ] >> start = 2 == Abs( #frame - FramesCount ) == 2 >> frame #5 : [ - X X - -
		 * ] >> start = 1 == Abs( #frame - FramesCount ) == 1
		 */

		int forewardFramesCount = FRAMES - FRAME_EXTENDS;
		int framesCount = forewardFramesCount * 2;
		if (frame >= forewardFramesCount) {
			frame = Math.abs(frame - framesCount);
		}

		float start = frame * FRAME_STEP_RATIO;
		float end = start + (FRAME_EXTENDS * FRAME_STEP_RATIO);

		paintImpl(c, g, x, y, false, start, end);
	}

	/**
	 * Implement determinate rendering by drawing a particular progress bar advance.<br>
	 * This method is marked <code>final</code> and can't be overrided.<br>
	 * If you want customize the painting process, you should override theses methods:
	 * <ul>
	 * <li>{@link #paintBackground(java.awt.Component, java.awt.Graphics)}</li>
	 * <li>{@link #paintDecoratedIcon(java.awt.Component, java.awt.Graphics, int, int) }
	 * <li>{@link #paintProgressBar(java.awt.Component, java.awt.Graphics, java.awt.Rectangle, boolean)}
	 * <li>
	 * {@link #paintProgressBarAdvance(java.awt.Component, java.awt.Graphics, java.awt.Rectangle, boolean, float, float)}
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
	 * Implement idle state rendering.<br>
	 * This method is marked <code>final</code> and can't be overrided.<br>
	 * If you want customize the painting process, you should override theses methods:
	 * <ul>
	 * <li>{@link #paintBackground(java.awt.Component, java.awt.Graphics) }
	 * <li>{@link #paintDecoratedIcon(java.awt.Component, java.awt.Graphics, int, int) }
	 * </ul>
	 * 
	 * @param c
	 * @param g
	 * @param x
	 * @param y
	 */
	@Override
	protected final void paintIdle(Component c, Graphics g, int x, int y) {
		if (isBackgroundPainted()) {
			Graphics2D g2d = (Graphics2D)g.create();
			try {
				g2d.translate(x, y);
				paintBackground(c, g2d);
			} finally {
				g2d.dispose();
			}
		}

		/**
		 * Paint the decorated icon
		 */
		Graphics2D g2d = (Graphics2D)g.create();
		try {
			paintDecoratedIcon(g2d, x, y);
		} finally {
			g2d.dispose();
		}
	}

	/**
	 * Define the delay (in milliseconds) for the <strong>undeterminate capsule</strong> to move completly inside the
	 * progress bar.
	 * 
	 * @param delay delay (in milliseconds) for the <strong>undeterminate capsule</strong> to move completly inside the
	 *            progress bar.
	 */
	public void setDelay(int delay) {
		/**
		 * Fix the frame rate for the undeterminate rate
		 */
		final float framesCount = (FRAMES - FRAME_EXTENDS) + 1;
		setUndeterminateFrameRate(Math.round(delay / framesCount), (FRAMES - FRAME_EXTENDS) * 2);
		this.delay = delay;
	}

	/**
	 * Retrieve the delay (in milliseconds) for the <strong>undeterminate capsule</strong> to move completly inside the
	 * progress bar.
	 * 
	 * @return delay (in milliseconds) for the <strong>undeterminate capsule</strong> to move completly inside the
	 *         progress bar.
	 */
	public int getDelay() {
		return this.delay;
	}

	/**
	 * Define the new background {@link Painter} creating the default painter with the specified color.<br>
	 * This method replace the previous Background Painter that can be set with the
	 * {@link #setBackgroundPainter(org.jdesktop.swingx.painter.Painter)}.<br>
	 * See {@link #createDefaultBackgroundPainter(java.awt.Color)} method for see how this background will be render.
	 * 
	 * @param color Color to use (can't be <code>null</code>
	 */
	public void setBackgroundColor(Color color) {
		createDefaultBackgroundPainter(color);
	}

	/**
	 * Define the new background {@link Painter} to use for render it.<br>
	 * This painter may be null, that means no background will be painted.
	 * 
	 * @param backgroundPainter The new background painter to use for render it.
	 */
	public void setBackgroundPainter(Painter<?> backgroundPainter) {
		if (backgroundPainter == null)
			backgroundPainter = createDefaultBackgroundPainter(Color.black);

		Painter<?> old = this.backgroundPainter;
		this.backgroundPainter = backgroundPainter;

		if (old != backgroundPainter)
			repaint(true);
	}

	/**
	 * Return the background {@link Painter} used for render it.<br>
	 * This painter may be null, that means no background is painted.
	 * 
	 * @return The background painter used for render it.
	 */
	public Painter<?> getBackgroundPainter() {
		return this.backgroundPainter;
	}

	/**
	 * Define if the background of this icon must be painted.
	 * 
	 * @param value <code>true</code> for enabling background of this icon.
	 */
	public void setBackgroundPainted(boolean value) {
		boolean old = isBackgroundPainted();
		this.backgroundPainted = value;
		if (old != isBackgroundPainted())
			repaint(true);
	}

	/**
	 * Indicate if the background of this icon is painted.
	 * 
	 * @return <code>true</code> is background painting is enabled.
	 */
	public boolean isBackgroundPainted() {
		return this.backgroundPainted;
	}

	/**
	 * Get the progress bar bounds inside this icon.<br>
	 * This bounds has coordinate that start from (0,0)
	 * <p>
	 * This should be set only by subclasses when they want to set a different location from the default.
	 * 
	 * @return Progress bar bounds
	 */
	protected Rectangle getProgressBarBounds() {
		return this.barBounds;
	}

	/**
	 * Define the progress bar bounds inside this icon.<br>
	 * This bounds has coordinate that start from (0,0)
	 * <p>
	 * This should be set only by subclasses when they want to set a different location from the default.
	 * 
	 * @param bounds
	 */
	protected void setProgressBarBounds(Rectangle bounds) {
		this.barBounds = bounds;
	}

	/**
	 * Define the progress bar background color.<br>
	 * This method requires a {@link Paint} object that can be a simple {@link Color}
	 */
	public void setProgressBarBackground(Paint background) {
		Paint old = this.barBackground;
		this.barBackground = background;
		if (old != this.barBackground)
			repaint(true);
	}

	/**
	 * Retrieve the progress bar background color.
	 * 
	 * @return Progress bar background color
	 */
	public Paint getProgressBarBackground() {
		return this.barBackground;
	}

	/**
	 * Define the progress bar foreground color.<br>
	 * This method requires a {@link Paint} object that can be a simple {@link Color}
	 */
	public void setProgressBarForeground(Paint foreground) {
		Paint old = this.barForeground;
		this.barForeground = foreground;
		if (old != this.barForeground)
			repaint(true);
	}

	/**
	 * Retrieve the progress bar foreground color.
	 * 
	 * @return Progress bar foreground color
	 */
	public Paint getProgressBarForeground() {
		return this.barForeground;
	}

	/**
	 * Install a default background painter used when no painter was specified or when a simple color was specified.<br>
	 * This implementation create a rounded square with a vertical gradient (shading from a brighted color to the
	 * specified color)
	 * <p>
	 * Subclasses can override this method for provide an other default background.
	 */
	protected Painter<?> createDefaultBackgroundPainter(Color color) {
		float[] fractions = { 0.2f, 0.8f };
		Color[] colors = { ColorUtils.brighter(color, 0.6f), color };
		int width = getIconWidth();
		int height = getIconHeight();

		Paint gradient = new LinearGradientPaint(0f, 0f, 0f, height, fractions, colors);
		RectanglePainter<Component> painter = new RectanglePainter<Component>(width, height);

		painter.setRoundWidth(width / 4);
		painter.setRoundHeight(height / 4);
		painter.setRounded(true);
		painter.setFillPaint(gradient);
		painter.setBorderWidth(1);
		painter.setBorderPaint(color);

		return painter;
	}

	/**
	 * Install default colors for the progress bar.<br>
	 * Theses colors are retrivied from the default UI using the {@link UIManager}:
	 * <ul>
	 * <li>The property <code>ProgressBar.background</code> for the background color</li>
	 * <li>The property <code>ProgressBar.foreground</code> for the foreground color</li>
	 * </ul>
	 */
	protected void installDefaultProgressBarColors() {
		setProgressBarBackground(createPaintUI("ProgressBar.background", getProgressBarBounds(), true));
		setProgressBarForeground(createPaintUI("ProgressBar.foreground", getProgressBarBounds(), true));
	}

	/**
	 * Install default bound's location of the progress bar inside this icon.<br>
	 * This implementaiton locate the progress bar at the bottom of the icon
	 * <ul>
	 * <li>Progress bar width = 80% of this icon</li>
	 * <li>Progress bar height = 12% of this icon</li>
	 * <li>Progress bar location = Horizontally centered, Vertically the upper-left corner is locate at 75% of this icon
	 * </li>
	 * </ul>
	 */
	protected void installDefaultProgressBarBounds() {
		int iconWidth = getIconWidth();
		int iconHeight = getIconHeight();

		int height = (int)(iconHeight * 0.12);
		int width = (int)(iconWidth * 0.80);
		int x = (iconWidth - width) / 2;
		int y = (int)(iconHeight * 0.75);

		setProgressBarBounds(new Rectangle(x, y, width, height));
	}

	/**
	 * Paint the background using the {@link Painter}
	 */
	protected void paintBackground(Component obj, Graphics g) {
		if (backgroundPainter == null)
			return;

		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		backgroundPainter.paint(g2d, null, getIconWidth(), getIconHeight());
	}

	/**
	 * Paint the <strong>empty</strong> progress bar using specified bounds location.
	 * 
	 * @param c Component to paint on
	 * @param g Graphics to paint on
	 * @param bounds Progress bar bounds location inside this icon
	 * @param determinate Indicate if the progress bar is in a determinate state or not
	 */
	protected void paintProgressBar(Component c, Graphics g, Rectangle bounds, boolean determinate) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setPaint(this.barBackground);
		g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, bounds.height, bounds.height / 2);
	}

	/**
	 * Paint the progress bar <strong>advance</strong> of this icon.
	 * 
	 * @param c Component to paint on
	 * @param g Graphics to paint on
	 * @param bounds Progress bar bounds location inside this icon
	 * @param determinate Indicate if the progress bar is in a determinate state or not
	 * @param start Start of the advance in a ratio (0f = at the start of the progress bar, 0.5f = at the midle of the
	 *            progress bar, 1.0f = at the end of the progress bar)
	 * @param end End of the advance in a ratio (0f = at the start of the progress bar, 0.5f = at the midle of the
	 *            progress bar, 1.0f = at the end of the progress bar)
	 */
	protected void paintProgressBarAdvance(Component c, Graphics g, Rectangle bounds, boolean determinate,
			double start, double end) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int offset = (int)Math.round(bounds.width * start);
		int length = (int)Math.round(bounds.width * (end - start));
		if (offset < 0)
			offset = 0;
		if (offset + length > bounds.x + bounds.width)
			length = bounds.x + bounds.width - offset;

		g2d.setPaint(this.barForeground);
		g2d.fillRoundRect(bounds.x + offset, bounds.y, length, bounds.height, bounds.height, bounds.height / 2);
	}

	@Override
	protected float getSignificantRatioOffset() {
		if (getIcon() == null)
			return 0.1f;
		return 1f / getProgressBarBounds().width;
	}

	/**
	 * Private paint implementation that deleguate all paint business to:
	 * <ul>
	 * <li>{@link #paintBackground(Component,Graphics)}</li>
	 * <li>{@link #paintDecoratedIcon(java.awt.Component, java.awt.Graphics, int, int)}</li>
	 * <li>{@link #paintProgressBar(java.awt.Component, java.awt.Graphics, java.awt.Rectangle, boolean)}</li>
	 * <li>
	 * {@link #paintProgressBarAdvance(java.awt.Component, java.awt.Graphics, java.awt.Rectangle, boolean, float, float)}
	 * </li>
	 * </ul>
	 */
	private void paintImpl(Component c, Graphics g, int x, int y, boolean determinate, double start, double end) {
		/**
		 * Paint the progress bar
		 */
		Graphics2D g2d = (Graphics2D)g.create();
		try {
			paintIdle(c, g, x, y);
		} finally {
			g2d.dispose();
		}

		/**
		 * Paint the progress bar
		 */
		g2d = (Graphics2D)g.create();
		try {
			g2d.translate(x, y);
			g2d.setClip(getProgressBarBounds());
			paintProgressBar(c, g, getProgressBarBounds(), determinate);
		} finally {
			g2d.dispose();
		}

		/**
		 * Paint the progress bar advance
		 */
		g2d = (Graphics2D)g.create();
		try {
			g2d.translate(x, y);
			g2d.setClip(getProgressBarBounds());
			paintProgressBarAdvance(c, g, getProgressBarBounds(), determinate, start, end);
		} finally {
			g2d.dispose();
		}
	}

	/**
	 * Create a Paint-UI
	 */
	private static Paint createPaintUI(String name, Rectangle bounds, boolean withShading) {
		Color color = UIManager.getColor(name);

		if (withShading) {
			float[] fractions = { 0.5f, 1f };
			Color[] colors = { ColorUtils.brighter(color, 0.30f), color };
			return new LinearGradientPaint(0f, bounds.y, 0f, bounds.y + bounds.height, fractions, colors);
		}
		return color;
	}
}
