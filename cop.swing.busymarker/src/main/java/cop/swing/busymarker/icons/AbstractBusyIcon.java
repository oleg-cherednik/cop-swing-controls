package cop.swing.busymarker.icons;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoundedRangeModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import cop.swing.busymarker.BusyListener;
import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.models.BusyState;
import cop.swing.busymarker.models.EmptyBusyModel;

/**
 * An implementation of the {@link BusyIcon} interface to serve as a basis for implementing various kinds of Busy Icons.
 * <p>
 * <ul>
 * At this level, this class gives some features to make {@link BusyIcon} implementation easier:
 * <li>Paint methods for various states of icons <code>(determinate,undeterminate,idle)</code></li>
 * <li>Common implementation for storing either a simple {@link BoundedRangeModel} or a more featured {@link BusyModel}</li>
 * <li>Buffer Image cache for optimize repaint's event from {@link BoundedRangeModel} changes :
 * {@link #setUseCache(boolean)}, {@link #getSignificantRatioOffset()}</li>
 * <li>Automatic frame rate mecanism for render undeterminate state animation :
 * {@link #setUndeterminateFrameRate(int, int)}</li>
 * <li>Provide a {@link #repaint(boolean)} for subclasses uses when they needs to send a repaint event</li>
 * <li>Extends {@link Observable} providing a delegate mecanism for repaint's event</li>
 * </ul>
 * <p>
 * This implementation provide 3 methods to implements for render a busy icon:
 * <ul>
 * <li>{@link #paintDeterminate(java.awt.Component, java.awt.Graphics, int, int, float)} for render a determinate state
 * with the specified ratio of the current progression</li>
 * <li>{@link #paintUndeterminate(java.awt.Component, java.awt.Graphics, int, int, int)} for render an undeterminate
 * state with the specified frame</li>
 * <li>{@link #paintIdle(java.awt.Component, java.awt.Graphics, int, int)} for render an idle state (not busy)</li>
 * </ul>
 * <p>
 * This icon accept generic {@link BoundedRangeModel} or a more specific {@link BusyModel}.<br>
 * When this icon is bound to a generic <code>BoundedRangeModel</code>, this icon is always considered in a
 * <code>determinate</code> state.<br>
 * But if this icon is bound to a <code>BusyModel</code>, this model can control the busy state or the
 * determinate/undeterminate state.
 * <p>
 * The buffer image cache is done for optimize painting process when this icon state are unchanged.<br>
 * Instead of call real paints methods each time, this abstract implementation use a previously rendered image of this
 * icon.<br>
 * In the other side, subclasses must implements {@link #getSignificantRatioOffset()} in the way to help this
 * implementation determine if the current buffer image cache is up to date regarding the current state of this icon.<br>
 * This basis implementation help us to prevent from various change from the {@link BoundedRangeModel}. Theses models
 * can have data range that can be large, and any minor change will fire a repaint event even if the change don't be
 * significant in the ui representation. That's why, you should implements the {@link #getSignificantRatioOffset()}
 * accordingly to your ui.
 * <p>
 * When this busy icon is on an <code>undeterminate</code> state, an internal timer will fire repaint events
 * periodically.<br>
 * The {@link #paintUndeterminate(java.awt.Component, java.awt.Graphics, int, int, int)} method will be use for
 * rendering this icon.<br>
 * The provided frame number is incremented each paint event and is cyclic accordingly to the configuration done by
 * {@link #setUndeterminateFrameRate(int, int)}.<br>
 * This method should be used by subclasses in order to configure the undeterminate frame rate animation.
 * <p>
 * A protected {@link #repaint(boolean)} method is provided for subclasses when they need to fire a repaint event.<br>
 * This method call a {@link Component#repaint()} on each components registered on this icon.<br>
 * Registered components are all components specified to the
 * {@link #paintIcon(java.awt.Component, java.awt.Graphics, int, int)}.<br>
 * In the other side, you can specify an {@link Observer} to this icon. If it's the case, components are not registered
 * and only observer will receive repaint's event.
 * <p>
 * This class don't give any basic implementation for {@link #getIconWidth()} and {@link #getIconHeight()} and must be
 * implemented by subclasses.
 * 
 * @author Oleg Cherednik
 * @since 26.03.2012
 */
