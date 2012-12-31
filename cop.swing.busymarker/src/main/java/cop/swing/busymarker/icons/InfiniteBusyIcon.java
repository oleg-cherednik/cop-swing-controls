package cop.swing.busymarker.icons;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import cop.swing.busymarker.models.BusyModel;
import cop.swing.painters.InfiniteBusyPainter;

/**
 * An infinite icon rendering always an <code>undeterminate</code> state as long as the model is busy (whenever the
 * model's state).<br>
 * This implementation use a {@link InfiniteBusyPainter}.<br>
 * You can set the frame rate using the method {@link #setDelay(int)} that give the delay in milliseconds between 2
 * frames.
 * 
 * @author Oleg Cherednik
 * @since 26.03.2012
 */
public class InfiniteBusyIcon extends AbstractBusyIcon {
	private static final int DEF_DELAY = 100;
	private static final int DEF_HEIGHT = 26;

	private InfiniteBusyPainter<Component> painter;
	private final int width;
	private final int height;
	private int delay = DEF_DELAY;

	/**
	 * Default constructor of an InfiniteIcon.<br>
	 * This icon is set with a dimension of 26x26 and use default BusyPainter
	 */

	public InfiniteBusyIcon() {
		this(null);
	}

	public InfiniteBusyIcon(BusyModel model) {
		this(DEF_HEIGHT, DEF_HEIGHT, model);
	}

	/**
	 * Create an InfiniteBusyIcon using the default BusyPainter with the specified dimension.
	 * 
	 * @param width icon width
	 * @param height icon height
	 */
	public InfiniteBusyIcon(int width, int height, BusyModel model) {
		this(width, height, null, model);
	}

	/**
	 * Create an InfiniteBusyIcon using the specified BusyPainter with the specified dimension.
	 * 
	 * @param width icon width
	 * @param height icon height
	 * @param painter BusyPainter to use
	 */
	public InfiniteBusyIcon(int width, int height, InfiniteBusyPainter<Component> painter, BusyModel model) {
		this.width = width;
		this.height = height;

		setBusyPainter((painter != null) ? painter : new InfiniteBusyPainter<Component>());
		setModel(model);

	}

	/**
	 * Paint a background.<br>
	 * By default this method do nothing, but it can be overriden by subclasses
	 */
	protected void paintBackground(Component obj, Graphics g, int x, int y) {}

	/**
	 * Paint infinite spinner animation using the {@link InfiniteBusyPainter}
	 */
	protected void paintInfiniteSpinner(Component obj, Graphics g, int x, int y) {
		if (painter == null)
			return;

		Graphics2D g2d = (Graphics2D)g.create();

		try {
			g2d.translate(x, y);
			painter.paint(g2d, obj, width, height);
		} finally {
			g2d.dispose();
		}
	}

	/**
	 * Define the delay (in milliseconds) between 2 points
	 * 
	 * @param delay delay (in milliseconds) between 2 points
	 */
	public void setDelay(int delay) {
		if (this.delay == delay)
			return;

		this.delay = delay;

		if (painter != null)
			setUndeterminateFrameRate(delay, painter.getPoints());
		else
			setUndeterminateFrameRate(0, 0); // stop timer
	}

	/**
	 * Return the delay (in milliseconds) between 2 points
	 * 
	 * @return the delay (in milliseconds) between 2 points
	 */
	public final int getDelay() {
		// if (this.delay == -1)
		// return UIManager.getInt("JXBusyLabel.delay");
		return delay;
	}

	/**
	 * Define a new {@link InfiniteBusyPainter} to use by this icon.<br>
	 * 
	 * @param painter New BusyPainter to use by this icon.
	 */
	public void setBusyPainter(InfiniteBusyPainter<Component> painter) {
		if (this.painter == painter)
			return;

		this.painter = painter;
		int delay = (this.painter != null) ? this.delay : 0;
		int frameCount = (this.painter != null) ? this.painter.getPoints() : 0;

		setUndeterminateFrameRate(delay, frameCount);
		repaint(true);
	}

	/**
	 * Retrieve the {@link InfiniteBusyPainter} used by this icon.
	 * 
	 * @return The BusyPainter used by this icon
	 */
	public InfiniteBusyPainter<?> getBusyPainter() {
		return painter;
	}

	// ========== BusyIcon ==========

	/**
	 * This icon doesn't support a determinate state. That's why this method always returns <code>false</code>.
	 * 
	 * @return <code>false</code>
	 */
	@Override
	public final boolean isDeterminate() {
		return false;
	}

	// ========== AbstractBusyIcon ==========

	/**
	 * In an idle state, this icon paints only the background
	 */
	@Override
	protected void paintIdle(Component c, Graphics g, int x, int y) {
		paintBackground(c, g, x, y);
	}

	/**
	 * This method paint nothing, determinate state is not supported by this implementation
	 */
	@Override
	protected void paintDeterminate(Component c, Graphics g, int x, int y, double ratio) {
		paintIdle(c, g, x, y);
	}

	/**
	 * Paint this icon in an undeterminate state
	 */
	@Override
	protected void paintUndeterminate(Component c, Graphics g, int x, int y, int frame) {
		paintBackground(c, g, x, y);

		if (painter != null)
			painter.setFrame(frame);
		paintInfiniteSpinner(c, g, x, y);
	}

	// ========== Icon ==========

	public final int getIconWidth() {
		return width;
	}

	public final int getIconHeight() {
		return height;
	}

	// ========== AbstractBusyIcon ==========

	/**
	 * Since this icon render only undeterminate state, this method has no really interrest. we return arbitrary 0.01f
	 * (1%) offset
	 */
	@Override
	protected float getSignificantRatioOffset() {
		return 0.01f;
	}
}
