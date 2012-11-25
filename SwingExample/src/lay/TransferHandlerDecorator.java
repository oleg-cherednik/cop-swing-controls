package lay;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

public class TransferHandlerDecorator extends TransferHandler {
	private final JPanel panel;

	public TransferHandlerDecorator(JPanel panel) {
		this.panel = panel;
	}

	public int getSourceActions(JComponent c) {
		return TransferHandler.NONE;
	}

	public boolean canImport(TransferSupport support) {
		try {
			return support.getTransferable().getTransferData(DataFlavor.stringFlavor) != null;
		} catch (Throwable e) {
			return false;
		}
	}

	public boolean importData(TransferHandler.TransferSupport info) {
		if (!info.isDrop())
			return false;
		
		try {
			String panelId = (String)info.getTransferable().getTransferData(DataFlavor.stringFlavor);
			Point point = info.getDropLocation().getDropPoint();

			if (panelId.equals(DndExample.panel1)) {
				panel.setLocation(point.x - DndExample.phase.width, point.y - DndExample.phase.height);
				panel.revalidate();
			}
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

}
