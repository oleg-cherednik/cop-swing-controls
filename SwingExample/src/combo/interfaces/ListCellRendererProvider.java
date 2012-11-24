package combo.interfaces;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public interface ListCellRendererProvider<T>
{
	void renderComponent(DefaultListCellRenderer cellRenderer, JList obj, T value, int index, boolean isSelected,
	                boolean cellHasFocus);
}
