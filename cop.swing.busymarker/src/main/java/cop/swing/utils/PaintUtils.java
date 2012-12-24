package cop.swing.utils;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A collection of utilties for painting visual effects.
 * 
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public final class PaintUtils {
	private PaintUtils() {}

	/**
	 * Resizes a gradient to fill the width and height available. If the gradient is left to right it will be resized to
	 * fill the entire width. If the gradient is top to bottom it will be resized to fill the entire height. If the
	 * gradient is on an angle it will be resized to go from one corner to the other of the rectangle formed by (0,0 ->
	 * width,height).
	 * 
	 * This method can resize java.awt.GradientPaint, java.awt.LinearGradientPaint, and the LinearGradientPaint
	 * implementation from Apache's Batik project. Note, this method does not require the MultipleGradientPaint.jar from
	 * Apache to compile or to run. MultipleGradientPaint.jar *is* required if you want to resize the
	 * LinearGradientPaint from that jar.
	 * 
	 * Any paint passed into this method which is not a kind of gradient paint (like a Color or TexturePaint) will be
	 * returned unmodified. It will not throw an exception. If the gradient cannot be resized due to other errors the
	 * original paint will be returned unmodified. It will not throw an exception.
	 * 
	 */
	public static Paint resizeGradient(Paint paint, int width, int height) {
		if (paint == null)
			return paint;

		if (paint instanceof GradientPaint) {
			GradientPaint gp = (GradientPaint)paint;
			Point2D[] pts = new Point2D[2];
			pts[0] = gp.getPoint1();
			pts[1] = gp.getPoint2();
			pts = adjustPoints(pts, width, height);
			return new GradientPaint(pts[0], gp.getColor1(), pts[1], gp.getColor2(), gp.isCyclic());
		}

		if ("java.awt.LinearGradientPaint".equals(paint.getClass().getName())
				|| "org.apache.batik.ext.awt.LinearGradientPaint".equals(paint.getClass().getName())) {
			return resizeLinearGradient(paint, width, height);
		}
		return paint;
	}

	private static Paint resizeLinearGradient(Paint p, int width, int height) {
		try {
			Point2D[] pts = new Point2D[2];
			pts[0] = (Point2D)invokeMethod(p, "getStartPoint");
			pts[1] = (Point2D)invokeMethod(p, "getEndPoint");
			pts = adjustPoints(pts, width, height);
			float[] fractions = (float[])invokeMethod(p, "getFractions");
			Color[] colors = (Color[])invokeMethod(p, "getColors");

			Constructor<?> con = p.getClass().getDeclaredConstructor(Point2D.class, Point2D.class,
					new float[0].getClass(), new Color[0].getClass());
			return (Paint)con.newInstance(pts[0], pts[1], fractions, colors);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return p;
	}

	private static Object invokeMethod(final Object p, final String methodName) throws NoSuchMethodException,
			InvocationTargetException, IllegalArgumentException, SecurityException, IllegalAccessException {
		Method meth = p.getClass().getMethod(methodName);
		return meth.invoke(p);
	}

	private static Point2D[] adjustPoints(Point2D[] pts, int width, int height) {
		Point2D start = pts[0];
		Point2D end = pts[1];

		double angle = calcAngle(start, end);
		double a2 = Math.toDegrees(angle);
		double e = 1;

		// if it is near 0 degrees
		if (Math.abs(angle) < Math.toRadians(e) || Math.abs(angle) > Math.toRadians(360 - e)) {
			start = new Point2D.Float(0, 0);
			end = new Point2D.Float(width, 0);
		}

		// near 45
		if (isNear(a2, 45, e)) {
			start = new Point2D.Float(0, 0);
			end = new Point2D.Float(width, height);
		}

		// near 90
		if (isNear(a2, 90, e)) {
			start = new Point2D.Float(0, 0);
			end = new Point2D.Float(0, height);
		}

		// near 135
		if (isNear(a2, 135, e)) {
			start = new Point2D.Float(width, 0);
			end = new Point2D.Float(0, height);
		}

		// near 180
		if (isNear(a2, 180, e)) {
			start = new Point2D.Float(width, 0);
			end = new Point2D.Float(0, 0);
		}

		// near 225
		if (isNear(a2, 225, e)) {
			start = new Point2D.Float(width, height);
			end = new Point2D.Float(0, 0);
		}

		// near 270
		if (isNear(a2, 270, e)) {
			start = new Point2D.Float(0, height);
			end = new Point2D.Float(0, 0);
		}

		// near 315
		if (isNear(a2, 315, e)) {
			start = new Point2D.Float(0, height);
			end = new Point2D.Float(width, 0);
		}

		return new Point2D[] { start, end };
	}

	private static boolean isNear(double angle, double target, double error) {
		return Math.abs(target - Math.abs(angle)) < error;
	}

	private static double calcAngle(Point2D p1, Point2D p2) {
		double x_off = p2.getX() - p1.getX();
		double y_off = p2.getY() - p1.getY();
		double angle = Math.atan(y_off / x_off);
		if (x_off < 0) {
			angle = angle + Math.PI;
		}

		if (angle < 0) {
			angle += 2 * Math.PI;
		}
		if (angle > 2 * Math.PI) {
			angle -= 2 * Math.PI;
		}
		return angle;
	}

}
