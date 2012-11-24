package filetree;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class Example
{
	private Example() throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = createFrame();
		
		new FileTreePanel(frame);
		
		
	}
	
	private static JFrame createFrame() {
		JFrame frame = new JFrame("Directories Tree");
		
		frame.setSize(400, 300);
		
		return frame;
	}
	
	public static void main(String[] args)
	{
		try {
			new Example();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
