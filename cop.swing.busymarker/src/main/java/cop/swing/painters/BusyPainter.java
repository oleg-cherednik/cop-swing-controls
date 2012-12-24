package cop.swing.painters;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.UIManager;

import cop.swing.busymarker.plaf.BusyPaneUI;
import cop.swing.painters.enums.Direction;
import cop.swing.utils.ColorUtils;

/**
 * A specific painter that paints an "infinite progress" like animation
 * 
 * @author Oleg Cherednik
 * @since 29.09.2012
 */
public class BusyPainter<T extends Component> extends AbstractBusyPainter<T> {
	private final Shape pointShape;
	private final Trajectory trajectory;

	private int frame = -1;
	private int totalPoints = UIManager.getInt(BusyPaneUI.TOTAL_POINTS);
	private int trailLength = UIManager.getInt(BusyPaneUI.TREIL_LENGTH);

	private Color backgroundColor = UIManager.getColor(BusyPaneUI.BP_COLOR_BACKGROUND);
	private Color foregroundColor = UIManager.getColor(BusyPaneUI.BP_COLOR_FOREGROUND);

	private Direction direction = Direction.CLOCKWISE;
	private boolean centered = true;

	private final Color[] trailColors = new Color[trailLength];
	private final List<Point> points = new ArrayList<Point>(totalPoints);
	/** describes one point */
	private final Segment[] segments;

	boolean updateTrailColors = true;
	boolean updatePoints = true;

	static {
		BusyPaneUI.create();
	}

	public BusyPainter() {
		this(UIManager.getInt(BusyPaneUI.HEIGHT));
	}

	public BusyPainter(int height) {
		this(getDefaultPointShape(height), getDefaultTrajectory(height));
	}

	/**
	 * Initializes painter with given trajectory and point shape. Bounds are dynamically calculated to let given
	 * trajectory fits in.
	 * 
	 * @param point point shape
	 * @param trajectory trajectory shape
	 */
	public BusyPainter(Shape point, Shape trajectory) {
		this.pointShape = point;
		this.trajectory = new Trajectory(trajectory);
		this.segments = getSegments(trajectory);
	}

	/**
	 * Gets value of centering hint. If true, shape will be positioned in the center of painted area.
	 * 
	 * @return Whether shape will be centered over painting area or not.
	 */
	public boolean isCentered() {
		return centered;
	}

	private void updatePoints(int width, int height) {
		if (!updatePoints)
			return;

		points.clear();

		Point startPoint = segments[0].getPoint();
		float delta = getDistance(segments) / totalPoints;
		points.add(startPoint);
		int sgIdx = 1;
		Segment segment = segments[sgIdx];
		float length = segment.length;
		float travDist = delta;

		for (int i = 1; i < totalPoints; i++) {
			while (length < delta) {
				sgIdx++;
				// Be carefull when messing around with points.
				startPoint = segment.p3;
				segment = segments[sgIdx];
				travDist = delta - length;
				length += segment.length;
			}

			points.add(segment.getPoint(travDist, startPoint, segment, width, height));

			length -= delta;
			travDist += delta;
		}

		if (direction == Direction.COUNTER_CLOCKWISE)
			Collections.reverse(points);

		updatePoints = false;
	}

	private void updateTrailColors() {
		if (!updateTrailColors)
			return;

		for (int i = 0; i < trailLength; i++)
			trailColors[i] = ColorUtils.interpolate(foregroundColor, backgroundColor, (i * 100) / trailLength);

		updateTrailColors = false;
	}

	private Color getFrameColor(int frame, int i) {
		if (frame >= 0)
			for (int j = 0; j < trailLength; j++)
				if (i == (frame - j + totalPoints) % totalPoints)
					return trailColors[j];

		return backgroundColor;
	}

	/**
	 * Gets current frame.
	 * 
	 * @return Current frame.
	 */
	public final int getFrame() {
		return frame;
	}

	/**
	 * Sets current frame.
	 * 
	 * @param frame Current frame.
	 */
	public void setFrame(int frame) {
		this.frame = frame;
	}

	public final Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		if (this.backgroundColor.equals(backgroundColor))
			return;

