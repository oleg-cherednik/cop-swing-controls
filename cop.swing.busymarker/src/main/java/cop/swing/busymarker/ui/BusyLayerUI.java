package cop.swing.busymarker.ui;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;

import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.models.DefaultBusyModel;

/**
 * Abstract implementation of {@link LockableUI} for busy indicator. This implementation subclass {@link LockableUI} for
 * protecting the view across any access during the <code>busy</code> state.
 * 
 * @author Oleg Cherednik
 * @since 27.03.2012
 */
public abstract class BusyLayerUI extends LockableUI implements ChangeListener, Observer {
	private static final long serialVersionUID = -8168015295084189438L;

	protected BusyModel model = new DefaultBusyModel(this);
	private AtomicBoolean lastBusyState = new AtomicBoolean(false);

	public void setBusyModel(BusyModel model) {
		if (model == null || this.model == model)
			return;

		this.model.removeChangeListener(this);
		this.model = model;

		this.lastBusyState.set(this.model.isBusy());
		this.model.addChangeListener(this);

		update();
	}

	@SuppressWarnings("unchecked")
	public final <T extends BusyModel> T getBusyModel() {
		return (T)model;
	}

	/**
	 * Internal "update" of this UI. This method should update this layer ui from the BusyModel properties.
	 */
	protected void update() {
		setLocked(shouldLock());
		setDirty(true);
	}

	/**
	 * Indicate if this layer should be placed in a locked state. This default implementation lock the layer when the
	 * model is <code>busy</code>.
	 */
	protected boolean shouldLock() {
		return model.isBusy();
	}

	/*
	 * Observer
	 */

	public void update(Observable o, Object arg) {
		update();
	}

	/*
	 * LayerUI
	 */

	@Override
	public final void updateUI(JXLayer<? extends JComponent> layer) {
		update();
		super.updateUI(layer);
	}

	/*
	 * ChangeListener
	 */

	public void stateChanged(ChangeEvent event) {
		boolean busy = model.isBusy();

		if (lastBusyState.get() == busy)
			return;

		lastBusyState.set(busy);
		update();
	}
}
