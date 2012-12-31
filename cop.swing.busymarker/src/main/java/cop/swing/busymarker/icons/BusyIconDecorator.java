package cop.swing.busymarker.icons;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * An implementation of the {@link BusyIcon} interface to serve as a basis for implementing Busy Icons based on another
 * icon extended with busy capabilities.
 * <p>
 * The method {@link #setIcon(javax.swing.Icon)} can be used for change or set the decorated icon.<br>
 * This icon can have a bigger size than the decorated by fixing an {@link Insets} by subclasses.<br>
 * When an <code>Insets</code> is set, the decorated icon are centered regarding the Insets.
 * <p>
 * The decorated icon can be an animated icon (like a .gif), in this case, each time a new frame is available, a repaint
 * event will be fired for update this icon.<br>
 * For be able to catch theses new frame events, the decorated icon must be an {@link ImageIcon} or extends
 * {@link Observable} class.
 * <p>
 * This implementation don't implements {@link #paintDeterminate(Component, Graphics, int, int, float)} or
 * {@link #paintUndeterminate(Component, Graphics, int, int, int) } but offer a
 * {@link #paintDecoratedIcon(Component, Graphics, int, int) } that can be used by subclasses for drawing the decorated
 * icon.
 * <p>
 * The {@link #paintIdle(Component, Graphics, int, int) } is implemented by painting only the decorated icon as is
 * (respecting insets).<br>
 * You can override this method if you need to provide a more sophisticated render process for the idle state.
 * 
 * @author Oleg Cherednik
 * @since 08.04.2012
 */
public abstract class BusyIconDecorator extends AbstractBusyIcon implements ImageObserver, Observer {
	private Icon icon;
	private BufferedImage iconFrame;
	private final Insets insets = new Insets(4, 4, 4, 4);

	public BusyIconDecorator(Icon icon) {
		setIcon(icon);
	}

	/**
	 * Retrieve the current decorated icon of this decorator.<br>
	 * A decorator busy icon extends a decorated icon with some busy-ui capabilities.
	 * 
	 * @return Current decorated icon (may be <code>null</code>)
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * Define the new decorated icon.
	 * <p>
	 * A decorator busy icon extends a decorated icon with some busy capabilities.<br>
	 * This method allow to change dynamically the base of this icon by setting a new decorated icon.
	 * <p>
	 * The decorated icon will be centered on this icon accordingly to the current {@link Insets}
	 * 
	 * @param icon New decorated icon of this decorator (may be <code>null</code>)
	 */
	public synchronized void setIcon(Icon icon) {
		if (this.icon == icon)
			return;

		if (this.icon instanceof ImageIcon)
			((ImageIcon)this.icon).setImageObserver(null);
		if (this.icon instanceof Observable)
			((Observable)this.icon).deleteObserver(this);

		this.icon = icon;
		iconFrame = null;

		if (icon != null) {
			iconFrame = createImage(icon.getIconWidth(), icon.getIconHeight());

			if (icon instanceof ImageIcon)
				((ImageIcon)icon).setImageObserver(this);
			if (icon instanceof Observable)
				((Observable)icon).addObserver(this);
		}

		doIconFrameUpdate(null);
	}

	protected void setInsets(int top, int left, int bottom, int right) {
		insets.top = top;
		insets.left = left;
		insets.bottom = bottom;
		insets.right = right;

		repaint(true);
	}

	/**
	 * By default, a decorator icon paint an <code>idle</code> state by painting the decorated icon without anything
	 * else.<br>
	 * If you want a more sophisticated idle icon, you should override this method.
	 */
	@Override
	protected void paintIdle(Component c, Graphics g, int x, int y) {
		paintDecoratedIcon(g, x, y);
	}

	/**
	 * Paint the decorated icon using <code>Insets</code> for place it inside this <code>DecoratorBusyIcon</code>
	 */
	protected synchronized void paintDecoratedIcon(Graphics g, int x, int y) {
		if (iconFrame != null)
			g.drawImage(iconFrame, x + insets.left, y + insets.top, null);
	}

	/**
	 * A new frame is available from the decorated icon.<br>
	 * We must update our reference-frame painted by the
	 * {@link #paintDecoratedIcon(java.awt.Component, java.awt.Graphics, int, int) }
	 * 
	 * @param image Image containing the new frame. If this image is <code>null</code>, we take directly the icon for
	 *            refresh our buffered image
	 */
	private synchronized void doIconFrameUpdate(Image image) {
		if (iconFrame != null) {
			Graphics2D g2d = iconFrame.createGraphics();

			try {
				g2d.setComposite(AlphaComposite.Clear);
				g2d.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
				g2d.setPaintMode();

				if (image != null)
					g2d.drawImage(image, 0, 0, null);
				else
					icon.paintIcon(null, g2d, 0, 0);
			} finally {
				g2d.dispose();
			}
		}

		repaint(true);
	}

	// ========== Icon ==========

	public int getIconWidth() {
		int result = insets.left + insets.right;
		return (icon != null) ? (result + icon.getIconWidth()) : result;
	}

	public int getIconHeight() {
		int result = insets.top + insets.bottom;
		return (icon != null) ? (result + icon.getIconHeight()) : result;
	}

	// ========== ImageObserver ==========

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		if ((infoflags & (FRAMEBITS | ALLBITS)) != 0) {
			doIconFrameUpdate(img);
		}
		return (infoflags & (ALLBITS | ABORT)) == 0;
	}

	// ========== Observer ==========

	public void update(Observable o, Object arg) {
		doIconFrameUpdate(null);
	}
}
