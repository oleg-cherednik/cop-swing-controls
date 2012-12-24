package cop.swing.busymarker.models;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;

/**
 * DataModel describes a <strong>busy</strong> state behavior. A busy state represents a disabled state (inaccessible)
 * for a while.<br>
 * This state is commonly bound to a swing component that can't be used while it's busy. Typically a pretty animation
 * will be show. <br>
 * When the model is gone to busy, it can be determinate that allow to track the progress and time remaining like a
 * {@link JProgressBar}. In fact, a {@link BusyModel} is a {@link BoundedRangeModel} that allow it to be bounded to a
 * {@link JProgressBar}. <br>
 * {@link BusyModel} can be cancellable to allow the controller of this model to cancel the underlying task.
 * 
 * @author Oleg Cherednik
 * @since 27.03.2012
 */
public interface BusyModel extends BoundedRangeModel {
	/**
	 * Define if the model is on a "busy" state
	 * 
	 * @param busy true to going in a busy state
	 */
	void setBusy(boolean busy);

	/**
	 * Returns true if the model is currently on a <code>busy</code> state
	 * 
	 * @return <code>true</code> if the model is currently busy
	 */
	boolean isBusy();

	/**
	 * Define if the model is in a <code>determinate mode</code> or not
	 * 
	 * @param value true for change this model in a determinate mode
	 */
	void setDeterminate(boolean value);

	/**
	 * Returns true if the model is in a <code>determinate mode</code>.
	 * 
	 * @return true if the model is in a determinate mode.
	 */
	boolean isDeterminate();

	/**
	 * Returns true if the model is <code>cancellable</code> the performing the job responsible on the <code>busy</code>
	 * state.
	 * 
	 * @return true is the model is cancellable
	 */
	boolean isCancellable();

	/**
	 * Define if this model is <code>cancellable</code>
	 * 
	 * @param value true for set this model cancellable.
	 */
	void setCancellable(boolean value);

	/**
	 * Invoke this method to cancel the current job responsible of the <code>busy</code> state. You need to override
	 * this method for implements you own cancellation process. Cancelling a task fire an {@link ActionEvent} to all
	 * registered {@link ActionListener} to this model.
	 */
	void cancel();

	/**
	 * Description to show by UI when the model is busy Return null for let the UI render the native description
	 * 
	 * @return Description to show by UI when the model is busy
	 */
	String getDescription();

	void setDescription(String description);

	/**
	 * Adds an <code>ActionListener</code> to the model.
	 * 
	 * @param listener the <code>ActionListener</code> to be added
	 */
	void addActionListener(ActionListener listener);

	/**
	 * Removes an <code>ActionListener</code> from the model.
	 * 
	 * @param listener the listener to be removed
	 */
	void removeActionListener(ActionListener listener);

	float getPercentValue();
}
