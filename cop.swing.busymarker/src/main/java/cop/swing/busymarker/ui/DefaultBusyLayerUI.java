package cop.swing.busymarker.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Formatter;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

import org.jdesktop.jxlayer.JXLayer;

import cop.swing.Hyperlink;
import cop.swing.busymarker.RemainingTimeMonitor;
import cop.swing.busymarker.icons.BusyIcon;
import cop.swing.busymarker.icons.EmptyBusyIcon;
import cop.swing.busymarker.icons.InfiniteBusyIcon;
import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.models.EmptyBusyModel;
import cop.swing.busymarker.plaf.BusyPaneUI;
import cop.swing.painters.EmptyPainter;
import cop.swing.painters.LayoutPainter;
import cop.swing.painters.MattePainter;
import cop.swing.utils.ColorUtils;

/**
 * A default implementation of {@link BusyLayerUI}.<br>
 * This <tt>ui</tt> provides busy animation, progress bar and cancellation button regarding the {@link BusyModel}.<br>
 * You can enhance any swing components with busy functionality like it:
 * 
 * <pre>
 * JPanel panel = new JPanel();
 * JLabel label = new JLabel({@literal "}The component{@literal "});
 * 
 * // Create the JXLayer decorator
 * JXLayer<JLabel>; layer = new JXLayer<JLabel>(label);
 * 
 * // Create the Busy Layer UI delegate
 * BusyLayerUI ui = new DefaultBusyLayerUI();
 * 
 * // Attach the UI to the decorator
 * layer.setUI(ui);
 * 
 * // Add the decorator to the container instead of our component
 * panel.add(layer);
 * 
 * // Use the BusyModel for control the busy state on our component
 * // If multiple components share the same {@link BusyModel}, all of theses will be
 * // triggered by the same model
 * ui.getBusyModel().setBusy(true); // an animation over our component is shown
 * </pre>
 * 
 * @author Oleg Cherednik
 * @since 24.03.2012
 */
public class DefaultBusyLayerUI extends BusyLayerUI implements ActionListener {
	private static final long serialVersionUID = -781504195248307094L;

	private final JLabel label = createLabel();
	private final JProgressBar progressBar = new JProgressBar();
	private final Hyperlink cancelButton = new Hyperlink("Cancel");
	private final JPanel panel = createPanel(label, progressBar, cancelButton);

	private final int shadeDelay; // in milliseconds

	private BusyIcon busyIcon = EmptyBusyIcon.OBJ;

	/**
	 * Members managing popup trigger and remaining time
	 */

	private boolean remainingTimeVisible;
	private RemainingTimeMonitor monitor;
	private int millisToDecideToPopup = 300;
	private int millisToPopup = 1200;

	protected final int veilAlpha; // % of alpha [0;100]
	protected final Color veilColor;

	private long shadeTime;

	private int alpha; // [0;0xFF]
	private LayoutPainter<JPanel> painter = EmptyPainter.create();
	private final Timer timer = new Timer(REFRESH_DELAY, this);
	private final AtomicBoolean repainted = new AtomicBoolean(false);

	private static final int REFRESH_DELAY = 32;

	public DefaultBusyLayerUI() {
		this(UIManager.getInt(BusyPaneUI.SHADE_DELAY), UIManager.getInt(BusyPaneUI.VEIL_ALPHA), UIManager
				.getColor(BusyPaneUI.COLOR_VEIL));
	}

	/**
	 * Basic Implementation with shading configuration's
	 * 
	 * @param shadeDelay Shading delay in milliseconds for render <code>busy</code> state change, 0 means no shading
	 * @param shadeFps Frame per Seconds to use for render the shading animation.
	 * @param veilAlpha Alpha ratio to use for the veil when the model is <code>busy</code>
	 * @param veilColor Color to use for render the veil
	 */
	public DefaultBusyLayerUI(int shadeDelay, int veilAlpha, Color veilColor) {
		this.shadeDelay = Math.max(0, shadeDelay);
		this.veilAlpha = Math.max(0, Math.min(100, veilAlpha));
		this.veilColor = (veilColor != null) ? veilColor : UIManager.getColor(BusyPaneUI.COLOR_VEIL);

		setBusyIcon(new InfiniteBusyIcon(model));

		this.cancelButton.addActionListener(this);
	}

