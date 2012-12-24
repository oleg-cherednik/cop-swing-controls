package cop.swing.busymarker.plaf;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/**
 * @author Oleg Cherednik
 * @since 01.10.2012
 */
public class BasicBusyPaneUI extends BusyPaneUI {
	private static BasicBusyPaneUI instance;

	public BasicBusyPaneUI() {
		this(false);
	}

	private BasicBusyPaneUI(boolean init) {
		if (init)
			init();
	}

	@SuppressWarnings("static-method")
	protected void init() {
		put(BP_COLOR_BACKGROUND, Color.lightGray);
		put(BP_COLOR_FOREGROUND, Color.black);

		put(TOTAL_POINTS, 8);
		put(TREIL_LENGTH, 4);
		put(WIDTH, 26);
		put(HEIGHT, 26);

		put(COLOR_VEIL, Color.white);
		put(SHADE_DELAY, 400); // in ms
		put(VEIL_ALPHA, 85); // [0;100] in %

		put(BI_UNDETERMINATE_ADVANCE_LENGTH, 60);	// [0;360] in grad 
		put(BI_DELAY, 1500); // in ms
	}

	// ========== static ==========

	protected static void put(Object key, Object value) {
		if (UIManager.get(key) == null)
			UIManager.put(key, value);
	}

	public static ComponentUI createUI(JComponent obj) {
		return instance != null ? instance : (instance = new BasicBusyPaneUI(true));
	}
}
