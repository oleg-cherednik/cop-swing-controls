import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JWindow;

import com.alee.gui.AWTUtilitiesWrapper;

public class Example extends JWindow {

	public static void main(String[] args) {
		Example myWin = new Example();

		JPanel myPanel = new JPanel();
		myPanel.setSize(200, 200);
		myPanel.setOpaque(false);
		myPanel.setDoubleBuffered(false);
		myPanel.setBackground(new Color(30, 190, 250));
		myPanel.setLayout(null);

		myWin.getContentPane().add(myPanel);
		myWin.setLocation(350, 50);
		myWin.setSize(200, 200);

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = env.getScreenDevices();

		GraphicsConfiguration translucencyCapableGC = null;

		// first see if we can find a translucency-capable graphics device
		for (int i = 0; i < devices.length && translucencyCapableGC == null; i++) {
			GraphicsConfiguration[] configs = devices[i].getConfigurations();

			for (int j = 0; j < configs.length && translucencyCapableGC == null; j++) {
				// translucency requires java version Java SE 6u10
				if (AWTUtilitiesWrapper.isTranslucencyCapable(configs[j])) {
					translucencyCapableGC = configs[j];
				}
			}
		}

		try {
			// set window transparent
			Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
			Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
			mSetWindowOpacity.invoke(null, myWin, Float.valueOf(1.0f));
			// comment this next line out to see it working as an opaque window
			AWTUtilitiesWrapper.setWindowOpaque(myWin, false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		final JButton myBut = new JButton("sdf");
		myBut.setFont(new Font("arial", Font.BOLD, 10));
		myBut.setForeground(Color.WHITE);
		myBut.setContentAreaFilled(false);
		myBut.setSize(100, 50);
		myBut.setLocation(50, 25);
		myPanel.add(myBut);

		myBut.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("you clicked my button");
			}
		});

		myPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("you clicked my panel");
			}
		});

		myWin.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("you clicked my window");
			}
		});

		myWin.setVisible(true);
	}
}
