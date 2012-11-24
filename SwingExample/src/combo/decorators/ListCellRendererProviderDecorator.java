package combo.decorators;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import combo.interfaces.ListCellRendererProvider;

public final class ListCellRendererProviderDecorator<T> extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 2078186015500229812L;
	private final ListCellRendererProvider<T> delegate;

	public ListCellRendererProviderDecorator(ListCellRendererProvider<T> delegate)
	{
		this.delegate = delegate;
	}

	/*
	 * DefaultListCellRenderer
	 */

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	                boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if(delegate == null)
			return this;

		if(!isSelected)
			isSelected = isPaintCurrentValueCallFrom();

		delegate.renderComponent(this, list, (T)value, index, isSelected, cellHasFocus);

		return this;
	}

	/*
	 * static
	 */

	public static boolean isPaintCurrentValueCallFrom()
	{
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

		for(int i = 0; i < 10; i++)
			if(stackTraceElements[i].getMethodName().equals("paintCurrentValue"))
				return true;

		return false;
	}
}
