package cop.swing.busymarker.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * @author Oleg Cherednik
 * @since 01.10.2012
 */
public class NimbusBusyPaneUI extends BasicBusyPaneUI {
	private static BasicBusyPaneUI instance;

	public NimbusBusyPaneUI() {
		this(false);
	}

	private NimbusBusyPaneUI(boolean init) {
		if (init)
			init();
	}

	// ========== BasicBusyPaneUI ==========

	@Override
	protected void init() {
//		put(COLOR_BACKGROUND, ColorUtils.getColor(220, 220, 220));
//		put(COLOR_FOREGROUND, ColorUtils.getColor(217, 127, 25));
		super.init();
	}

	// ========== static ==========

	public static ComponentUI createUI(JComponent obj) {
		return instance != null ? instance : (instance = new NimbusBusyPaneUI(true));
	}
}
