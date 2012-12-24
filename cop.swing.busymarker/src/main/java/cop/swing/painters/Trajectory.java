package cop.swing.painters;

import java.awt.Rectangle;
import java.awt.Shape;

/**
 * @author Oleg Cherednik
 * @since 08.10.2012
 */
final class Trajectory {
	public final Shape shape;
	public final Point center;

	public Trajectory(Shape shape) {
		Rectangle bounds = shape.getBounds();

		this.shape = shape;
		this.center = Point.create(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
	}

	// ========== Object ==========

	@Override
	public String toString() {
		return shape.getBounds().toString();
	}
}
