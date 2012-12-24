package cop.swing.busymarker.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import cop.swing.utils.ColorUtils;

/**
 * @author Oleg Cherednik
 * @since 01.10.2012
 */
public class WindowsBusyPaneUI extends BasicBusyPaneUI {
	private static BasicBusyPaneUI instance;

	public WindowsBusyPaneUI() {
		this(false);
	}

	private WindowsBusyPaneUI(boolean init) {
		if (init)
			init();
	}

	// ========== BasicBusyPaneUI ==========

	@Override
	protected void init() {
		put(BP_COLOR_BACKGROUND, ColorUtils.getColor(209, 209, 209));
		put(BP_COLOR_FOREGROUND, ColorUtils.getColor(5, 214, 42));
		super.init();
	}

	// ========== static ==========

	public static ComponentUI createUI(JComponent obj) {
		return instance != null ? instance : (instance = new WindowsBusyPaneUI(true));
	}
}
