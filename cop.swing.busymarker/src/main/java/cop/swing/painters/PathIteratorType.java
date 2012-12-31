package cop.swing.painters;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.List;

import cop.swing.painters.Point;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
enum PathIteratorType {
	MOVETO(PathIterator.SEG_MOVETO) {
		@Override
		public Point getPoint(Segment data) {
			return data.p3;
		}

		@Override
		public Segment createData(Point point, float[] coords) {
			Point p1 = Point.create(coords[0], coords[1]);
			float length = getLength(point, coords);

			return new Segment(length, Point.ZERO, Point.ZERO, p1, this);
		}

		@Override
		protected float getLength(Point point, float[] coords) {
			return 0;
		}

		@Override
		public Point getPoint(float dist2go, Point startPoint, Segment sgmt, int w, int h) {
			return LINETO.getPoint(dist2go, startPoint, sgmt, w, h);
		}
	},
	CLOSE(PathIterator.SEG_CLOSE) {
		@Override
		public Point getPoint(Segment data) {
			return data.p3;
		}

		@Override
		public Segment createData(Point point, float[] coords) {
			Point p1 = Point.create(coords[0], coords[1]);
			float length = getLength(point, coords);

			return new Segment(length, Point.ZERO, Point.ZERO, p1, this);
		}

		@Override
		protected float getLength(Point point, float[] coords) {
			return CUBICTO.getLength(point, coords);
		}

		@Override
		public Point getPoint(float dist2go, Point startPoint, Segment sgmt, int w, int h) {
			return CUBICTO.getPoint(dist2go, startPoint, sgmt, w, h);
		}

		@Override
		protected boolean doCreateSegment(Point startPoint, Point currentPoint) {
			return !startPoint.equals(currentPoint);
		}
	},
	CUBICTO(PathIterator.SEG_CUBICTO) {
		@Override
		public Point getPoint(Segment data) {
			return data.p3;
		}

		@Override
		public Segment createData(Point point, float[] coords) {
			Point p1 = Point.create(coords[0], coords[1]);
			Point p2 = Point.create(coords[2], coords[3]);
			Point p3 = Point.create(coords[4], coords[5]);
			float length = getLength(point, coords);

			return new Segment(length, p1, p2, p3, this);
		}

		@Override
		protected float getLength(Point point, float[] coords) {
			float x = Math.abs(point.x - coords[4]);
			float y = Math.abs(point.y - coords[5]);

			// trans coords from abs to rel
			float c1rx = Math.abs(point.x - coords[0]) / x;
			float c1ry = Math.abs(point.y - coords[1]) / y;
			float c2rx = Math.abs(point.x - coords[2]) / x;
			float c2ry = Math.abs(point.y - coords[3]) / y;
			float prvLen = 0;
			float prvX = 0;
			float prvY = 0;

			for (float t = 0.01f; t <= 1.0f; t += .01f) {
				Point2D.Float xy = getXY(t, c1rx, c1ry, c2rx, c2ry);

				prvLen += Math.sqrt((xy.x - prvX) * (xy.x - prvX) + (xy.y - prvY) * (xy.y - prvY));
				prvX = xy.x;
				prvY = xy.y;
			}

			return (Math.abs(x) + Math.abs(y)) / 2 * prvLen;
		}

		@Override
		public Point getPoint(float dist2go, Point startPoint, Segment sgmt, int w, int h) {
			// bezier curve
			float x = Math.abs(startPoint.x - sgmt.p3.x);
			float y = Math.abs(startPoint.y - sgmt.p3.y);

			// trans coords from abs to rel
			float c1rx = Math.abs(startPoint.x - sgmt.p1.x) / x;
			float c1ry = Math.abs(startPoint.y - sgmt.p1.y) / y;
			float c2rx = Math.abs(startPoint.x - sgmt.p2.x) / x;
			float c2ry = Math.abs(startPoint.y - sgmt.p2.y) / y;

			Point2D.Float point = PathIteratorType.getXY(dist2go / sgmt.length, c1rx, c1ry, c2rx, c2ry);

			float a = startPoint.x - sgmt.p3.x;
			float b = startPoint.y - sgmt.p3.y;

			point.x = startPoint.x - point.x * a;
			point.y = startPoint.y - point.y * b;

			return Point.create(point.x, point.y);
		}
	},
	LINETO(PathIterator.SEG_LINETO) {
		@Override
		public Point getPoint(Segment data) {
			return data.p3;
		}

		@Override
		public Segment createData(Point point, float[] coords) {
			Point p1 = Point.create(coords[0], coords[1]);
			float length = getLength(point, coords);

			return new Segment(length, Point.ZERO, Point.ZERO, p1, this);
		}

		@Override
		protected float getLength(Point point, float[] coords) {
			float a = point.x - coords[0];
			float b = point.y - coords[1];
			return (float)Math.sqrt(a * a + b * b);
		}

		@Override
		public Point getPoint(float dist2go, Point startPoint, Segment sgmt, int w, int h) {
			// linear
			float a = sgmt.p3.x - startPoint.x;
			float b = sgmt.p3.y - startPoint.y;
			float x = startPoint.x + a * dist2go / sgmt.length;
			float y = startPoint.y + b * dist2go / sgmt.length;

			return Point.create(x, y);
		}
	},
	QUADTO(PathIterator.SEG_QUADTO) {
		@Override
		public Point getPoint(Segment data) {
			return data.p3;
		}

		@Override
		public Segment createData(Point point, float[] coords) {
			Point p1 = Point.create(coords[0], coords[1]);
			Point p2 = Point.create(coords[2], coords[3]);
			float length = getLength(point, coords);

			return new Segment(length, p1, Point.ZERO, p2, this);
		}

		@Override
		protected float getLength(Point point, float[] coords) {
			Float ctrl = new Point2D.Float(coords[0], coords[1]);
			Float end = new Point2D.Float(coords[2], coords[3]);
			// get abs values
			// ctrl1
			float c1ax = Math.abs(point.x - ctrl.x);
			float c1ay = Math.abs(point.y - ctrl.y);
			// end1
			float e1ax = Math.abs(point.x - end.x);
			float e1ay = Math.abs(point.y - end.y);
			// get max value on each axis
			float maxX = Math.max(c1ax, e1ax);
			float maxY = Math.max(c1ay, e1ay);

			// trans coords from abs to rel
			// ctrl1
			ctrl.x = c1ax / maxX;
			ctrl.y = c1ay / maxY;
			// end1
			end.x = e1ax / maxX;
			end.y = e1ay / maxY;

			// claculate length
			float prvLength = 0, prevX = 0, prevY = 0;
			for (float t = 0.01f; t <= 1.0f; t += .01f) {
				Point2D.Float xy = getXY(t, new Float(0, 0), ctrl, end);
				prvLength += (float)Math.sqrt((xy.x - prevX) * (xy.x - prevX) + (xy.y - prevY) * (xy.y - prevY));
				prevX = xy.x;
				prevY = xy.y;
			}
			// prev len is a fraction num of the real path length
			float a = Math.abs(coords[2] - point.x);
			float b = Math.abs(coords[3] - point.y);

			return prvLength * (float)Math.sqrt(a * a + b * b);
		}

		@Override
		public Point getPoint(float dist2go, Point startPoint, Segment sgmt, int w, int h) {
			// quadratic curve
			Float ctrl = new Point2D.Float(sgmt.p1.x / w, sgmt.p1.y / h);
			Float end = new Point2D.Float(sgmt.p3.x / w, sgmt.p3.y / h);
			Float start = new Float(startPoint.x / w, startPoint.y / h);

			// trans coords from abs to rel
			Point2D.Float point = PathIteratorType.getXY(dist2go / sgmt.length, start, ctrl, end);
			point.x *= w;
			point.y *= h;

			return Point.create(point.x, point.y);
		}
	};

