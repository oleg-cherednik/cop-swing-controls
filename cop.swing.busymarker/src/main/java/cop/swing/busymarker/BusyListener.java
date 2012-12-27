package cop.swing.busymarker;

import java.util.EventListener;

import cop.swing.busymarker.icons.BusyIcon;

/**
 * @author Oleg Cherednik
 * @since 26.12.2012
 */
public interface BusyListener extends EventListener {
	void onBusyUpdate(BusyIcon icon);
}
