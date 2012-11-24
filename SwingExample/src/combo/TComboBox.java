package combo;


import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import combo.interfaces.NameProvider;
import combo.interfaces.NameProviderSupport;

public class TComboBox<T> extends JComboBox implements NameProviderSupport<T>
{
	private static final long serialVersionUID = 4741092467716937860L;
	private NameProvider<T> nameProvider;

	public TComboBox()
	{
		this((NameProvider<T>)null);
	}

	public TComboBox(NameProvider<T> nameProvider)
	{
		setNameProvider(nameProvider);
	}

	public TComboBox(ComboBoxModel model)
	{
		this(null, model);
	}

	public TComboBox(NameProvider<T> nameProvider, ComboBoxModel model)
	{
		super(model);
		setNameProvider(nameProvider);
	}

	public TComboBox(T[] items)
	{
		this(null, items);
	}

	public TComboBox(NameProvider<T> nameProvider, T[] items)
	{
		super(getItemsName(nameProvider, items));
		setNameProvider(nameProvider);
	}

	public T getSelectedItemKey()
	{
		return (T)super.getSelectedItem();
	}

	public String getSelectedItemName()
	{
		T item = getSelectedItemKey();

		if(item == null)
			return null;

		return (nameProvider != null) ? nameProvider.getName(item) : item.toString();
	}

	/*
	 * NameProviderSupport
	 */

	@Override
	public void setNameProvider(NameProvider<T> nameProvider)
	{
		NameProvider<T> oldNameProvider = this.nameProvider;
		this.nameProvider = nameProvider;

		if(nameProvider != oldNameProvider)
			firePropertyChange("nameProvider", oldNameProvider, nameProvider);
	}

	@Override
	public NameProvider<T> getNameProvider()
	{
		return nameProvider;
	}

	/*
	 * static
	 */

	protected static <T> Object[] getItemsName(NameProvider<T> nameProvider, T[] items)
	{
		if(items == null)
			return null;
		if(nameProvider == null)
			return items;

		String[] arr = new String[items.length];
		int i = 0;

		for(T item : items)
			arr[i++] = nameProvider.getName(item);

		return arr;
	}
}
