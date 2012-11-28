package lay;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 * User: mgarin Date: 18.04.11 Time: 13:05
 */

public class DndExample/* implements MouseListener */{
	static final String panel1 = "panel1";

	static Dimension phase;
	private BufferedImage dragged;

	private final JLabel label = new JLabel("Drag me somewhere!", SwingConstants.CENTER);
	private final JButton button = new JButton("Some custom button");

	public DndExample() {
		// try {
		// // Устанавливаем нативный стиль компонентов
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (Throwable e) {
		// //
		// }

		final JFrame frame = new JFrame();
		

		// frame.getContentPane().setLayout(new BorderLayout());
		// frame.getContentPane().setBackground(Color.red);

		// Контейнер в котором будет происходить ДнД
		final SectionViewer dragContainer = new SectionViewer();
		 dragContainer.setOpaque(false);
		// dragContainer.setLayout(null);
		frame.getContentPane().add(dragContainer, BorderLayout.CENTER);

		dragContainer.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;

		dragContainer.add(new Section(dragContainer, "Section 1", Color.yellow), gbc);
		dragContainer.add(new Section(dragContainer, "Section 2", Color.green), gbc);
		dragContainer.add(new Section(dragContainer, "Section 3", Color.blue), gbc);

		gbc.weighty = 1;

		dragContainer.add(Box.createVerticalGlue(), gbc);

		// Перетаскиваемая панель
		final JPanel panel = new JPanel();

		// dragContainer.add(panel);

		final TransferHandlerDecorator transferHandle = new TransferHandlerDecorator(panel);

		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
				BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		panel.setLayout(new BorderLayout(4, 4));

		panel.add(label, BorderLayout.CENTER);
		panel.add(button, BorderLayout.SOUTH);

		label.setTransferHandler(new TransferHandler() {
			public int getSourceActions(JComponent c) {
				return TransferHandler.MOVE;
			}

			public boolean canImport(TransferSupport support) {
				// Для "прозрачности" панели при сбросе ДнД
				// Позволяет располагать панель даже когда курсор не над dragContainer'ом
				return transferHandle.canImport(support);
			}

			public boolean importData(TransferSupport support) {
				// Для "прозрачности" панели при сбросе ДнД
				// Позволяет располагать панель даже когда курсор не над dragContainer'ом
				return transferHandle.importData(support);
			}

			protected Transferable createTransferable(JComponent c) {
				return new StringSelection(panel1);
			}
		});

		label.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					// Для корректной вставки панели позднее
					Point point = panel.getLocationOnScreen();
					phase = new Dimension(e.getLocationOnScreen().x - point.x, e.getLocationOnScreen().y - point.y);

					// Для отрисовки перетаскиваемого образа
					dragged = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
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

		panel.setBounds(25, 25, 200, 100);
		panel.setBackground(Color.yellow);

		// Слушатель для корректного расположния панели при её сбросе
		dragContainer.setTransferHandler(transferHandle);

		final GlassPane glassPane = new GlassPane();
		frame.setGlassPane(glassPane);
//		
//		glassPane.addMouseMotionListener(new MouseMotionListener() {
//			
//			public void mouseMoved(MouseEvent e) {
//				System.out.println("glass: mouseMoved - " + e.getModifiers() + " - " + e.getModifiersEx());
//				
//			}
//			
//			public void mouseDragged(MouseEvent e) {
//				System.out.println("glass: mouseDragged");
//				
//			}
//		});

//		glassPane.addMouseListener(new MouseListener() {
//
//			public void mouseClicked(MouseEvent e) {
//				int modifiers = e.getModifiers();
//
//				if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
//					System.out.println("Left button pressed.");
//				} else
//
//					System.out.println("name: mouseClicked");
//			}
//
//			public void mousePressed(MouseEvent e) {
//				System.out.println("name: mousePressed");
//			}
//
//			public void mouseReleased(MouseEvent e) {
//				System.out.println("name: mouseReleased");
//			}
//
//			public void mouseEntered(MouseEvent e) {
//				System.out.println("name: mouseEntered");
//			}
//
//			public void mouseExited(MouseEvent e) {
//				System.out.println("name: mouseExited");
//			}
//		});

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

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		glassPane.setVisible(true);

	}

	public static void main(String[] args) {
		new DndExample();
	}

	// ========== MouseListener ==========

}