		this.backgroundColor = backgroundColor;
		this.updateTrailColors = true;
	}

	public final Color getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(Color foregroundColor) {
		if (this.foregroundColor == foregroundColor)
			return;

		this.foregroundColor = foregroundColor;
		this.updateTrailColors = true;
	}

	/**
	 * Gets total amount of distinct points in spinner.
	 * 
	 * @return Total amount of points.
	 */
	public final int getPoints() {
		return totalPoints;
	}

	/**
	 * Sets total amount of points in spinner. Bound property.
	 * 
	 * @param points Total amount of points.
	 */
	public void setPoints(int points) {
		if (points <= 0)
			points = UIManager.getInt(BusyPaneUI.TOTAL_POINTS);
		if (this.totalPoints == points)
			return;

		this.totalPoints = points;
		this.updateTrailColors = true;
	}

	/**
	 * Gets length of trail in number of points.
	 * 
	 * @return Trail lenght.
	 */
	public final int getTrailLength() {
		return trailLength;
	}

	/**
	 * Sets length of the trail in points. Bound property.
	 * 
	 * @param trailLength Trail length in points.
	 */
	public void setTrailLength(int trailLength) {
		trailLength = Math.max(1, Math.min(totalPoints, trailLength));

		if (this.trailLength == trailLength)
			return;

		this.trailLength = trailLength;
		this.updateTrailColors = true;
	}

	/**
	 * Gets shape of current point.
	 * 
	 * @return Shape of the point.
	 */
	public final Shape getPointShape() {
		return pointShape;
	}

	/**
	 * Gets current trajectory.
	 * 
	 * @return Current spinner trajectory .
	 */
	public final Shape getTrajectory() {
		return trajectory.shape;
	}

	/**
	 * Sets new spinning direction.
	 * 
	 * @param direction Spinning direction.
	 */
	public void setDirection(Direction direction) {
		if (this.direction == direction)
			return;

		this.direction = direction;
		this.updatePoints = true;
	}

	/**
	 * Gets current direction of spinning.
	 * 
	 * @return Current spinning direction.
	 */
	public final Direction getDirection() {
		return direction;
	}

	/**
	 * Centers shape in the area covered by the painter.
	 * 
	 * @param centered Centering hint.
	 */
	public void setCentered(boolean centered) {
		this.centered = centered;
	}

	// ========== AbstractPainter ==========

	@Override
	protected void doPaint(Graphics2D g2d, T obj, int width, int height) {
		updateTrailColors();
		updatePoints(width, height);

		if (centered) {
			Rectangle size = trajectory.shape.getBounds();
			int x = (width - size.width - 2 * size.x) / 2;
			int y = (height - size.height - 2 * size.y) / 2;

			g2d.translate(x, y);
			paintPoints(g2d, trajectory.center);
			g2d.translate(-x, -y);
		} else
			paintPoints(g2d, trajectory.center);
	}

	private void paintPoints(Graphics2D g2d, Point center) {
		int i = 0;

		g2d.translate(center.x, center.y);

		for (Point point : points) {
			double angle = getPointAngle(point, center);

			double a = Math.abs(center.y - point.y);
			double b = Math.abs(point.x - center.x);
			double x = Math.sqrt(a * a + b * b) - pointShape.getBounds().getWidth() / 2;
			double y = -pointShape.getBounds().getHeight() / 2;

			g2d.setColor(getFrameColor(frame, i++));

			g2d.rotate(angle);
			g2d.translate(x, y);
			g2d.fill(pointShape);
			g2d.translate(-x, -y);
			g2d.rotate(-angle);
		}

		g2d.translate(-center.x, -center.y);
	}

	// ========== static ==========

	private static double getPointAngle(Point point, Point center) {
		double a = Math.abs(center.y - point.y);
		double b = Math.abs(point.x - center.x);
		double angle = Math.atan(a / b);

		angle = Double.compare(0, angle) == 0 ? 0 : angle;

		if (center.y >= point.y)
			return point.x >= center.x ? -angle : angle - Math.PI;

		return point.x >= center.x ? angle : Math.PI - angle;
	}

	private static Segment[] getSegments(Shape shape) {
		float[] coords = new float[6];
		List<Segment> segments = new ArrayList<Segment>(6);
		PathIterator it = shape.getPathIterator(null);

		segments.clear();

		while (!it.isDone()) {
			PathIteratorType.parseType(it.currentSegment(coords)).addSegment(coords, segments);
			it.next();
		}

		return segments.toArray(new Segment[segments.size()]);
	}

	private static float getDistance(Segment[] segments) {
		float distance = 0;

		for (Segment segment : segments)
			distance += segment.length;

		return distance;
	}

	protected static Shape getDefaultTrajectory(int height) {
		float x = height * 2 / 13;
		float h = height - (height * 4 / 13);
		return new Ellipse2D.Float(x, x, h, h);
	}

	protected static Shape getDefaultPointShape(int height) {
		return new RoundRectangle2D.Float(0, 0, height * 4 / 13, 4, 4, 4);
	}
}
