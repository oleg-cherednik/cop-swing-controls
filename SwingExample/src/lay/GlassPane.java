package lay;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

final class GlassPane extends JComponent {
	private static final long serialVersionUID = 2849610603095088058L;

	private Point point;
	private BufferedImage image;

	public GlassPane() {
		setOpaque(false);
	}

	public void setPoint(Point point) {
		this.point = point;
		repaint();
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		repaint();
	}
	
	// ========== Component ==========

	public void paint(Graphics g) {
		super.paint(g);

		if (point != null && image != null)
			g.drawImage(image, point.x, point.y, null);
	}
}
