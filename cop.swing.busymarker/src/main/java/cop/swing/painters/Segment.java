package cop.swing.painters;

import java.text.NumberFormat;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
final class Segment {
	final float length;
	final Point p1;
	final Point p2;
	final Point p3;
	final PathIteratorType type;

	Segment(float length, Point p1, Point p2, Point p3, PathIteratorType type) {
		this.length = length;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.type = type;
	}

	public Point getPoint() {
		return type.getPoint(this);
	}

	public Point getPoint(float dist2go, Point startPoint, Segment sgmt, int w, int h) {
		return type.getPoint(dist2go, startPoint, sgmt, w, h);
	}

	// ========== Object ==========

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + Float.floatToIntBits(length);
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		result = prime * result + ((p3 == null) ? 0 : p3.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Segment))
			return false;
		Segment other = (Segment)obj;
		if (Float.floatToIntBits(length) != Float.floatToIntBits(other.length))
			return false;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		if (p3 == null) {
			if (other.p3 != null)
				return false;
		} else if (!p3.equals(other.p3))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);

		return type.name().toLowerCase() + ": length=" + nf.format(length) + "; p1=" + p1 + "; p2=" + p2 + "; p3=" + p3;
	}
}
