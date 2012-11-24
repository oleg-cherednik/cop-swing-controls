package combo;

import java.lang.reflect.Field;

import javax.swing.ComboBoxModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;

import combo.decorators.ListCellRendererProviderDecorator;
import combo.decorators.ListSelectionProviderDecorator;
import combo.interfaces.DisableableItem;
import combo.interfaces.DisableableListItem;
import combo.interfaces.ListCellRendererProvider;
import combo.interfaces.ListSelectionProvider;
import combo.interfaces.NameProvider;

public class TSmartComboBox<T> extends TComboBox<T> implements DisableableListItem, DisableableItem<T>
{
	private static final long serialVersionUID = -4267173986846245413L;
	private TListModel<T> dataModel;

	public TSmartComboBox(ListCellRendererProvider<T> renderer, ListSelectionProvider selectionModel)
	{
		this((NameProvider<T>)null);
		setRendererSelectionModel(renderer, selectionModel);
	}

	public TSmartComboBox(NameProvider<T> nameProvider)
	{
		super(nameProvider, new TListModel<T>(nameProvider));
		setRendererSelectionModel(dataModel, dataModel);
	}

	public TSmartComboBox(TListModel<T> dataModel, ListCellRendererProvider<T> renderer,
	                ListSelectionProvider selectionModel)
	{
		super(null, dataModel);

		this.dataModel = getModel();
		setRendererSelectionModel(renderer, selectionModel);
	}

	public void setEmptyValueEnabled(boolean emptyValueEnabled)
	{
		dataModel.setEmptyValueEnabled(emptyValueEnabled);
		repaint();
	}

	public void setRendererSelectionModel(ListCellRendererProvider<T> renderer, ListSelectionProvider selectionModel)
	{
		ListCellRendererProviderDecorator<T> rendererDecorator = new ListCellRendererProviderDecorator<T>(renderer);
		ListSelectionModel selectionModelDecorator = new ListSelectionProviderDecorator(selectionModel);

		if(setSelectionModel(selectionModelDecorator))
			super.setRenderer(rendererDecorator);
	}

	public boolean isSelectedItemEnabled()
	{
		return dataModel.isSelectedItemEnabled();
	}

	private boolean setSelectionModel(ListSelectionModel selectionModel)
	{
		ComboBoxUI ui = (ComboBoxUI)getUI();

		if(!(ui instanceof BasicComboBoxUI))
			return false;

		try
		{
			Class<?> clazz = ui.getClass();

			while(!clazz.getSimpleName().equals(BasicComboBoxUI.class.getSimpleName()))
			{
				clazz = clazz.getSuperclass();
			}

			Field field = clazz.getDeclaredField("listBox");
			field.setAccessible(true);

			JList list = (JList)field.get(ui);

			if(list == null)
				return false;

			list.setSelectionModel(selectionModel);
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public void setModel(TListModel<T> dataModel)
	{
		super.setModel(dataModel);
		this.dataModel = dataModel;
	}

	/*
	 * JComboBox
	 */

	@Override
	@Deprecated
	public void setRenderer(ListCellRenderer renderer)
	{
		super.setRenderer(renderer);
	}

	@Override
	@Deprecated
	public void setModel(ComboBoxModel dataModel)
	{
		if(!(dataModel instanceof TListModel<?>))
			return;

		super.setModel(dataModel);
		this.dataModel = (TListModel<T>)super.getModel();
	}

	@Override
	public TListModel<T> getModel()
	{
		return dataModel;
	}

	@Override
	public int getSelectedIndex()
	{
		return dataModel.getSelectedItemIndex();
	}

	@Override
	public void setSelectedIndex(int index)
	{
		int selectedItemIndex = dataModel.getSelectedItemIndex();

		dataModel.setSelectedIndex(index);

		if(selectedItemIndex == dataModel.getSelectedItemIndex())
			return;

		if(selectedItemReminder != dataModel.getSelectedItem())
			selectedItemChanged();

		fireActionEvent();
	}

	/*
	 * DisableableListItem
	 */

	public boolean isItemEnabled(int index)
	{
		return dataModel.isItemEnabled(index);
	}

	public void setItemEnabled(int index, boolean enabled)
	{
		dataModel.setItemEnabled(index, enabled);
		repaint();
	}

	public void setItemEnabled(int index, boolean enabled, String toolTipText)
	{
		dataModel.setItemEnabled(index, enabled, toolTipText);
		repaint();
	}

	/*
	 * DisableableItem
	 */

	public boolean isItemEnabled(T item)
	{
		return dataModel.isItemEnabled(item);
	}

	public void setItemEnabled(T item, boolean enabled)
	{
		dataModel.setItemEnabled(item, enabled);
		repaint();
	}

	public void setItemEnabled(T item, boolean enabled, String toolTipText)
	{
		dataModel.setItemEnabled(item, enabled, toolTipText);
		repaint();
	}
}
