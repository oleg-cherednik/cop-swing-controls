package cop.swing.busymarker.models;

import java.awt.event.ActionEvent;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public enum BusyAction {
	START(0, "start"),
	STOP(1, "stop"),
	CANCEL(2, "cancel");

	private final int id;
	private final String command;

	BusyAction(int id, String command) {
		this.id = id;
		this.command = command.toLowerCase();
	}

	public final ActionEvent createEvent(Object source) {
		return new ActionEvent(source, id, command, System.currentTimeMillis(), 0);
	}

	public boolean checkEvent(ActionEvent event) {
		return (event != null) && (ordinal() == event.getID()) && name().equalsIgnoreCase(event.getActionCommand());
	}
}
