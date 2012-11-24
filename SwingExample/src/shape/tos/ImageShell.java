/**
 * <b>License</b>: <a href="http://www.gnu.org/licenses/lgpl.html">GNU Leser General Public License</a>
 * <b>Copyright</b>: <a href="mailto:abba-best@mail.ru">Oleg Cherednik</a>
 * 
 * $Id$
 * $HeadURL$
 */
package shape.tos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * @author Oleg Cherednik
 * @since 08.03.2012
 */
public class ImageShell extends RegionShell {
	private final Image image;

	public ImageShell(Display display, Region region, int width, int height) {
		super(display, region, width, height);

		this.image = null;
	}

	public ImageShell(Display display, ImageData imageData) {
		super(display, AlphaChannel.createRegion(imageData), imageData.width, imageData.height);

		this.image = new Image(display, imageData);

		addListeners();
	}

	private void addListeners() {
		addListener(SWT.Paint, this);
	}

	protected void onPaint(Event e) {
		e.gc.drawImage(image, 0, 0);
	}

	/*
	 * Widget
	 */

	@Override
	public void dispose() {
		if (image != null)
			image.dispose();
		super.dispose();
	}

	/*
	 * Listener
	 */

	@Override
	public void handleEvent(Event e) {
		if (e.type == SWT.Paint)
			onPaint(e);
		else
			super.handleEvent(e);
	}

	/*
	 * enum
	 */

	public enum AlphaChannel {
		NONE {
			@Override
			protected boolean isPixelVisible(int x, int y, ImageData imageData) {
				return imageData.getPixel(x, y) != 0;
			}
		},
		ALPHA {
			@Override
			protected boolean isPixelVisible(int x, int y, ImageData imageData) {
				return imageData.getAlpha(x, y) == 0xFF;
			}
		};

		protected abstract boolean isPixelVisible(int x, int y, ImageData imageData);

		private final Region _createRegion(ImageData imageData) {
			Region region = new Region();
			ImageData mask = imageData.getTransparencyMask();

			for (int y = 0; y < mask.height; y++)
				for (int x = 0; x < mask.width; x++)
					if (isPixelVisible(x, y, mask))
						region.add(x, y, 1, 1);

			return region;
		}

		public static AlphaChannel parseChannel(ImageData imageData) {
			return (imageData.alphaData != null) ? ALPHA : NONE;
		}

		public static Region createRegion(ImageData imageData) {
			return parseChannel(imageData)._createRegion(imageData);
		}
	}
}
