package filetree;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class FileTreePanel /*extends JPanel*/ implements TreeExpansionListener, TreeSelectionListener
{
	private static final long serialVersionUID = -8665278651198506594L;
	private static final Cursor WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
	private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private static final FileSystemView fsv = FileSystemView.getFileSystemView();

	protected JTree m_tree;
	protected JTabbedPane ff;
	protected DefaultTreeModel model;
	JTextField m_display;
	private final JFrame frame;

	public FileTreePanel(JFrame frame)
	{
		this.frame = frame;
		File[] files = fsv.getRoots();
		DefaultMutableTreeNode node;
		FileNode fileNode = new FileNode(files[0], fsv);
		node = new DefaultMutableTreeNode(fileNode);

		if(fileNode.hasSubDirs())
			node.add(new DefaultMutableTreeNode(true));

		fileNode.expand(node);

		model = new DefaultTreeModel(node);
		m_tree = new JTree(model);
		
		m_tree.putClientProperty("JTree.lineStyle", "Angled");

		TreeCellEditor ce = m_tree.getCellEditor();

		m_tree.setCellRenderer(new IconCellRenderer());
		m_tree.addTreeExpansionListener(this);
		m_tree.addTreeSelectionListener(this);
		m_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		m_tree.setShowsRootHandles(true);
		m_tree.setEditable(false);

		JScrollPane s = new JScrollPane();
		s.getViewport().add(m_tree);
		frame.getContentPane().add(s, BorderLayout.CENTER);

		m_display = new JTextField();
		m_display.setEditable(false);
		frame.getContentPane().add(m_display, BorderLayout.NORTH);

		WindowListener wndCloser = new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		};
		frame.addWindowListener(wndCloser);

		frame.setVisible(true);
	}
	
	private void init() {
		
	}

	private DefaultMutableTreeNode getTreeNode(TreePath path)
	{
		return (DefaultMutableTreeNode)path.getLastPathComponent();
	}

	private FileNode getFileNode(DefaultMutableTreeNode node)
	{
		if(node == null)
			return null;

		Object obj = node.getUserObject();

		return (obj instanceof FileNode) ? (FileNode)obj : null;
	}

	/*
	 * TreeExpansionListener
	 */

	public void treeExpanded(TreeExpansionEvent event)
	{
		DefaultMutableTreeNode parent = getTreeNode(event.getPath());
		FileNode node = getFileNode(parent);

		if(node != null)
			new ExpandNode(node, parent).execute();
	}

	public void treeCollapsed(TreeExpansionEvent event)
	{}

	/*
	 * TreeSelectionListener
	 */

	public void valueChanged(TreeSelectionEvent event)
	{
		DefaultMutableTreeNode node = getTreeNode(event.getPath());
		FileNode fnode = getFileNode(node);
		if(fnode != null)
		{
			File file = fnode.getFile();
			m_display.setText(file.getAbsolutePath());
		}
		else
		{
			m_display.setText("");
		}
	}

	/*
	 * class
	 */

	private class ExpandNode extends SwingWorker<FileNode, Object>
	{
		private final FileNode node;
		private final DefaultMutableTreeNode parent;

		ExpandNode(FileNode node, DefaultMutableTreeNode parent)
		{
			this.node = node;
			this.parent = parent;
		}

		/*
		 * SwingWorker
		 */

		@Override
		protected FileNode doInBackground() throws Exception
		{
			frame.setCursor(WAIT_CURSOR);

			if(node.expand(parent))
				model.reload(parent);

			frame.setCursor(DEFAULT_CURSOR);

			return null;
		}
	}
}
