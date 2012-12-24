package cop.swing.painters;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cop.swing.painters.enums.Interpolation;
import cop.swing.utils.GraphicsUtils;

/**
 * <p>
 * A convenient base class from which concrete {@link Painter} implementations may extend. It extends
 * {@link org.jdesktop.beans.AbstractBean} as a convenience for adding property change notification support. In
 * addition, <code>AbstractPainter</code> provides subclasses with the ability to cacheable painting operations,
 * configure the drawing surface with common settings (such as antialiasing and interpolation), and toggle whether a
 * subclass paints or not via the <code>visibility</code> property.
 * </p>
 * <p>
 * Subclasses of <code>AbstractPainter</code> generally need only override the
 * {@link #doPaint(Graphics2D, Object, int, int)} method. If a subclass requires more control over whether cacheing is
 * enabled, or for configuring the graphics state, then it may override the appropriate protected methods to interpose
 * its own behavior.
 * </p>
 * <p>
 * For example, here is the doPaint method of a simple <code>Painter</code> that paints an opaque rectangle:
 * 
 * <pre>
 * <code>
 *  public void doPaint(Graphics2D g, T obj, int width, int height) {
 *      g.setPaint(Color.BLUE);
 *      g.fillRect(0, 0, width, height);
 *  }
 * </code>
 * </pre>
 * </p>
 * 
 * @author rbair
 */
public abstract class AbstractPainter<T extends Component> implements Painter<T> {
	private final Set<BufferedImageOp> filters = new HashSet<BufferedImageOp>();

	private transient SoftReference<BufferedImage> cachedImage;

	private boolean cacheCleared = true;
	private boolean antialiasing = true;
	private boolean visible = true;
	private boolean cacheable;
	private boolean dirty;
	private Interpolation interpolation = Interpolation.NEAREST_NEIGHBOR;

	/**
	 * Creates a new instance of AbstractPainter.
	 */
	public AbstractPainter() {}

	/**
	 * Creates a new instance of AbstractPainter.
	 * 
	 * @param cacheable indicates if this painter should be cacheable
	 */
	public AbstractPainter(boolean cacheable) {
		setCacheable(cacheable);
	}

	/**
	 * A defensive copy of the Effects to apply to the results of the AbstractPainter's painting operation. The array
	 * may be empty but it will never be null.
	 * 
	 * @return the array of filters applied to this painter
	 */
	public final Set<BufferedImageOp> getFilters() {
		return filters.isEmpty() ? Collections.<BufferedImageOp> emptySet() : Collections.unmodifiableSet(filters);
	}

	/**
	 * <p>
	 * A convenience method for specifying the filters to use based on BufferedImageOps. These will each be individually
	 * wrapped by an ImageFilter and then setFilters(Effect... filters) will be called with the resulting array
	 * </p>
	 * 
	 * @param filters the BufferedImageOps to wrap as filters
	 */
	public void setFilters(BufferedImageOp... filters) {
		if (this.filters.isEmpty() && (filters == null || filters.length == 0))
			return;

		this.filters.clear();

		for (BufferedImageOp filter : filters)
			if (filter != null)
				this.filters.add(filter);

		setDirty(true);
	}

	public boolean isAntialiasing() {
		return antialiasing;
	}

	public void setAntialiasing(boolean antialiasing) {
		if (this.antialiasing == antialiasing)
			return;

		this.antialiasing = antialiasing;
		setDirty(true);
	}

	/**
	 * Gets the current interpolation setting. This property determines if interpolation will be used when drawing
	 * scaled images. @see java.awt.RenderingHints.KEY_INTERPOLATION.
	 * 
	 * @return the current interpolation setting
	 */
	public Interpolation getInterpolation() {
		return interpolation;
	}

	/**
	 * Sets a new value for the interpolation setting. This setting determines if interpolation should be used when
	 * drawing scaled images. @see java.awt.RenderingHints.KEY_INTERPOLATION.
	 * 
	 * @param interpolation the new interpolation setting
	 */
	public void setInterpolation(Interpolation interpolation) {
		if (interpolation == null)
			interpolation = Interpolation.NEAREST_NEIGHBOR;

		if (this.interpolation == interpolation)
			return;

		this.interpolation = interpolation;

		setDirty(true);
	}

	/**
	 * Gets the visible property. This controls if the painter should paint itself. It is true by default. Setting
	 * visible to false is good when you want to temporarily turn off a painter. An example of this is a painter that
	 * you only use when a button is highlighted.
	 * 
	 * @return current value of visible property
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * <p>
	 * Sets the visible property. This controls if the painter should paint itself. It is true by default. Setting
	 * visible to false is good when you want to temporarily turn off a painter. An example of this is a painter that
	 * you only use when a button is highlighted.
	 * </p>
	 * 
	 * @param visible New value of visible property.
	 */
	public void setVisible(boolean visible) {
		if (this.visible == visible)
			return;

		this.visible = visible;

		setDirty(true);
	}

	/**
	 * <p>
	 * Gets whether this <code>AbstractPainter</code> can be cached as an image. If cacheing is enabled, then it is the
	 * responsibility of the developer to invalidate the painter (via {@link #clearCache}) if external state has changed
	 * in such a way that the painter is invalidated and needs to be repainted.
	 * </p>
	 * 
	 * @return whether this is cacheable
	 */
	public boolean isCacheable() {
		return cacheable;
	}

