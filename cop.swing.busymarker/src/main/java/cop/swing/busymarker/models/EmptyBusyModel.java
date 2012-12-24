package cop.swing.busymarker.models;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public final class EmptyBusyModel extends DefaultBusyModel {
	private static final long serialVersionUID = -8946832060258079115L;

	private static final EmptyBusyModel INSTANCE = new EmptyBusyModel();

	public static EmptyBusyModel getInstance() {
		return INSTANCE;
	}

	private EmptyBusyModel() {}
}
