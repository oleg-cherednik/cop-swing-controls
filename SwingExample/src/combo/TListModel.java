package combo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import combo.interfaces.DisableableItem;
import combo.interfaces.DisableableListItem;
import combo.interfaces.ListCellRendererProvider;
import combo.interfaces.ListSelectionProvider;
import combo.interfaces.NameProvider;

public class TListModel<T> extends AbstractListModel implements MutableComboBoxModel, ListCellRendererProvider<T>,
                ListSelectionProvider, DisableableListItem, DisableableItem<T>, ListDataListener
{
	private static final long serialVersionUID = -6483739679078841370L;

	protected static final int INVALID_INDEX = -1;
	protected static final String NULL_NAME = " ";
	protected final List<ListNode<T>> nodes = getModelNodesContainer();

	private final ListNode<T> EMPTY_LIST_NODE = createNode(null);

	private boolean emptyValueEnabled;
	private final NameProvider<T> nameProvider;
	private int selectedItemIndex = INVALID_INDEX;

	public TListModel(NameProvider<T> nameProvider)
	{
		this.nameProvider = nameProvider;
		listenerList.add(ListDataListener.class, this);
	}

	protected List<ListNode<T>> getModelNodesContainer()
	{
		return new ArrayList<ListNode<T>>();
	}

	public void setEmptyValueEnabled(boolean emptyValueEnabled)
	{
		if(this.emptyValueEnabled == emptyValueEnabled)
			return;

		this.emptyValueEnabled = emptyValueEnabled;

		if(nodes.isEmpty())
			return;

		if(emptyValueEnabled)
			nodes.add(0, EMPTY_LIST_NODE);
		else
			nodes.remove(EMPTY_LIST_NODE);
	}

	/**
	 * Returns the component at the specified index.
	 * 
	 * @param index an index into this list
	 * @return the component at the specified index
	 * @exception ArrayIndexOutOfBoundsException if the <code>index</code> is negative or greater than the current size
	 *                of this list
	 * @see List#get(int)
	 */
	public T get(int index)
	{
		return nodes.get(index).getItem();
	}

	/**
	 * Tests whether this list has any components.
	 * 
	 * @return <code>true</code> if and only if this list has no components, that is, its size is zero;
	 *         <code>false</code> otherwise
	 * @see List#isEmpty()
	 */
	public boolean isEmpty()
	{
		return nodes.isEmpty();
	}

	/**
	 * Searches for the first occurrence of <code>item</code>.
	 * 
	 * @param item an object
	 * @return the index of the first occurrence of the argument in this list; returns <code>-1</code> if the object is
	 *         not found
	 * @see List#indexOf(Object)
	 */
	public int indexOf(T item)
	{
		int i = 0;

		for(ListNode<T> node : nodes)
		{
			if((item != null) ? item.equals(node.getItem()) : (node.getItem() == null))
				return i;
			else
				i++;
		}

		return INVALID_INDEX;
	}

	/**
	 * Returns the index of the last occurrence of <code>item</code>.
	 * 
	 * @param item the desired component
	 * @return the index of the last occurrence of <code>item</code> in the list; returns <code>-1</code> if the object
	 *         is not found
	 * @see List#lastIndexOf(Object)
	 */
	public int lastIndexOf(T item)
	{
		for(int index = nodes.size() - 1; index >= 0; index--)
		{
			ListNode<T> node = nodes.get(index);

			if((item != null) ? item.equals(node.getItem()) : (node.getItem() == null))
				return index;
		}

		return INVALID_INDEX;
	}

	/**
	 * Sets the component at the specified <code>index</code> of this list to be the specified object. The previous
	 * component at that position is discarded.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is invalid.
	 * 
	 * @param index the specified index
	 * @param item what the component is to be set to
	 * @see List#set(int, Object)
	 */
	public void set(int index, T item)
	{
		addEmptyValue();
		nodes.set(index, createNode(item));
		fireContentsChanged(this, index, index);
	}

	/**
	 * Deletes the component at the specified index.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is invalid.
	 * 
	 * @param index the index of the object to remove
	 * @see #remove(int)
	 * @see List#remove(int)
	 */
	public void remove(int index)
	{
		nodes.remove(index);
		removeEmptyValue();
		fireIntervalRemoved(this, index, index);
	}

	/**
	 * Adds the specified component to the end of this list.
	 * 
	 * @param item the component to be added
	 * @see List#add(Object)
	 */
	public void add(T item)
	{
		if(item == null)
			return;

		int index = nodes.size();

		addEmptyValue();
		nodes.add(createNode(item));
		fireIntervalAdded(this, index, index);
	}

	private void addEmptyValue()
	{
		if(emptyValueEnabled && nodes.isEmpty())
			nodes.add(EMPTY_LIST_NODE);
	}

	private void removeEmptyValue()
	{
		if(nodes.size() == 1 && nodes.get(0) == EMPTY_LIST_NODE)
			nodes.remove(EMPTY_LIST_NODE);
	}

	/**
	 * Removes the first (lowest-indexed) occurrence of the argument from this list.
	 * 
	 * @param item the component to be removed
	 * @return <code>true</code> if the argument was a component of this list; <code>false</code> otherwise
	 * @see List#remove(Object)
	 */
	public boolean remove(T item)
	{
		int index = indexOf(item);

		if(index >= 0)
		{
			nodes.remove(index);
			removeEmptyValue();
			fireIntervalRemoved(this, index, index);
			return true;
		}

		return false;
	}

	/**
	 * Inserts the specified object as a component in this list at the specified <code>index</code>.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is invalid.
	 * 
	 * @param index where to insert the new component
	 * @param item the component to insert
	 * @exception ArrayIndexOutOfBoundsException if the index was invalid
	 * @see List#add(int, Object)
	 */
	public void add(int index, T item)
	{
		addEmptyValue();
		nodes.add(index, createNode(item));
		fireIntervalAdded(this, index, index);
	}

	/**
	 * Removes all components from this list and sets its size to zero.
	 * 
	 * @see List#clear()
	 */
	public void clear()
	{
		if(nodes.isEmpty())
			return;

		int index1 = nodes.size() - 1;

		nodes.clear();
		fireIntervalRemoved(this, 0, index1);
	}

	public int getSelectedItemIndex()
	{
		return selectedItemIndex;
	}

	protected ListNode<T> createNode(T item)
	{
		return new ListNode<T>(item);
	}

	public void setSelectedIndex(int index)
	{
		if(isIndexValid(nodes, index))
			selectedItemIndex = index;
	}

	public boolean isSelectedItemEnabled()
	{
		return nodes.get(selectedItemIndex).isEnabled();
	}

	/*
	 * ComboBoxModel
	 */

	@Override
	public void setSelectedItem(Object item)
	{
		selectedItemIndex = indexOf((T)item);
	}

	@Override
	public Object getSelectedItem()
	{
		return isIndexValid(nodes, selectedItemIndex) ? nodes.get(selectedItemIndex).getItem() : null;
	}

	/*
	 * MutableComboBoxModel
	 */

	@Override
	public void addElement(Object obj)
	{
		add((T)obj);
	}

	@Override
	public void removeElement(Object obj)
	{
		remove((T)obj);
	}

	@Override
	public void insertElementAt(Object obj, int index)
	{
		add(index, (T)obj);
	}

	@Override
	public void removeElementAt(int index)
	{
		remove(index);
	}

	/*
	 * ListCellRendererDelegate
	 */

	@Override
	public void renderComponent(DefaultListCellRenderer cellRenderer, JList obj, T value, int index,
	                boolean isSelected, boolean cellHasFocus)
	{
		cellRenderer.setText(getValueText(value));

		if(selectedItemIndex == INVALID_INDEX || (index == INVALID_INDEX && !isSelected))
			return;

		ListNode<T> node = nodes.get((index != INVALID_INDEX) ? index : selectedItemIndex);

		cellRenderer.setEnabled(node.isEnabled());
		cellRenderer.setToolTipText(node.getToolTipText());
	}

	private String getValueText(T value)
	{
		if(value == null)
			return NULL_NAME;

		return (nameProvider != null) ? nameProvider.getName(value) : value.toString();
	}

	/*
	 * Object
	 */

	/**
	 * Returns a string that displays and identifies this object's properties.
	 * 
	 * @return a String representation of this object
	 */
	public String toString()
	{
		return nodes.toString();
	}

	/*
	 * ListModel
	 */

	/**
	 * Returns the number of components in this list.
	 * <p>
	 * This method is identical to <code>size</code>, which implements the <code>List</code> interface defined in the
	 * 1.2 Collections framework. This method exists in conjunction with <code>setSize</code> so that <code>size</code>
	 * is identifiable as a JavaBean property.
	 * 
	 * @return the number of components in this list
	 */
	public int getSize()
	{
		return nodes.size();
	}

	/**
	 * Returns the component at the specified index.
	 * 
	 * @param index an index into this list
	 * @return the component at the specified index
	 * @exception ArrayIndexOutOfBoundsException if the <code>index</code> is negative or greater than the current size
	 *                of this list
	 */
	public T getElementAt(int index)
	{
		return nodes.get(index).getItem();
	}

	/*
	 * ListSelectionProvider
	 */

	public boolean isItemSelectable(int index)
	{
		return isItemEnabled(index);
	}

	/*
	 * DisableableListItem
	 */

	public boolean isItemEnabled(int index)
	{
		return isIndexValid(nodes, index) ? nodes.get(index).isEnabled() : ListNode.DEF_ENABLE_STATUS;
	}

	public void setItemEnabled(int index, boolean enabled)
	{
		setItemEnabled(index, enabled, null);
	}

	public void setItemEnabled(int index, boolean enabled, String toolTipText)
	{
		if(!isIndexValid(nodes, index))
			return;

		nodes.get(index).setEnabled(enabled, toolTipText);
	}

	/*
	 * DisableableItem
	 */

	public boolean isItemEnabled(T item)
	{
		if(item == null)
			return ListNode.DEF_ENABLE_STATUS;

		for(ListNode<T> node : nodes)
			if(node.getItem().equals(item) && !node.isEnabled())
				return false;

		return true;
	}

	public void setItemEnabled(T item, boolean enabled)
	{
		setItemEnabled(item, enabled, null);
	}

	public void setItemEnabled(T item, boolean enabled, String toolTipText)
	{
		if(item == null)
			return;

		for(ListNode<T> node : nodes)
			if(node.getItem().equals(item))
				node.setEnabled(enabled, toolTipText);
	}

	/*
	 * ListDataListener
	 */

	public void intervalAdded(ListDataEvent e)
	{
		if(selectedItemIndex == INVALID_INDEX && !nodes.isEmpty())
			selectedItemIndex = 0;
	}

	public void intervalRemoved(ListDataEvent e)
	{
		if(nodes.isEmpty())
			selectedItemIndex = INVALID_INDEX;
	}

	public void contentsChanged(ListDataEvent e)
	{}

	/*
	 * static
	 */

	protected static boolean isIndexValid(List<?> collection, int index)
	{
		int size = collection.size();
		return (size > 0) && (index >= 0) && (index < size);
	}
}
