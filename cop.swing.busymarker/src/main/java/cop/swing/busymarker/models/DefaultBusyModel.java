package cop.swing.busymarker.models;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Default Implementation of interface {@link BusyModel}.
 * <p>
 * It add <code>AutoCompletion</code> feature for determinate model. This feature allow to move the current value to the
 * minimum range when the busy property is set to <code>true</code>.<br>
 * At the other side, when the current value reach the maximum bounded range, it set automatically the busy property to
 * <code>false</code>.
 * 
 * @author Oleg Chrednik
 * @since 27.03.2012
 */
public class DefaultBusyModel extends DefaultBoundedRangeModel implements BusyModel {
	private static final long serialVersionUID = 8987614744693228601L;

	private boolean busy;
	private boolean determinateState;
	private boolean autoCompletionState;
	private boolean cancellableState;
	private String description;

	public DefaultBusyModel() {}

	public DefaultBusyModel(ChangeListener listener) {
		if (listener != null)
			addChangeListener(listener);
	}

	/**
	 * Define if the model is on a <b>busy</b> state. This method fire an {@link ActionEvent} of
	 * {@link BusyAction#START} or {@link BusyAction#STOP} following by a {@link ChangeEvent}
	 * 
	 * @param busy true to going in a busy state
	 */
	public void setBusy(boolean busy) {
		if (this.busy == busy)
			return;

		this.busy = busy;

		if (this.busy && determinateState && autoCompletionState)
			setValue(getMinimum());

		fireActionPerformed((this.busy ? BusyAction.START : BusyAction.STOP).createEvent(this));
		fireStateChanged();
	}

	/**
	 * Returns true if the model is currently on a <code>busy</code> state
	 * 
	 * @return true if the model is currently busy
	 */
	public final boolean isBusy() {
		return busy;
	}

	/**
	 * Define if the model is in a <code>determinate mode</code> or not
	 * 
	 * @param value true for change this model in a determinate mode
	 */
	public final void setDeterminate(boolean value) {
		if (this.determinateState == value)
			return;

		this.determinateState = value;
		fireStateChanged();
	}

	/**
	 * Returns true if the model is in a <code>determinate mode</code>.
	 * 
	 * @return true if the model is in a determinate mode.
	 */
	public final boolean isDeterminate() {
		return determinateState;
	}

	/**
	 * Define if the range value must manage the completion automatically. This property is significant only when this
	 * model is <code>determinate</code>. When the <code>busy</code> property is set to true the range
	 * <code>value</code> is set to the <code>minimum</code>. When the range <code>value</code> reach the
	 * <code>maximum</code>, the <code>busy</code> property is set to <code>false</code>.
	 */
	public void setAutoCompletionEnabled(boolean value) {
		if (this.autoCompletionState == value)
			return;

		this.autoCompletionState = value;
		fireStateChanged();
	}

	/**
	 * Returns <code>true</code> if the range value must manage the completion automatically. This property is
	 * significant only when this model is <code>determinate</code>. When the <code>busy</code> property is set to true
	 * the range <code>value</code> is set to the <code>minimum</code>. When the range <code>value</code> reach the
	 * <code>maximum</code>, the <code>busy</code> property is set to <code>false</code>.
	 */
	public final boolean isAutoCompletionEnabled() {
		return autoCompletionState;
	}

	/**
	 * Returns true if the model is <code>cancellable</code> the performing the job responsible on the <code>busy</code>
	 * state
	 * 
	 * @return true is the model is cancellable
	 */
	public boolean isCancellable() {
		return cancellableState;
	}

	/**
	 * Default implementation that simply stop the <code>busy</code> state
	 */
	public void cancel() {
		if (cancellableState)
			fireActionPerformed(BusyAction.CANCEL.createEvent(this));
	}

	/**
	 * Define if this model is <code>cancellable</code>
	 * 
	 * @param value true for set this model cancellable.
	 */
	public void setCancellable(boolean value) {
		if (this.cancellableState != value) {
			this.cancellableState = value;
			fireStateChanged();
		}
	}

	/**
	 * Description to show by UI when the model is busy Return null for let the UI render the native description
	 * 
	 * @return Description to show by UI when the model is busy
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Define the description to show by UI when the model is busy
	 * 
	 * @param description new description to show by UI, set null if you want to restore default value
	 */
	public void setDescription(String description) {
		if (description != null && description.trim().isEmpty())
			description = null;
		if (this.description == description || this.description != null && this.description.equals(description))
			return;

		this.description = description;
		fireStateChanged();
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The event instance is
	 * lazily created using the <code>event</code> parameter.
	 * 
	 * @param event the <code>ActionEvent</code> object
	 */
	protected final void fireActionPerformed(final ActionEvent event) {
		if (SwingUtilities.isEventDispatchThread())
			for (ActionListener listener : listenerList.getListeners(ActionListener.class))
				listener.actionPerformed(event);
		else
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					fireActionPerformed(event);
				}
			});
	}

	// ========== BoundedRangeModel ==========

	@Override
	public void setValue(int value) {
		super.setValue(value);

		if (determinateState && autoCompletionState && getValue() >= getMaximum())
			setBusy(false);
	}

	// ========== BusyModel ==========

	public int getRange() {
		return getMaximum() - getMinimum();
	}

	public int getExtValue() {
		return getValue() + getExtent();
	}

	public double getRatio() {
		return (double)getExtValue() / getRange();
	}

	public void addActionListener(ActionListener listener) {
		if (listener != null) {
			listenerList.remove(ActionListener.class, listener);
			listenerList.add(ActionListener.class, listener);
		}
	}

	public void removeActionListener(ActionListener listener) {
		if (listener != null)
			listenerList.remove(ActionListener.class, listener);
	}

	// [0;100]. -1 - undefined
	public double getPercentValue() {
		return !busy || !determinateState ? -1 : (100. / getRange()) * (getValue() - getMinimum());
	}

	// ========== DefaultBoundedRangeModel ==========

	@Override
	protected void fireStateChanged() {
		if (SwingUtilities.isEventDispatchThread())
			super.fireStateChanged();
		else
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					fireStateChanged();
				}
			});
	}

	// ========== Object ==========

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