	/**
	 * <p>
	 * Sets whether this <code>AbstractPainter</code> can be cached as an image. If true, this is treated as a hint.
	 * That is, a cacheable may or may not be used. The {@link #shouldUseCache} method actually determines whether the
	 * cacheable is used. However, if false, then this is treated as an absolute value. That is, no cacheable will be
	 * used.
	 * </p>
	 * <p>
	 * If set to false, then #clearCache is called to free system resources.
	 * </p>
	 * 
	 * @param cacheable
	 */
	public void setCacheable(boolean cacheable) {
		if (this.cacheable == cacheable)
			return;

		this.cacheable = cacheable;

		if (!this.cacheable)
			clearCache();
	}

	/**
	 * <p>
	 * Call this method to clear the cacheable. This may be called whether there is a cacheable being used or not. If
	 * cleared, on the next call to <code>paint</code>, the painting routines will be called.
	 * </p>
	 * <p>
	 * <strong>Subclasses</strong>If overridden in subclasses, you <strong>must</strong> call super.clearCache, or
	 * physical resources (such as an Image) may leak.
	 * </p>
	 */
	public void clearCache() {
		BufferedImage cache = cachedImage == null ? null : cachedImage.get();
		if (cache != null) {
			cache.flush();
		}
		cacheCleared = true;
		if (!isCacheable()) {
			cachedImage = null;
		}
	}

	/**
	 * Only made package private for testing. Don't call this method outside of this class! This is NOT a bound property
	 */
	boolean isCacheCleared() {
		return cacheCleared;
	}

	/**
	 * <p>
	 * Called to allow <code>Painter</code> subclasses a chance to see if any state in the given object has changed from
	 * the last paint operation. If it has, then the <code>Painter</code> has a chance to mark itself as dirty, thus
	 * causing a repaint, even if cached.
	 * </p>
	 * 
	 * @param object
	 */
	protected void validate(T object) {}

	/**
	 * Ye olde dirty bit. If true, then the painter is considered dirty and in need of being repainted. This is a bound
	 * property.
	 * 
	 * @return true if the painter state has changed and the painter needs to be repainted.
	 */
	protected boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets the dirty bit. If true, then the painter is considered dirty, and the cache will be cleared. This property
	 * is bound.
	 * 
	 * @param dirty whether this <code>Painter</code> is dirty.
	 */
	protected void setDirty(boolean dirty) {
		if (this.dirty == dirty)
			return;

		this.dirty = dirty;

		if (this.dirty)
			clearCache();
	}

	/**
	 * <p>
	 * Returns true if the painter should use caching. This method allows subclasses to specify the heuristics regarding
	 * whether to cache or not. If a <code>Painter</code> has intelligent rules regarding painting times, and can more
	 * accurately indicate whether it should be cached, it could implement that logic in this method.
	 * </p>
	 * 
	 * @return whether or not a cache should be used
	 */
	protected boolean shouldUseCache() {
		return cacheable || !filters.isEmpty(); // NOTE, I can only do this because getFilters() is final
	}

	/**
	 * This method is called by the <code>paint</code> method prior to any drawing operations to configure the drawing
	 * surface. The default implementation sets the rendering hints that have been specified for this
	 * <code>AbstractPainter</code>.<br>
	 * This method can be overridden by subclasses to modify the drawing surface before any painting happens.
	 * 
	 * @param g the graphics surface to configure. This will never be null.
	 * @see #paint(Graphics2D, Object, int, int)
	 */
	protected void configureGraphics(Graphics2D g) {
		Object value = antialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, value);
		interpolation.configureGraphics(g);
	}

	/**
	 * Subclasses must implement this method and perform custom painting operations here.
	 * 
	 * @param width
	 * @param height
	 * @param g2d The Graphics2D object in which to paint
	 * @param obj
	 */
	protected abstract void doPaint(Graphics2D g2d, T obj, int width, int height);

	public final void paint(Graphics2D g2d, T obj, int width, int height) {
		if (!visible || width < 1 || height < 1)
			return;

		configureGraphics(g2d);

		// paint to a temporary image if I'm caching, or if there are filters to apply
		if (shouldUseCache() || !filters.isEmpty()) {
			validate(obj);
			BufferedImage cache = (cachedImage != null) ? cachedImage.get() : null;
			boolean invalidCache = (cache == null) || (cache.getWidth() != width) || (cache.getHeight() != height);

			if (cacheCleared || invalidCache || dirty) {
				// rebuild the cacheable. I do this both if a cacheable is needed, and if any
				// filters exist. I only *save* the resulting image if caching is turned on
				if (invalidCache)
					cache = GraphicsUtils.createCompatibleTranslucentImage(width, height);

				if (cache == null)
					return;

				Graphics2D gfx = cache.createGraphics();

				try {
					gfx.setClip(0, 0, width, height);

					if (!invalidCache) {
						// If we are doing a repaint, but we didn't have to
						// recreate the image, we need to clear it back
						// to a fully transparent background.
						Composite composite = gfx.getComposite();
						gfx.setComposite(AlphaComposite.Clear);
						gfx.fillRect(0, 0, width, height);
						gfx.setComposite(composite);
					}

					configureGraphics(gfx);
					doPaint(gfx, obj, width, height);
				} finally {
					gfx.dispose();
				}

				for (BufferedImageOp f : getFilters())
					cache = f.filter(cache, null);

				// only save the temporary image as the cacheable if I'm caching
				if (shouldUseCache()) {
					cachedImage = new SoftReference<BufferedImage>(cache);
					cacheCleared = false;
				}
			}

			g2d.drawImage(cache, 0, 0, null);
		} else
			doPaint(g2d, obj, width, height);

		// painting has occurred, so restore the dirty bit to false
		setDirty(false);
	}
}
