package directorychooser;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

public class DirNode extends DefaultMutableTreeNode
{
	private static FileSystemView fsv = DirectoryChooser.fsv;
	
	private static final boolean SHOW_HIDDEN = true;
	private boolean chlidrenLoaded = false;
	private int childrenCount = -1;
	private final boolean hasSubDirs = false;
	private static int total = 0;

	DirNode(File dir)
	{
		super(dir);
		// hasSubDirs = hasSubDirs();

		total++;

		// new SwingWorker() {
		// @Override
		// public Object construct()
		// {
		// populateChildren();
		// return null;
		// }
		//
		// }.start();

		// File file = getDir();
		// System.out.println(total + " - " + file.getAbsolutePath());

		// getChildCount();

		// if(file.isDirectory())
		// {
		// File[] files = file.listFiles();
		//
		// if(files != null)
		// {
		// int folders = 0;
		//
		// for(File file1 : files)
		// {
		// if(file1.isDirectory())
		// {
		// if(file1.isHidden())
		// {
		// if(SHOW_HIDDEN)
		// folders++;
		// }
		// else
		// folders++;
		// }
		// }
		//
		// childrenCount1 = folders;
		//
		// }
		// else
		// childrenCount1 = 0;
		// }
		// else
		// childrenCount1 = 0;
	}

	private boolean hasSubDirs()
	{
		File[] files = getDir().listFiles();

		if(files == null)
			return false;
		for(int k = 0; k < files.length; k++)
		{
			if(files[k].isDirectory())
				return true;
		}
		return false;
	}

	public File getDir()
	{
		return (File)userObject;
	}

	public boolean areChildrenLoaded()
	{
		return chlidrenLoaded;
	}

	@Override
	public int getChildCount()
	{
		if(!chlidrenLoaded)
		{
			populateChildren();
			childrenCount = super.getChildCount();
			chlidrenLoaded = true;
		}
		return childrenCount;
	}

	@Override
	public Enumeration children()
	{
		if(!chlidrenLoaded)
		{
			populateChildren();
			childrenCount = super.getChildCount();
			chlidrenLoaded = true;
		}
		return super.children();
	}

	@Override
	public boolean isLeaf()
	{
		return false;
	}

	private synchronized void populateChildren()
	{
		if(children == null)
		{
			File[] files = DirectoryChooser.fsv.getFiles(getDir(), !SHOW_HIDDEN);
			//Arrays.sort(files);
			for(int i = 0; i < files.length; i++)
			{
				File f = files[i];
				if(fsv.isTraversable(f).booleanValue())
				{
					insert(new DirNode(f), (children == null) ? 0 : children.size());
				}
			}
		}
	}

	@Override
	public String toString()
	{
		return fsv.getSystemDisplayName(getDir());
	}

	@Override
	public boolean equals(Object o)
	{
		return (o instanceof DirNode && userObject.equals(((DirNode)o).userObject));
	}
}
