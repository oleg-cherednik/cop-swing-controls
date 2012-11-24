package shape.tos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import shell.image.ProgressShell;
import shell.image.ThinkOrSwimPaintModel;

public class ImageShellExample {
	public static void main(String[] args) {
		Display display = new Display();
		ImageData imageData = new ImageData("d:\\Programming\\Eclipse\\ThinkOrSwim1.gif");
		
		final ProgressShell shell = new ProgressShell(display, imageData);
		Shell ctrlShell = new Shell(display);
		//shell.setLayout(new FillLayout());
		ctrlShell.setLayout(new GridLayout());
		ctrlShell.setSize(200, 100);
		
		shell.setPaintModel(ThinkOrSwimPaintModel.INCTANCE);
		shell.setSelection(null);
		
		final Spinner spinner = new Spinner(ctrlShell, SWT.NONE);
		spinner.setMinimum(-1);
		spinner.setMaximum(101);
		spinner.setSelection(-1);
		
		spinner.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selection = spinner.getSelection();
				shell.setSelection((selection < 0 || selection > 100) ? null : selection);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		
		shell.setLocation(67, 67);
		ctrlShell.setLocation(67, 250);
		shell.open();
		ctrlShell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}
}
