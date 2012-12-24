package cop.swing.utils;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public final class GraphicsUtils {
	private GraphicsUtils() {}

	// Returns the graphics configuration for the primary screen
	private static GraphicsConfiguration getGraphicsConfiguration() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}

	private static boolean isHeadless() {
		return GraphicsEnvironment.isHeadless();
	}

	/**
	 * Returns a new translucent compatible image of the specified width and height. That is, the returned
	 * <code>BufferedImage</code> is compatible with the graphics hardware. If the method is called in a headless
	 * environment, then the returned BufferedImage will be compatible with the source image.
	 * 
	 * @param width the width of the new image
	 * @param height the height of the new image
	 * @return a new translucent compatible <code>BufferedImage</code> of the specified width and height
	 * @see #createCompatibleImage(java.awt.image.BufferedImage)
	 * @see #createCompatibleImage(java.awt.image.BufferedImage, int, int)
	 * @see #createCompatibleImage(int, int)
	 * @see #loadCompatibleImage(java.net.URL)
	 * @see #toCompatibleImage(java.awt.image.BufferedImage)
	 */
	public static BufferedImage createCompatibleTranslucentImage(int width, int height) {
		if (isHeadless())
			return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		return getGraphicsConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}

	/**
	 * Sets the clip on a graphics object by merging a supplied clip with the existing one. The new clip will be an
	 * intersection of the old clip and the supplied clip. The old clip shape will be returned. This is useful for
	 * resetting the old clip after an operation is performed.
	 * 
	 * @param g the graphics object to update
	 * @param clip a new clipping region to add to the graphics clip. This may return {@code null} if the current clip
	 *            is {@code null}.
	 * @return the current clipping region of the supplied graphics object
	 * @throws NullPointerException if any parameter is {@code null}
	 */
	public static Shape mergeClip(Graphics g, Shape clip) {
		Shape oldClip = g.getClip();
		if (oldClip == null) {
			g.setClip(clip);
			return null;
		}
		Area area = new Area(oldClip);
		area.intersect(new Area(clip));// new Rectangle(0,0,width,height)));
		g.setClip(area);
		return oldClip;
	}
}
