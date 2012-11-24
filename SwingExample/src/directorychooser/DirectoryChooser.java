package directorychooser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DirectoryChooser extends JTree implements TreeSelectionListener, MouseListener
{

	public static FileSystemView fsv = FileSystemView.getFileSystemView();

	private TreeSelectionModel treeSelectionModel = null;
	private javax.swing.Timer disableTreeSelcetionTimer = null;
	public final MyTreeWillExpandListener expandListener = new MyTreeWillExpandListener(this);

	/* --constructors */
	public DirectoryChooser()
	{
		this(null);
	}

	public DirectoryChooser(File dir)
	{
		super(new DirNode(fsv.getRoots()[0]));
		addTreeWillExpandListener(expandListener);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// setSelectedDirectory(dir);
		addTreeSelectionListener(this);
		addMouseListener(this);
	}

	public void enableTreeSelection()
	{
		if(treeSelectionModel != null)
		{
			setSelectionModel(treeSelectionModel);
			treeSelectionModel = null;
		}
	}

	public void disableTreeSelection()
	{
		disableTreeSelcetionTimer = new javax.swing.Timer(100, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if(expandListener.nodeExpansionInProcess && (treeSelectionModel == null))
				{
					treeSelectionModel = DirectoryChooser.this.getSelectionModel();

					DirectoryChooser.this.setSelectionModel(null);
				}
				else
				{

					DirectoryChooser.this.stopTimer();
				}
			}
		});
		disableTreeSelcetionTimer.start();
	}

	private void stopTimer()
	{
		if(disableTreeSelcetionTimer != null)
		{
			disableTreeSelcetionTimer.stop();
		}
	}

	/*--- Begin Public API -----*/

	public void setSelectedDirectory(File dir)
	{
		if(dir == null)
		{
			dir = fsv.getDefaultDirectory();
		}
		setSelectionPath(mkPath(dir));
	}

	public File getSelectedDirectory()
	{
		DirNode node = (DirNode)getLastSelectedPathComponent();
		if(node != null)
		{
			File dir = node.getDir();
			if(fsv.isFileSystem(dir))
			{
				return dir;
			}
		}
		return null;
	}

	public void addActionListener(ActionListener l)
	{
		listenerList.add(ActionListener.class, l);
	}

	public void removeActionListener(ActionListener l)
	{
		listenerList.remove(ActionListener.class, l);
	}

	public ActionListener[] getActionListeners()
	{
		return (ActionListener[])listenerList.getListeners(ActionListener.class);
	}

	/*--- End Public API -----*/

	/*--- TreeSelectionListener Interface -----*/

	public void valueChanged(TreeSelectionEvent ev)
	{
		File oldDir = null;
		TreePath oldPath = ev.getOldLeadSelectionPath();
		if(oldPath != null)
		{
			oldDir = ((DirNode)oldPath.getLastPathComponent()).getDir();
			if(!fsv.isFileSystem(oldDir))
			{
				oldDir = null;
			}
		}
		File newDir = getSelectedDirectory();
		firePropertyChange("selectedDirectory", oldDir, newDir);
	}

	/*--- MouseListener Interface -----*/

	public void mousePressed(MouseEvent e)
	{
		if(e.getClickCount() == 2)
		{
			TreePath path = getPathForLocation(e.getX(), e.getY());
			if(path != null && path.equals(getSelectionPath()) && getSelectedDirectory() != null)
			{

				fireActionPerformed("dirSelected", e);
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{}

	public void mouseClicked(MouseEvent e)
	{}

	public void mouseEntered(MouseEvent e)
	{}

	public void mouseExited(MouseEvent e)
	{}

	/*--- Private Section ------*/

	private TreePath mkPath(File dir)
	{
		DirNode root = (DirNode)getModel().getRoot();
		if(root.getDir().equals(dir))
		{
			return new TreePath(root);
		}

		TreePath parentPath = mkPath(fsv.getParentDirectory(dir));
		DirNode parentNode = (DirNode)parentPath.getLastPathComponent();
		Enumeration enumeration = parentNode.children();
		while(enumeration.hasMoreElements())
		{
			DirNode child = (DirNode)enumeration.nextElement();
			if(child.getDir().equals(dir))
			{
				return parentPath.pathByAddingChild(child);
			}
		}
		return null;
	}

	private void fireActionPerformed(String command, InputEvent evt)
	{
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command, evt.getWhen(), evt.getModifiers());
		ActionListener[] listeners = getActionListeners();
		for(int i = listeners.length - 1; i >= 0; i--)
		{
			listeners[i].actionPerformed(e);
		}
	}

	/*--- Main for testing  ---*/

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex)
		{}

		final JDialog dialog = new JDialog((JFrame)null, true);
		final DirectoryChooser dc = new DirectoryChooser();
		final JButton okButton = new JButton("OK");
		final JButton cancelButton = new JButton("Cancel");

		dialog.getContentPane().add(new JScrollPane(dc), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		ActionListener actionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object c = e.getSource();
				if(c == okButton || c == dc)
				{
					System.out.println("You selected: " + dc.getSelectedDirectory());
				}
				dialog.hide();
			}
		};

		dc.addActionListener(actionListener);
		okButton.addActionListener(actionListener);
		cancelButton.addActionListener(actionListener);

		dc.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent ev)
			{
				if(ev.getPropertyName().equals("selectedDirectory"))
				{
					okButton.setEnabled(dc.getSelectedDirectory() != null);
				}
			}
		});

		dialog.setBounds(200, 200, 300, 350);
		dc.scrollRowToVisible(Math.max(0, dc.getMinSelectionRow() - 4));
		dialog.show();
		System.exit(0);
	}
}
