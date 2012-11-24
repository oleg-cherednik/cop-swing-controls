package dirtree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

final class IconCellRenderer extends JButton implements TreeCellRenderer
{
	private static final long serialVersionUID = 275296723231363602L;

	private static final Color textSelectionColor = UIManager.getColor("Tree.selectionForeground");
	private static final Color textNonSelectionColor = UIManager.getColor("Tree.textForeground");
	private static final Color backgroundSelectionColor = UIManager.getColor("Tree.selectionBackground");
	private static final Color backgroundNonSelectionColor = UIManager.getColor("Tree.textBackground");
	private static final Color borderSelectionColor = UIManager.getColor("Tree.selectionBorderColor");

	private boolean selected;

	/*
	 * TreeCellRenderer
	 */

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
	                boolean leaf, int row, boolean hasFocus)

	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		Object obj = node.getUserObject();

		if(obj instanceof Boolean)
		{
			setText("...");
			//setIcon(null);
		}
		else if(obj instanceof FileNode)
		{
			setText(obj.toString());
			//setIcon(((FileNode)obj).getIcon(expanded));
		}

		setFont(tree.getFont());
		setForeground(sel ? textSelectionColor : textNonSelectionColor);
		setBackground(sel ? backgroundSelectionColor : backgroundNonSelectionColor);
		selected = sel;

		return this;
	}

	/*
	 * JComponent
	 */

	@Override
	public void paintComponent(Graphics g)
	{
		Color color = getBackground();
		Icon icon = getIcon();
		int offset = (icon != null && getText() != null) ? (icon.getIconWidth() + getIconTextGap()) : 0;

		g.setColor(color);
		g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);

		if(selected)
		{
			g.setColor(borderSelectionColor);
			g.drawRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
		}

		super.paintComponent(g);
	}
}
