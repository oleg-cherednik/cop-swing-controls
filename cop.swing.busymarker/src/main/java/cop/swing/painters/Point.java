package cop.swing.painters;

import java.text.NumberFormat;

/**
 * @author Oleg Cherednik
 * @since 01.10.2012
 */
public final class Point {
	public static final Point ZERO = new Point(0, 0);

	public final float x;
	public final float y;

	public static Point create(float x, float y) {
		return (Float.floatToIntBits(x) != 0 || Float.floatToIntBits(y) != 0) ? new Point(x, y) : ZERO;
	}

	private Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	// ========== Object ==========

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point)obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		return "[" + nf.format(x) + ";" + nf.format(y) + "]";
	}
}