	/**
	 * Define the BusyIcon to use by this ui to render the busy animation.
	 * 
	 * @param busyIcon New BusyIcon to use by this ui
	 */
	public void setBusyIcon(BusyIcon busyIcon) {
		if (this.busyIcon == busyIcon)
			return;

		if (this.busyIcon instanceof Observable)
			((Observable)this.busyIcon).deleteObserver(this);

		this.busyIcon.setModel(EmptyBusyModel.getInstance());
		this.busyIcon = (busyIcon != null) ? busyIcon : EmptyBusyIcon.OBJ;

		if (this.busyIcon instanceof Observable)
			((Observable)this.busyIcon).addObserver(this);

		this.busyIcon.setModel(model);
		label.setIcon(this.busyIcon);

		update();
	}

	/**
	 * Return the BusyIcon used by this ui for render the busy animation.
	 * 
	 * @return BusyIcon used by this ui
	 */
	public BusyIcon getBusyIcon() {
		return busyIcon;
	}

	/**
	 * Specifies the amount of time to wait before deciding whether or not to make busy the component when it's
	 * underlying model is.
	 * <p>
	 * This feature purpose is to prevent to show a progress bar for a very very short time.<br>
	 * This {@link _BusyLayerUI} wait few times (300ms by default) and decide to popup the progress bar or not.
	 * <p>
	 * The decision was made by computing a predicted remaining time of the underlying task.<br>
	 * If the remaining time is long enough (>= 1200ms by default), the progress bar will shown.
	 * <p>
	 * When the model gone busy, the component is instantly locked (can't be accessed anymore) even if the progress bar
	 * is not yet visible.
	 * <p>
	 * Setting a 0 value or any negative value disable this feature. In this case, the progress bar will popup instantly
	 * when the model become busy.
	 * 
	 * @param millisToDecideToPopup an int specifying the time to wait, in milliseconds
	 * @see #getMillisToDecideToPopup
	 */
	public void setMillisToDecideToPopup(int millisToDecideToPopup) {
		this.millisToDecideToPopup = millisToDecideToPopup;
	}

	/**
	 * Returns the amount of time this object waits before deciding whether or not to propage the busy state from the
	 * model to the component.
	 * <p>
	 * This feature purpose is to prevent to show a progress bar for a very very short time.<br>
	 * This {@link _BusyLayerUI} wait few times (300ms by default) and decide to popup the progress bar or not.
	 * <p>
	 * The decision was made by computing a predicted remaining time of the underlying task.<br>
	 * If the remaining time is long enough (>= 1200ms by default), the progress bar will shown.
	 * <p>
	 * When the model gone busy, the component is instantly locked (can't be accessed anymore) even if the progress bar
	 * is not yet visible.
	 * <p>
	 * Getting a 0 value or any negative value indicate a disabled feature.<br>
	 * In this case, the progress bar will popup instantly when the model become busy.
	 * 
	 * @see #setMillisToDecideToPopup
	 */
	public int getMillisToDecideToPopup() {
		return millisToDecideToPopup;
	}