public abstract class AbstractBusyIcon implements BusyIcon, ActionListener, ChangeListener {
	private final List<WeakReference<Component>> components = new LinkedList<WeakReference<Component>>();

	private BusyModel model = EmptyBusyModel.getInstance();
	private BufferedImage image;
	private float lastRatio = -1;
	private BusyState lastStateFlag;
	private boolean discarded;

	private int undeterminateFrameRate;
	private final Timer undeterminateTimer = new Timer(0, this);

	private int frameCount;
	private int frame;

	protected final EventListenerList listenerList = new EventListenerList();

	/**
	 * Paint this icon in a <code>determinate</code> state at the given ratio.
	 * 
	 * @param c Component using this icon
	 * @param g Graphics to paint on
	 * @param x Upper left corner (horizontal value)
	 * @param y Upper left corner (vertical value)
	 * @param ratio Current advance of the {@link BoundedRangeModel}
	 */
	protected abstract void paintDeterminate(Component c, Graphics g, int x, int y, double ratio);

	/**
	 * Paint this icon in an <code>undeterminate</code> state.<br>
	 * Regarding configuration set with {@link #setUndeterminateFrameRate(int, int)}, this method will be invoked
	 * periodically when this icon need to render an undeterminate state.<br>
	 * The given frame number is a cyclic counter in range [0 ~ frameCount - 1].
	 * 
	 * @param c Component using this icon
	 * @param g Graphics to paint on
	 * @param x Upper left corner (horizontal value)
	 * @param y Upper left corner (vertical value)
	 * @param frame Current undeterminate frame number
	 */
	protected abstract void paintUndeterminate(Component c, Graphics g, int x, int y, int frame);

	/**
	 * Paint this icon in an <code>idle</code> state.<br>
	 * An idle state means that this icon is not currently busy and this icon should not paint any related information
	 * from the {@link BusyModel}
	 * 
	 * @param c Component using this icon
	 * @param g Graphics to paint on
	 * @param x Upper left corner (horizontal value)
	 * @param y Upper left corner (vertical value)
	 */
	protected abstract void paintIdle(Component c, Graphics g, int x, int y);

	/**
	 * Return the minimum advance to reach by the {@link BoundedRangeModel} between two paint's requests in order to
	 * discard the current buffer image (cache) and update this icon with the current state.
	 * <p>
	 * The {@link BoundedRangeModel} can have a large length between it's minimum and maximum values. When this length
	 * is large, this icon may receive a lot of repaint's events.<br>
	 * All of theses event's should not be forwarded if theses event's don't perform a significant difference into this
	 * icon.
	 * <p>
	 * By exemple, a progress bar of <code>50</code> pixels should serve a repaint event if some pixels changes. The
	 * significant ratio offset should be <code>1/50 = 0.02f</code><br>
	 * In this case, this icon will be updated each time at least a pixel change inside the progress bar.
	 * <p>
	 * If you render a progress bar with a radial representation, typically you can return a significant ratio offset of
	 * <code>1/360</code>.<br>
	 * In this case, this icon will be updated each time at least a degree change inside the radial representation.
	 * <p>
	 * This information are used only in conjunction of the buffer image provided with {@link #useCache()} and
	 * {@link #setUseCache(boolean) } methods.<br>
	 * If the image cache is disabled, all repaint events notified by the {@link BoundedRangeModel} will be forwarded.
	 * 
	 * @return Minimum ratio offset that must be reach by the BoundedRangeModel between to paint processes. Can be set
	 *         to 0 for serve all paint's requests
	 * @see #useCache()
	 * @see #setUseCache(boolean)
	 * @see #getRatio()
	 */
	protected abstract float getSignificantRatioOffset();

