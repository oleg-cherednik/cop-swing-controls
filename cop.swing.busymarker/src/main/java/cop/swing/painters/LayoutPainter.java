package cop.swing.painters;

import java.awt.Component;
import java.awt.Paint;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 * @param <T>
 */
public interface LayoutPainter<T extends Component> extends Painter<T> {
	Paint getFillPaint();

	void setFillPaint(Paint paint);
}
