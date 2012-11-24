package directorychooser;

import java.awt.Cursor;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

public class MyTreeWillExpandListener implements TreeWillExpandListener
{
	private final DirectoryChooser chooser;
	public boolean nodeExpansionInProcess = false;
	private NodeExpansionThread nodeExpansionThread = null;

	public MyTreeWillExpandListener(DirectoryChooser chooser)
	{
		this.chooser = chooser;
	}

	public void treeWillExpand(TreeExpansionEvent evt) throws ExpandVetoException
	{
		if(nodeExpansionInProcess)
		{
			throw new ExpandVetoException(evt);
		}
		DirectoryChooser tree = chooser;
		tree.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		TreePath path = evt.getPath();
		DirNode node = (DirNode)path.getLastPathComponent();
		boolean areChildrenLoaded = node.areChildrenLoaded();
		if(!areChildrenLoaded)
		{
			nodeExpansionThread = new NodeExpansionThread(path, chooser);
			nodeExpansionThread.execute();
			nodeExpansionInProcess = true;
			tree.disableTreeSelection();
			throw new ExpandVetoException(evt);
		}
		tree.enableTreeSelection();
		tree.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void treeWillCollapse(TreeExpansionEvent evt) throws ExpandVetoException
	{
		if(nodeExpansionInProcess)
		{
			throw new ExpandVetoException(evt);
		}
	}
}