	public synchronized void setModel(BusyModel model) {
		if (model == null)
			model = EmptyBusyModel.getInstance();
		if (this.model == model)
			return;

		this.model.removeChangeListener(this);
		this.model = model;
		this.model.addChangeListener(this);
	}

	/**
	 * Indicate if this icon is rendering a determinate or undeterminate progression.
	 * <p>
	 * This attribute can be data-driven by a {@link BusyModel} set into
	 * {@link #setModel(javax.swing.BoundedRangeModel)}.<br>
	 * Any other instances of {@link BoundedRangeModel} will be considered as determinate.
	 * 
	 * @return <code>true</code> if this icon render a determinate progression, <code>false</code> otherwise
	 */
	public boolean isDeterminate() {
		return model.isDeterminate();
	}

	/**
	 * If this icon supports animation for undeterminate model using a {@link BusyModel}, This method should be call in
	 * order to configure the frame rate of this animation
	 * 
	 * @param delay
	 */
	protected void setUndeterminateFrameRate(int delay, int frameCount) {
		this.undeterminateFrameRate = delay;
		this.frameCount = frameCount;
		refreshUndeterminateTimer();
	}

	/**
	 * Paint this icon.
	 * <p>
	 * This method can use an image buffer for render quickly this icon if no significant change is available since the
	 * last paint process.<br>
	 * You can enable or disable this behaviour with the {@link #setUseCache(boolean)} method.
	 * <p>
	 * When using a buffer image cache, this implementation must determine if the current image is up to date and can be
	 * used instead of perform a full painting process. This determination are done using the
	 * {@link #getSignificantRatioOffset()} information.
	 * <p>
	 * 
	 * @param comp Component using this icon
	 * @param g Graphics to paint on
	 * @param x Upper left corner (horizontal value)
	 * @param y Upper left corner (vertical value)
	 */
	public final void paintIcon(Component comp, Graphics g, int x, int y) {
		if (listenerList.getListenerCount(BusyListener.class) == 0)
			register(comp);

		updateImage();

		boolean busy = model.isBusy();
		boolean determinate = busy && isDeterminate();
		float ratio = determinate ? getRatio() : 0f;
		int frame = Math.max(0, this.frame);

		if (!isCacheUpToDate(busy, determinate, ratio)) {
			Graphics2D g2d = image.createGraphics();

			g2d.setComposite(AlphaComposite.Clear);
			g2d.fillRect(0, 0, getIconWidth(), getIconHeight());
			g2d.setPaintMode();

			if (busy) {
				if (determinate)
					paintDeterminate(comp, g2d, 0, 0, ratio);
				else
					paintUndeterminate(comp, g2d, 0, 0, frame);
			} else
				paintIdle(comp, g2d, 0, 0);

			lastRatio = ratio;
			lastStateFlag = BusyState.parseBusyState(busy, determinate);
			discarded = false;
		}

		g.drawImage(image, x, y, comp);
	}

	/**
	 * Request a paint update on any components that are previously painted this icon.
	 * <p>
	 * This method check the buffer-image up to date and if it's the case, the repaint will be ignored unless the
	 * <code>force</code> attribute is at <code>true</code>.
	 * 
	 * @param force <code>true</code> for serve a repaint in all cases.
	 */
	protected synchronized void repaint(boolean force) {
		if (!force) {
			if (isCacheUpToDate(model.isBusy(), isDeterminate(), getRatio()))
				return; // repaint request ignored
		} else
			discarded = true;

		Iterator<WeakReference<Component>> it = components.iterator();

		while (it.hasNext()) {
			WeakReference<Component> ref = it.next();
			Component component = ref.get();

			if (component != null && component.isShowing())
				component.repaint();
			else
				it.remove();
		}

		notifyListeners();
	}

	protected void notifyListeners() {
		for (BusyListener listener : listenerList.getListeners(BusyListener.class))
			listener.onBusyUpdate(this);
	}