	/**
	 * Specifies the amount of remaining time required when this object take it's decision.
	 * <p>
	 * After {@link #getMillisToDecideToPopup()}, this layer compute the remaining time's job,<br>
	 * If its long enough regarding this property, the progress bar will be shown.
	 * <p>
	 * This feature purpose is to prevent to show a progress bar for a very very short time.<br>
	 * This {@link _BusyLayerUI} wait few times (300ms by default) and decide to popup the progress bar or not.
	 * <p>
	 * The decision was made by computing a predicted remaining time of the underlying task.<br>
	 * If the remaining time is long enough (>= 1200ms by default), the progress bar will shown.
	 * <p>
	 * When the model gone busy, the component is instantly locked (can't be accessed anymore) even if the progress bar
	 * is not yet visible.
	 * <p>
	 * Setting a 0 value or any negative value disable this feature. In this case, the progress bar will popup instantly
	 * when the model become busy.
	 * 
	 * @param millisToPopup an int specifying the time in milliseconds
	 * @see #getMillisToPopup
	 */
	public void setMillisToPopup(int millisToPopup) {
		this.millisToPopup = millisToPopup;
	}

	/**
	 * Returns the amount of remaining time required when this object take it's decision.
	 * <p>
	 * After {@link #getMillisToDecideToPopup()}, this layer compute the remaining time's job,<br>
	 * If its long enough regarding this property, the progress bar will be shown.
	 * <p>
	 * This feature purpose is to prevent to show a progress bar for a very very short time.<br>
	 * This {@link _BusyLayerUI} wait few times (300ms by default) and decide to popup the progress bar or not.
	 * <p>
	 * The decision was made by computing a predicted remaining time of the underlying task.<br>
	 * If the remaining time is long enough (>= 1200ms by default), the progress bar will shown.
	 * <p>
	 * When the model gone busy, the component is instantly locked (can't be accessed anymore) even if the progress bar
	 * is not yet visible.
	 * <p>
	 * Getting a 0 value or any negative value indicate a disabled feature.<br>
	 * In this case, the progress bar will popup instantly when the model become busy.
	 * 
	 * @see #setMillisToPopup
	 */
	public int getMillisToPopup() {
		return millisToPopup;
	}

	/**
	 * Define if this {@link BusyLayerUI} should show the remaining time of the job underlying the busy state.
	 * <p>
	 * This feature works only with determinate {@link BusyModel}
	 * 
	 * @param value set to <code>true</code> to show the remaining time when a determinate model is busy
	 * @see #isRemainingTimeVisible()
	 */
	public void setRemainingTimeVisible(boolean value) {
		remainingTimeVisible = value;
	}

	/**
	 * Indicate if this {@link _BusyLayerUI} should show the remaining time of the job underlying the busy state
	 * <p>
	 * This feature works only with determinate {@link BusyModel}
	 * 
	 * @return <code>true</code> of the remaining time should by shown
	 */
	public boolean isRemainingTimeVisible() {
		return remainingTimeVisible;
	}

	private final String getRemainingTimeString() {
		if (getBusyModel().isDeterminate() && monitor != null)
			return formatTime(monitor.getRemainingTime(TimeUnit.SECONDS));
		return null;
	}

	private final String getPercentValueString() {
		if (getBusyModel().isDeterminate() && monitor != null)
			return Integer.toString((int)getBusyModel().getPercentValue()) + " %";
		return null;
	}

	private static String formatTime(long time) {
		if (time <= 0)
			return null;
		if (time == Long.MAX_VALUE)
			return "Remaining time: \u221E";

		Formatter fmt = new Formatter();

		if (time < 60)
			return fmt.format("Remaining time: %ds", time).toString();
		if (time < 3600)
			return fmt.format("Remaining time: %dm %02ds", time / 60, time % 60).toString();

		return fmt.format("Remaining time: %dh %02dm %02ds", time / 3600, time / 60, time % 60).toString();
	}

