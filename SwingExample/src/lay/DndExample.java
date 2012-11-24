package lay;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 * User: mgarin Date: 18.04.11 Time: 13:05
 */

public class DndExample {
	static final String panel1 = "panel1";

	static Dimension phase = null;
	private static BufferedImage dragged = null;

	public static void main(String[] args) {
//		try {
//			// Устанавливаем нативный стиль компонентов
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Throwable e) {
//			//
//		}

		final JFrame frame = new JFrame();

		frame.getRootPane().setOpaque(true);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().setBackground(Color.red);

		// Контейнер в котором будет происходить ДнД
		final JPanel dragContainer = new JPanel();
		dragContainer.setOpaque(false);
		dragContainer.setLayout(null);
		frame.getContentPane().add(dragContainer, BorderLayout.CENTER);
		dragContainer.setBackground(Color.magenta);

		// Перетаскиваемая панель
		final JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
				BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		panel.setLayout(new BorderLayout(4, 4));
		panel.add(new JLabel("Drag me somewhere!", JLabel.CENTER) {
			{
				setTransferHandler(new TransferHandler() {
					public int getSourceActions(JComponent c) {
						return TransferHandler.MOVE;
					}

					public boolean canImport(TransferSupport support) {
						// Для "прозрачности" панели при сбросе ДнД
						// Позволяет располагать панель даже когда курсор не над dragContainer'ом
						return dragContainer.getTransferHandler().canImport(support);
					}

					public boolean importData(TransferSupport support) {
						// Для "прозрачности" панели при сбросе ДнД
						// Позволяет располагать панель даже когда курсор не над dragContainer'ом
						return dragContainer.getTransferHandler().importData(support);
					}

					protected Transferable createTransferable(JComponent c) {
						return new StringSelection(panel1);
					}
				});
				addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						if (SwingUtilities.isLeftMouseButton(e)) {
							// Для корректной вставки панели позднее
							Point los = panel.getLocationOnScreen();
							phase = new Dimension(e.getLocationOnScreen().x - los.x, e.getLocationOnScreen().y - los.y);

							// Для отрисовки перетаскиваемого образа
							dragged = new BufferedImage(panel.getWidth(), panel.getHeight(),
									BufferedImage.TYPE_INT_ARGB);
							Graphics2D g2d = dragged.createGraphics();
							g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
							panel.paintAll(g2d);
							g2d.dispose();

							JComponent c = (JComponent)e.getSource();
							TransferHandler handler = c.getTransferHandler();
							handler.exportAsDrag(c, e, TransferHandler.MOVE);
						}
					}
				});
			}
		});

		panel.add(new JButton("Some custom button"), BorderLayout.SOUTH);
		panel.setBounds(25, 25, 200, 100);
		panel.setBackground(Color.yellow);
		dragContainer.add(panel);

		// Слушатель для корректного расположния панели при её сбросе
		dragContainer.setTransferHandler(new TransferHandlerDecorator(panel));
//		dragContainer.setTransferHandler(new TransferHandler() {
//			public int getSourceActions(JComponent c) {
//				return TransferHandler.NONE;
//			}
//
//			public boolean canImport(TransferSupport support) {
//				try {
//					return support.getTransferable().getTransferData(DataFlavor.stringFlavor) != null;
//				} catch (Throwable e) {
//					return false;
//				}
//			}
//
//			public boolean importData(TransferHandler.TransferSupport info) {
//				if (info.isDrop()) {
//					try {
//						String panelId = (String)info.getTransferable().getTransferData(DataFlavor.stringFlavor);
//						Point point = info.getDropLocation().getDropPoint();
//						
//						if (panelId.equals(panel1)) {
//							panel.setLocation(point.x - phase.width, point.y - phase.height);
//							panel.revalidate();
//						}
//						return true;
//					} catch (Throwable e) {
//						return false;
//					}
//				} else {
//					return false;
//				}
//			}
//		});
		

		final GlassPane glassPane = new GlassPane();
		frame.setGlassPane(glassPane);

		// Слушатель для перерисовки перетаскиваемого образа
		DragSourceAdapter dsa = new DragSourceAdapter() {
			public void dragEnter(DragSourceDragEvent dsde) {
				updateGlassPane(dsde);
			}

			public void dragMouseMoved(DragSourceDragEvent dsde) {
				updateGlassPane(dsde);
			}

			private void updateGlassPane(DragSourceDragEvent dsde) {
				if (frame != null && frame.isVisible()) {
					glassPane.setImage(dragged);
					glassPane
							.setPoint(new Point(dsde.getLocation().x - dragContainer.getLocationOnScreen().x
									- phase.width, dsde.getLocation().y - dragContainer.getLocationOnScreen().y
									- phase.height));
				}
			}

			public void dragDropEnd(DragSourceDropEvent dsde) {
				if (frame != null && frame.isVisible()) {
					glassPane.setImage(null);
					glassPane.setPoint(null);
				}
			}
		};
		DragSource.getDefaultDragSource().addDragSourceListener(dsa);
		DragSource.getDefaultDragSource().addDragSourceMotionListener(dsa);

		frame.setDefaultCloseOperation(args.length > 0 ? JFrame.DISPOSE_ON_CLOSE : JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		glassPane.setVisible(true);
	}

}