	public final BusyModel getModel() {
		return model;
	}

	/**
	 * Indicate if the current buffer image is up to date and can be use for a quick-render of this icon.
	 * 
	 * @return <code>true</code> if the curent buffer image is up to date
	 * @see #useCache()
	 * @see #getSignificantRatioOffset()
	 */
	private boolean isCacheUpToDate(boolean isBusy, boolean determinate, float ratio) {
		BusyState state = BusyState.parseBusyState(isBusy, determinate);

		if (state != lastStateFlag)
			return false;

		if (determinate) {
			float offs = Math.abs(ratio - lastRatio);
			return !discarded && (lastRatio >= 0f && offs < getSignificantRatioOffset());
		}

		return !discarded;
	}

	private void updateImage() {
		if (image == null || image.getWidth() != getIconWidth() || image.getHeight() != getIconHeight())
			image = createImage(getIconWidth(), getIconHeight());
	}

	/**
	 * Return the current advance of the {@link BoundedRangeModel}. This advance is given as a ratio [0 ~ 1] where 0 =
	 * 0% and 1 == 100%
	 * 
	 * @return Curent advance of the {@link BoundedRangeModel}.
	 * @see #getSignificantRatioOffset()
	 */
	private float getRatio() {
		BusyModel model = getModel();

		if (model != null) {
			if (!model.isBusy())
				return 0f;
			if (!model.isDeterminate())
				return 0f;

			int length = model.getMaximum() - model.getMinimum();
			int value = model.getValue() + model.getExtent();
			return (float)value / (float)length;
		}
		return 0f;
	}

	private synchronized void register(Component component) {
		Iterator<WeakReference<Component>> it = components.iterator();

		while (it.hasNext()) {
			WeakReference<Component> ref = it.next();
			Component comp = ref.get();
			if (comp == null)
				it.remove();
			if (comp == component)
				return;
		}

		components.add(new WeakReference<Component>(component));
	}

	/**
	 * Unable to start/stop timer for paint undeterminate state
	 * 
	 * @return <code>true</code> if the timer was changed
	 */
	private boolean refreshUndeterminateTimer() {
		boolean timerEnabled = model.isBusy() && !isDeterminate() && this.undeterminateFrameRate > 0;
		if (timerEnabled) {
			this.undeterminateTimer.setDelay(this.undeterminateFrameRate);
			if (!this.undeterminateTimer.isRunning()) {
				this.frame = -1;
				this.undeterminateTimer.start();
				return true;
			}
		} else {
			if (this.undeterminateTimer.isRunning()) {
				this.undeterminateTimer.stop();
				return true;
			}
		}
		return false;
	}

	public void onModelChanged() {
		BusyModel model = getModel();

		/**
		 * Refresh configuration timer with the new model state Maybe the timer should be stopped, started,
		 * reconfigured...
		 */
		boolean force = refreshUndeterminateTimer();

		if (model != null) {
			if (model.getValue() == model.getMaximum() && lastRatio < 1f) {
				force = true;
			}
		}
		repaint(force);
	}

	private void onTimer() {
		frame = (++frame >= frameCount) ? 0 : frame;
		repaint(true);
	}

	// ========== BusyIcon ==========

	public void addListener(BusyListener listener) {
		if (listener != null) {
			listenerList.remove(BusyListener.class, listener);
			listenerList.add(BusyListener.class, listener);
		}
	}

	public void removeListener(BusyListener listener) {
		if (listener != null)
			listenerList.remove(BusyListener.class, listener);
	}

	// ========== ActionListener ==========

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == undeterminateTimer)
			onTimer();
	}

	// ========== ChangeListener ==========

	public synchronized void stateChanged(ChangeEvent e) {
		if (e.getSource() == model)
			onModelChanged();
	}

	// ========== static ==========

	protected static BufferedImage createImage(int width, int height) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

		return gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}
}
