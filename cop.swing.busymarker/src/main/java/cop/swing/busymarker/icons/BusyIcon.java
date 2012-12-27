package cop.swing.busymarker.icons;

import java.util.Observable;

import javax.swing.BoundedRangeModel;
import javax.swing.Icon;

import cop.swing.busymarker.BusyListener;
import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.ui.BusyLockableUI;

/**
 * {@link BusyIcon} is a simple icon with <code>Busy</code> renderable capabilities.
 * <p>
 * Such icons draw a animations related to the current state of the underlying {@link BusyModel}.<br>
 * A {@link BusyIcon}> can be bound to a basic <code>BoundedRangeModel</code> or to a more featured {@link BusyModel}.
 * <p>
 * When a {@link BusyIcon} is bound to a {@link BusyModel}, this icon can render undeterminable state or idle state (not
 * busy).<br>
 * Otherwise, if an icon is bound to a simple {@link BoundedRangeModel}, this icon will be always considered in a
 * determinate state.
 * <p>
 * This interface don't provide any contract on how a {@link BusyIcon} should fire an event to the graphical interface
 * when it need to be repainted (when the <code>model</code> or <code>state</Code> change).<br>
 * <strong>But we strong recommand</strong> to support the {@link Observable} class for the best integration inside
 * {@link BusyLockableUI}.
 * 
 * @author Oleg Cherednik
 * @since 26.03.2012
 */
public interface BusyIcon extends Icon {
	void setModel(BusyModel model);

	BusyModel getModel();

	/**
	 * Returns <code>true</code> if this icon currently render a determinate state<br>
	 * Some implementation of BusyIcon don't support to render a determinate state whatever the current state of the
	 * underlying Model.<br>
	 * In theses cases, this method should indicate that this icon render an undeterminate state even if the BusyModel
	 * is on an determinate state.
	 * <p>
	 * If this icon can render all states (undeterminate/determinate), this method should be in sync with the underlying
	 * model.<br>
	 * In the other case, this asynchronous state can be used by {@link _BusyLayerUI} to determine if they need to add
	 * some externals informations (example a Progress Bar) in addition of the icon for track the progression advance of
	 * the BusyModel.
	 * 
	 * @return <code>true</code> if this icon currently render a determinate state
	 */
	boolean isDeterminate();

	void addListener(BusyListener listener);

	void removeListener(BusyListener listener);
}
