package directorychooser;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.TreePath;

public class NodeExpansionThread extends SwingWorker<DirNode, Object>
{
	private final DirectoryChooser chooser;
	TreePath treePath = null;

	public NodeExpansionThread(TreePath path, DirectoryChooser chooser)
	{
		this.treePath = path;
		this.chooser = chooser;
	}

	/*
	 * SwingWorker
	 */

	@Override
	public DirNode doInBackground()
	{
		DirNode node = (DirNode)treePath.getLastPathComponent();
		node.children();
		chooser.expandListener.nodeExpansionInProcess = false;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				chooser.expandPath(treePath);
			}
		});

		return node;
	}
}
