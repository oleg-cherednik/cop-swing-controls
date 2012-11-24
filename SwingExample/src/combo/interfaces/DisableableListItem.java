package combo.interfaces;

public interface DisableableListItem
{
	boolean isItemEnabled(int index);

	void setItemEnabled(int index, boolean enabled);

	void setItemEnabled(int index, boolean enabled, String toolTipText);
}
