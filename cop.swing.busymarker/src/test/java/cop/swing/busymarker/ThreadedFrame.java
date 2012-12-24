package cop.swing.busymarker;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ThreadedFrame extends JFrame {
	public static final long DELAY = 10 * 1000;
	private static final long serialVersionUID = 4135865320735365979L;
	private static int initCounter = 0;
	private JTabbedPane tabbedPane;

	public ThreadedFrame() {
		getContentPane().setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addTab("Закладка 1", new TabOne());
		tabbedPane.addTab("Закладка 2", new TabTwo());
		tabbedPane.addTab("Закладка 3", new TabThree());
		tabbedPane.addTab("Закладка 4", new TabFour());
		setBounds(new Rectangle(400, 200));
		
		setVisible(true);

		while (initCounter > 0) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//pack();
		//setVisible(true);
	}

	public synchronized static final void incCounter() {
		initCounter++;
	}

	public synchronized static final void decCounter() {
		initCounter--;
	}

	public static void main(String... args) {
		new ThreadedFrame();
	}
}

class TabOne extends JPanel implements Runnable {
	private static final long serialVersionUID = 9070937497768242934L;

	public TabOne() {
		ThreadedFrame.incCounter();
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		try {
	        Thread.sleep(ThreadedFrame.DELAY);
        } catch (InterruptedException e) {
	        e.printStackTrace();
        }
		
		ThreadedFrame.decCounter();
		System.out.println(getClass().getSimpleName() + " - done");
	}
}

class TabTwo extends JPanel implements Runnable {
	private static final long serialVersionUID = 1101013757376052517L;

	public TabTwo() {
		ThreadedFrame.incCounter();
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		try {
	        Thread.sleep(ThreadedFrame.DELAY);
        } catch (InterruptedException e) {
	        e.printStackTrace();
        }
		
		ThreadedFrame.decCounter();
		System.out.println(getClass().getSimpleName() + " - done");
	}
}

class TabThree extends JPanel implements Runnable {
	private static final long serialVersionUID = 459143265914009804L;

	public TabThree() {
		ThreadedFrame.incCounter();
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		try {
	        Thread.sleep(ThreadedFrame.DELAY);
        } catch (InterruptedException e) {
	        e.printStackTrace();
        }
		
		ThreadedFrame.decCounter();
		System.out.println(getClass().getSimpleName() + " - done");
	}
}

class TabFour extends JPanel implements Runnable {
	private static final long serialVersionUID = -1079862244844777002L;

	public TabFour() {
		ThreadedFrame.incCounter();
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		try {
	        Thread.sleep(ThreadedFrame.DELAY);
        } catch (InterruptedException e) {
	        e.printStackTrace();
        }
		
		ThreadedFrame.decCounter();
		System.out.println(getClass().getSimpleName() + " - done");
	}
}
