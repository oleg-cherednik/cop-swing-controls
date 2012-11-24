package combo.decorators;

import javax.swing.DefaultListSelectionModel;

import combo.interfaces.ListSelectionProvider;

public final class ListSelectionProviderDecorator extends DefaultListSelectionModel
{
	private static final long serialVersionUID = 6863520785495026433L;
	private final ListSelectionProvider selectionProvider;

	public ListSelectionProviderDecorator(ListSelectionProvider selectionProvider)
	{
		this.selectionProvider = selectionProvider;
	}

	/*
	 * DefaultListSelectionModel
	 */

	@Override
	public void setSelectionInterval(int index0, int index1)
	{
		for(int index = index0; index <= index1; index++)
			if(selectionProvider.isItemSelectable(index))
				super.setSelectionInterval(index, index);
	}
}
