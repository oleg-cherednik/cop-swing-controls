package filetree;

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import directorychooser.DirectoryChooser;

class FileNode// extends DefaultMutableTreeNode
{
	private static final Icon ICON_DISK = UIManager.getIcon("FileView.hardDriveIcon");
	private static final Icon ICON_COMPUTER = UIManager.getIcon("FileView.hardDriveIcon");
	private static final Icon ICON_FOLDER = UIManager.getIcon("Tree.closedIcon");
	private static final Icon ICON_FOLDER_EXPANDED = UIManager.getIcon("Tree.openIcon");
	// public static final Icon ICON_NEW = UIManager.getIcon("FileChooser.newFolderIcon");
	
	

	public final FileSystemView fsv;
	private static final boolean SHOW_HIDDEN = false;
	private final File file;

	private Icon icon;
	private Icon expandedIcon;

	public FileNode(File file, FileSystemView fsv)
	{
		this.file = file;
		this.fsv = fsv;

		if(fsv.isDrive(file))
			icon = ICON_DISK;
		else if(fsv.isComputerNode(file))
			icon = ICON_COMPUTER;
		else if(file.isDirectory())
		{
			icon = ICON_FOLDER;
			expandedIcon = ICON_FOLDER_EXPANDED;
		}
	}

	public File getFile()
	{
		return file;
	}

	public boolean expand(DefaultMutableTreeNode parent)
	{
		DefaultMutableTreeNode flag = (DefaultMutableTreeNode)parent.getFirstChild();

		if(flag == null)
			return false;

		Object obj = flag.getUserObject();

		if(!(obj instanceof Boolean))
			return false;

		parent.removeAllChildren();

		DefaultMutableTreeNode node;
		FileNode fileNode;

		for(File file : DirectoryChooser.fsv.getFiles(this.file, !SHOW_HIDDEN))
		{
			if(!fsv.isTraversable(file).booleanValue())
				continue;
			if(!file.isDirectory())
				continue;
			if(isLink(file))
				continue;

			node = new DefaultMutableTreeNode(fileNode = new FileNode(file, fsv));
			parent.add(node);

			if(fileNode.hasSubDirs())
				node.add(new DefaultMutableTreeNode(true));
		}

		return true;
	}

	public Icon getIcon()
	{
		return icon;
	}

	public Icon getIcon(boolean expanded)
	{
		return expanded ? getExpandedIcon() : getIcon();
	}

	public Icon getExpandedIcon()
	{
		return (expandedIcon != null) ? expandedIcon : getIcon();
	}

	private boolean isLink(File file)
	{
		try
		{
			file.getAbsolutePath();
			file.getCanonicalPath();

			return !fsv.isFileSystem(file);
		}
		catch(IOException ex)
		{
			return false;
		}
	}

	public boolean hasSubDirs()
	{
		for(File file : DirectoryChooser.fsv.getFiles(this.file, !SHOW_HIDDEN))
			if(file.isDirectory() && (file.isHidden() ? SHOW_HIDDEN : true))
				return true;

		return false;
	}

	/*
	 * Object
	 */

	@Override
	public String toString()
	{
		return fsv.getSystemDisplayName(file);
	}
}
