package cop.swing.busymarker.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import cop.swing.utils.ColorUtils;

/**
 * @author Oleg Cherednik
 * @since 01.10.2012
 */
public class WindowsClassicBusyPaneUI extends BasicBusyPaneUI {
	private static BasicBusyPaneUI instance;

	public WindowsClassicBusyPaneUI() {
		this(false);
	}

	private WindowsClassicBusyPaneUI(boolean init) {
		if (init)
			init();
	}

	// ========== BasicBusyPaneUI ==========

	@Override
	protected void init() {
		put(BP_COLOR_BACKGROUND, ColorUtils.getColor(240, 240, 240));
		put(BP_COLOR_FOREGROUND, ColorUtils.getColor(51, 153, 255));
		super.init();
	}

	// ========== static ==========

	public static ComponentUI createUI(JComponent obj) {
		return instance != null ? instance : (instance = new WindowsClassicBusyPaneUI(true));
	}
}
