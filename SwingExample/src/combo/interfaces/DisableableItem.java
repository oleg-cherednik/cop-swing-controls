package combo.interfaces;

public interface DisableableItem<T>
{
	boolean isItemEnabled(T item);

	void setItemEnabled(T item, boolean enabled);

	void setItemEnabled(T item, boolean enabled, String toolTipText);
}
