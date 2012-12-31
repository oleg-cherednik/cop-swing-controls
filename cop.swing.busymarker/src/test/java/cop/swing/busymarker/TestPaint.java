package cop.swing.busymarker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cop.swing.painters.InfiniteBusyPainter;

public class TestPaint extends JPanel {
	private static final long serialVersionUID = -1207475454894233038L;
	
	private final InfiniteBusyPainter painter = new InfiniteBusyPainter<Component>(26);

	private Shape shape;
	private static float dash[] = new float[] { 1, 0.4f, 1.5f };

	public TestPaint() {
		Font f = getFont().deriveFont(Font.BOLD, 70);
		GlyphVector v = f.createGlyphVector(getFontMetrics(f).getFontRenderContext(), "Hello");
//		shape = v.getOutline();
		int height = 26;
		
		float x = height * 2 / 13;
		float h = height - (height * 4 / 13);
		shape = new Ellipse2D.Float(x, x, h, h);
		
		
		
//		shape = new RoundRectangle2D.Float(0, 0, height * 4 / 13, 4, 4, 4);
		setPreferredSize(new Dimension(150, 150));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g.create();
		
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, 100, 100);
		
		
		
		
		painter.paint(g2d, this, 100, 100);
//
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2.translate(100, 150);
//		// g2.rotate(0.4);
//		g2.setPaint(Color.black);
//		g2.fill(shape);
//		
//		for (Point point : points) {
//			g2d.setColor(getFrameColor(frame, i++));
//			paintRotatedCenteredShapeAtPoint(point, center, g2d);
//		}
//		
////		g2.setPaint(Color.black);
////		g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1, dash, 0));
//		g2.draw(shape);
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("Test");
		Component c = new TestPaint();
		f.getContentPane().add(c);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
}