	/**
	 * Indicate if the component should be busy.
	 * <p>
	 * If you use {@link #getMillisToDecideToPopup()} and {@link #getMillisToPopup()}, the component will not be busy
	 * instantly when the model is. This layer will take time to predict the remaining time and decide if the component
	 * would be busy or not.
	 * 
	 * @return <code>true</code> if the component would be busy
	 */
	protected boolean isComponentBusy() {
		boolean busy = model.isBusy();
		boolean determinate = busy && model.isDeterminate();
		boolean triggerEnabled = millisToDecideToPopup > 0 && millisToPopup > 0;

		if (busy && determinate) {
			if (monitor == null && (triggerEnabled || remainingTimeVisible))
				monitor = new RemainingTimeMonitor(model);

			// if the component is already busy, we will let it busy until the end
			if (panel.isVisible())
				return true;

			if (triggerEnabled) {
				if (monitor.getWorkingTime() < getMillisToDecideToPopup())
					return false;
				
				long remainingTime = monitor.getRemainingTime();
				return remainingTime < 0 || remainingTime > getMillisToPopup();
			}
		} else if (monitor != null) {
			monitor.dispose();
			monitor = null;
		}

		// if(!busy) {
		// if(alpha > 0)
		// return true;
		// }

		return busy;
	}

	/**
	 * Overridable method that need to create a painter with a specified alpha level. <code>BasicBusyLayerUI</code>
	 * invoke this method each time requested for paint the shadowing animation.
	 * 
	 * @param alpha The alpha value (0 ~ 255) requested for the painter
	 * @return painter the new painter with the correct alpha value
	 */
	protected LayoutPainter<JPanel> updatePainter(LayoutPainter<JPanel> painter, int alpha) {
		if (alpha >= 0xFF)
			return painter;
		if (alpha <= 0)
			return EmptyPainter.create();

		if (painter == EmptyPainter.<JPanel> create())
			painter = new MattePainter<JPanel>();

		painter.setFillPaint(ColorUtils.getColor(veilColor, alpha));
		setDirty(true);

		return painter;
	}

	/**
	 * Manage the background shading by starting the dedicated timer if needed. This method can only start the timer,
	 * never it stop it.
	 * <p>
	 * For start a timer, the background painter must be dirty (shading not completed) and the timer must not already
	 * running.
	 * <p>
	 * If no shading is requested (shadeDelayTotal <= 0 ) then the background is updated directly by this method without
	 * using any timer)
	 */
	private synchronized void manageBackgroundVeil(boolean busy) {
		if (isBackgroundPainterDirty(busy) && shadeDelay <= 0)
			updatePainter(busy);
	}

	/**
	 * Indicate if the background painter is dirty. This method consider the painter as dirty along the shading is not
	 * completed. If no veil is request by this UI, this method return <code>false</code>
	 * <p>
	 * The shading is considered as completed when - the painter is opaque and busy (opaque is relative from the
	 * veilAlpha) - the painter is translucent and not busy If it's the case, a new painter must be created and this
	 * layer UI should be repainted with it
	 */
	private synchronized boolean isBackgroundPainterDirty(boolean busy) {
		if (veilAlpha == 0f)
			return false;
		if (busy && alpha < 255)
			return true;
		if (!busy && alpha > 0)
			return true;
		return false;
	}

	/**
	 * Update the painter for paint the next step of the shading.
	 * <p>
	 * This method request an updateUI() if a new painter is created. The method update the alpha of the white veil
	 * depending <code>shadeDelayTotal</code> delay and <code>shadeDelayInterval</code> delay
	 * 
	 * @return <code>true</code> when this method request an {@link #update()} call for refresh ui state
	 */
	private synchronized boolean updatePainter(boolean busy) {
		final LayoutPainter<JPanel> old = painter;

		if (busy && (alpha < 0xFF))
			painter = updatePainter(painter, getShadeAlpha(busy));
		else if (!busy && (alpha > 0))
			painter = updatePainter(painter, getShadeAlpha(busy));

		return old != this.painter;
	}