	private final int pathSegmentType;

	PathIteratorType(int pathSegmentType) {
		this.pathSegmentType = pathSegmentType;
	}

	protected abstract float getLength(Point point, float[] coords);

	public abstract Point getPoint(float dist2go, Point startPoint, Segment sgmt, int w, int h);

	public abstract Segment createData(Point point, float[] coords);

	public abstract Point getPoint(Segment data);

	public final void addSegment(float[] coords, List<Segment> segments) {
		Point currentPoint = getCurrentPoint(segments);
		Point startPoint = getStartPoint(segments);

		if (doCreateSegment(startPoint, currentPoint))
			segments.add(createData(currentPoint, coords));
	}

	protected boolean doCreateSegment(Point startPoint, Point currentPoint) {
		return true;
	}

	// ========== static ==========

	public static Point getCurrentPoint(List<Segment> segments) {
		return segments.isEmpty() ? Point.ZERO : segments.get(segments.size() - 1).getPoint();
	}

	public static Point getStartPoint(List<Segment> segments) {
		return segments.isEmpty() ? Point.ZERO : segments.get(0).getPoint();
	}

	/**
	 * Calculates the XY point for a given t value. The general spline equation is: x = b0*x0 + b1*x1 + b2*x2 + b3*x3 y
	 * = b0*y0 + b1*y1 + b2*y2 + b3*y3 where: b0 = (1-t)^3 b1 = 3 * t * (1-t)^2 b2 = 3 * t^2 * (1-t) b3 = t^3 We know
	 * that (x0,y0) == (0,0) and (x1,y1) == (1,1) for our splines, so this simplifies to: x = b1*x1 + b2*x2 + b3 y =
	 * b1*x1 + b2*x2 + b3
	 * 
	 * @param t parametric value for spline calculation
	 */
	public static Point2D.Float getXY(float t, float x1, float y1, float x2, float y2) {
		Point2D.Float xy;
		float invT = (1 - t);
		float b1 = 3 * t * (invT * invT);
		float b2 = 3 * (t * t) * invT;
		float b3 = t * t * t;
		xy = new Point2D.Float((b1 * x1) + (b2 * x2) + b3, (b1 * y1) + (b2 * y2) + b3);
		return xy;
	}

