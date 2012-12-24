package cop.swing.busymarker.plaf;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ProgressBarUI;

import cop.swing.busymarker.JBusyPane;

/**
 * @author Oleg Cherednik
 * @since 01.10.2012
 */
public abstract class BusyPaneUI extends ProgressBarUI {
	public static final String BP_COLOR_BACKGROUND = "JBusyPane.background";
	public static final String BP_COLOR_FOREGROUND = "JBusyPane.foreground";

	public static final String TOTAL_POINTS = "JBusyPane.total_points";
	public static final String TREIL_LENGTH = "JBusyPane.treil_length";
	public static final String WIDTH = "JBusyPane.height";
	public static final String HEIGHT = "JBusyPane.height";

	public static final String COLOR_VEIL = "JBusyPane.color_veil";
	public static final String SHADE_DELAY = "JBusyPane.shade_delay";
	public static final String VEIL_ALPHA = "JBusyPane.veil_alpha";
	
	public static final String BI_UNDETERMINATE_ADVANCE_LENGTH = "BusyIcon.undeterminate_advance_length"; 
	public static final String BI_DELAY = "BusyIcon.delay";

	// ========== static ==========

	public static void create() {
		getUI(null);
	}

	public static ComponentUI getUI(JBusyPane obj) {
		LookAndFeel laf = UIManager.getLookAndFeel();

		if (laf.getClass().getSimpleName().equals("MotifLookAndFeel"))
			return MotifBusyPaneUI.createUI(obj);
		if (laf.getClass().getSimpleName().equals("MetalLookAndFeel"))
			return MetalBusyPaneUI.createUI(obj);
		if (laf.getClass().getSimpleName().equals("NimbusLookAndFeel"))
			return NimbusBusyPaneUI.createUI(obj);
		if (laf.getClass().getSimpleName().equals("WindowsLookAndFeel"))
			return WindowsBusyPaneUI.createUI(obj);
		if (laf.getClass().getSimpleName().equals("WindowsClassicLookAndFeel"))
			return WindowsClassicBusyPaneUI.createUI(obj);

		return BasicBusyPaneUI.createUI(obj);
	}
}