	private int getShadeAlpha(boolean busy) {
		if (shadeTime == 0)
			shadeTime = System.currentTimeMillis();

		long delta = System.currentTimeMillis() - shadeTime;
		int ratio = (int)(delta * 1000) / shadeDelay;

		if (ratio > 1000) {
			alpha = busy ? 0xFF : 0;
			shadeTime = 0;
		} else {
			alpha = (0xFF * ratio) / 1000;
			alpha = busy ? alpha : (0xFF - alpha);
		}

		return (alpha * veilAlpha) / 100;
	}

	private synchronized void onTimer() {
		boolean busy = isComponentBusy();

		if (updatePainter(busy))
			update();
		else if (!repainted.get()) {
			update();
			repainted.set(false); // the timer is on the EDT, the updateUI is really done
		}

		if (!getBusyModel().isBusy() && !isBackgroundPainterDirty(busy))
			timer.stop();
	}

	private void onCancel() {
		try {
			getBusyModel().cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ========== BusyLayerUI ==========

	/**
	 * Indicate if this layer should be placed in a locked state. This default implementation return <code>true</code>
	 * if the model is "busy" OR the background animation is not ended.
	 * <p>
	 * Whenever the component is not yet busy because we wait some times to decide if progression would popup, the
	 * component will be locked instantly (when model is busy) anyway to prevent any access during the job.
	 */
	@Override
	protected boolean shouldLock() {
		return isBackgroundPainterDirty(isComponentBusy()) ? true : super.shouldLock();
	}

	@Override
	public void setBusyModel(BusyModel busyModel) {
		super.setBusyModel(busyModel);

		progressBar.setModel(busyModel);
		busyIcon.setModel(busyModel);
	}

	@Override
	protected void update() {
		BusyModel busyModel = getBusyModel();
		boolean busy = isComponentBusy();

		// Ensure the timer is running when the model is busy
		if (busyModel.isBusy() && !timer.isRunning())
			timer.start();

		repainted.set(true);
		panel.setVisible(busy);
		label.setVisible(busy);
		progressBar.setVisible(busy && busyModel.isDeterminate() && !busyIcon.isDeterminate());
		cancelButton.setVisible(busy && busyModel.isCancellable());

		manageBackgroundVeil(busy);

		// If cancellable, update it's border regarding the progress bar visible state
		if (busy) {
			String description = busyModel.getDescription();
			String str = remainingTimeVisible ? getRemainingTimeString() : getPercentValueString();

			label.setText((description != null) ? description : str);
			progressBar.setString((description != null) ? str : null);
			progressBar.setStringPainted(description != null);
		}

		super.update();
	}

	// ========== AbstractLayerUI ==========

	@Override
	protected void paintLayer(Graphics2D g2d, JXLayer<? extends JComponent> layer) {
		super.paintLayer(g2d, layer);
		painter.paint(g2d, null, layer.getWidth(), layer.getHeight());
	}

	// ========== ComponentUI ==========

	@Override
	public void installUI(JComponent ui) {
		super.installUI(ui);
		((JXLayer<?>)ui).setGlassPane(panel);
	}

	@Override
	public void uninstallUI(JComponent ui) {
		super.uninstallUI(ui);
		((JXLayer<?>)ui).setGlassPane(null);
	}

	// ========== ActionListener ==========

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton)
			onCancel();
		else if (e.getSource() == timer)
			onTimer();
	}

	// ========== static ==========

	private static GridBagConstraints updateGridBagConstraints(GridBagConstraints gbc, int insetTop) {
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets.top = insetTop;
		return gbc;
	}

	private static JLabel createLabel() {
		JLabel label = new JLabel();

		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.BOTTOM);

		return label;
	}

	private static JPanel createPanel(JLabel label, JProgressBar progressBar, Hyperlink cancelButton) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		panel.setOpaque(false);
		panel.add(label, updateGridBagConstraints(gbc, 0));
		panel.add(progressBar, updateGridBagConstraints(gbc, 2));
		panel.add(cancelButton, updateGridBagConstraints(gbc, 0));

		return panel;
	}
}