	/**
	 * Calculates relative position of the point on the quad curve in time t&lt;0,1&gt;.
	 * 
	 * @param t distance on the curve
	 * @param ctrl Control point in rel coords
	 * @param end End point in rel coords
	 * @return Solution of the quad equation for time T in non complex space in rel coords.
	 */
	public static Point2D.Float getXY(float t, Point2D.Float begin, Point2D.Float ctrl, Point2D.Float end) {
		/*
		 * P1 = (x1, y1) - start point of curve P2 = (x2, y2) - end point of curve Pc = (xc, yc) - control point Pq(t) =
		 * P1*(1 - t)^2 + 2*Pc*t*(1 - t) + P2*t^2 = = (P1 - 2*Pc + P2)*t^2 + 2*(Pc - P1)*t + P1 t = [0:1] // thx Jim ...
		 * b0 = (1 -t)^2, b1 = 2*t*(1-t), b2 = t^2
		 */
		Point2D.Float xy;
		float invT = (1 - t);
		float b0 = invT * invT;
		float b1 = 2 * t * invT;
		float b2 = t * t;
		xy = new Point2D.Float(b0 * begin.x + (b1 * ctrl.x) + b2 * end.x, b0 * begin.y + (b1 * ctrl.y) + b2 * end.y);

		return xy;
	}

	public static PathIteratorType parsePathIterator(int pathIterator) {
		for (PathIteratorType segment : values())
			if (segment.pathSegmentType == pathIterator)
				return segment;

		return null;
	}

	public static PathIteratorType parseType(int pathSegmentType) {
		for (PathIteratorType segment : values())
			if (segment.pathSegmentType == pathSegmentType)
				return segment;

		throw new IllegalArgumentException("Can't parse '" + pathSegmentType + "' to PathIteratorType");
	}
}
