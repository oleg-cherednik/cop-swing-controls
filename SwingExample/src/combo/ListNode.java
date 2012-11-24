package combo;

public class ListNode<T>
{
	public static final boolean DEF_ENABLE_STATUS = true;

	private final T item;
	private String toolTipText;
	private boolean enabled = DEF_ENABLE_STATUS;

	public ListNode(T item)
	{
		this.item = item;
	}

	public ListNode(T item, boolean enabled)
	{
		this.item = item;
		this.enabled = enabled;
	}

	public T getItem()
	{
		return item;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public String getToolTipText()
	{
		return toolTipText;
	}

	public void setToolTipText(String toolTipText)
	{
		this.toolTipText = toolTipText;
	}

	public void setEnabled(boolean enabled, String toolTipText)
	{
		this.enabled = enabled;
		this.toolTipText = toolTipText;
	}

	/*
	 * Object
	 */

	public String toString()
	{
		return "" + item + " - " + enabled;
	}
}
