package cop.swing.busymarker.models;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public enum BusyState {
	READY,
	UNDETERMINATE,
	DETERMINATE;

	// ========== static ==========

	public static BusyState parseBusyState(boolean busy, boolean determinate) {
		int value = (busy ? 1 : 0) + (determinate ? 2 : 0);

		if (value == 3)
			return DETERMINATE;
		if (value == 1)
			return UNDETERMINATE;

		return READY;
	}
}
