package cop.swing.busymarker.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import cop.swing.utils.ColorUtils;

/**
 * @author Oleg Cherednik
 * @since 01.10.2012
 */
public class MotifBusyPaneUI extends BasicBusyPaneUI {
	private static BasicBusyPaneUI instance;

	public MotifBusyPaneUI() {
		this(false);
	}

	private MotifBusyPaneUI(boolean init) {
		if (init)
			init();
	}

	// ========== BasicBusyPaneUI ==========

	@Override
	protected void init() {
		put(BP_COLOR_BACKGROUND, ColorUtils.getColor(174, 178, 195));
		put(BP_COLOR_FOREGROUND, ColorUtils.getColor(147, 151, 165));
		super.init();
	}

	// ========== static ==========

	public static ComponentUI createUI(JComponent obj) {
		return instance != null ? instance : (instance = new MotifBusyPaneUI(true));
	}
}
