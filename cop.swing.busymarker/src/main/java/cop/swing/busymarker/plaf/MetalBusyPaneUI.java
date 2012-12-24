package cop.swing.busymarker.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import cop.swing.utils.ColorUtils;

/**
 * @author Oleg Cherednik
 * @since 01.10.2012
 */
public class MetalBusyPaneUI extends BasicBusyPaneUI {
	private static BasicBusyPaneUI instance;

	public MetalBusyPaneUI() {
		this(false);
	}

	private MetalBusyPaneUI(boolean init) {
		if (init)
			init();
	}

	// ========== BasicBusyPaneUI ==========

	@Override
	protected void init() {
		put(BP_COLOR_BACKGROUND, ColorUtils.getColor(238, 238, 238));
		put(BP_COLOR_FOREGROUND, ColorUtils.getColor(163, 184, 204));
		super.init();
	}

	// ========== static ==========

	public static ComponentUI createUI(JComponent obj) {
		return instance != null ? instance : (instance = new MetalBusyPaneUI(true));
	}
}
