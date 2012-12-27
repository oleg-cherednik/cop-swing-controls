package cop.swing.busymarker.icons;

import java.awt.Component;
import java.awt.Graphics;

import cop.swing.busymarker.BusyListener;
import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.models.EmptyBusyModel;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public final class EmptyBusyIcon implements BusyIcon {
	public static final BusyIcon OBJ = new EmptyBusyIcon();

	private EmptyBusyIcon() {}

	// ========== BusyIcon ==========

	public void paintIcon(Component c, Graphics g, int x, int y) {}

	public int getIconWidth() {
		return 0;
	}

	public int getIconHeight() {
		return 0;
	}

	public void setModel(BusyModel model) {}

	public BusyModel getModel() {
		return EmptyBusyModel.getInstance();
	}

	public boolean isDeterminate() {
		return false;
	}

	public void addListener(BusyListener listener) {}

	public void removeListener(BusyListener listener) {}

	// ========== Object ==========

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
