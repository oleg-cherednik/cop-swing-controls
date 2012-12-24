package cop.swing.utils;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public final class ColorUtils {
	private static final Map<Integer, Color> COLORS = new ConcurrentHashMap<Integer, Color>();

	static {
		storeColor(Color.white);
		storeColor(Color.lightGray);
		storeColor(Color.gray);
		storeColor(Color.darkGray);
		storeColor(Color.black);
		storeColor(Color.red);
		storeColor(Color.pink);
		storeColor(Color.orange);
		storeColor(Color.yellow);
		storeColor(Color.green);
		storeColor(Color.magenta);
		storeColor(Color.cyan);
		storeColor(Color.blue);
	}

	private ColorUtils() {}

	public static Color getColor(Color color, int alpha) {
		return getColor(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	public static Color getColor(int red, int green, int blue) {
		return getColor(red, green, blue, 0xFF);
	}

	public static Color getColor(int red, int green, int blue, int alpha) {
		return getColor(getRGB(red, green, blue, alpha));
	}

	public static Color getColor(int rgb) {
		Color color = COLORS.get(rgb);

		if (color == null)
			color = storeColor(new Color(rgb, true));

		return color;
	}

	private static Color storeColor(Color color) {
		COLORS.put(color.getRGB(), color);
		return color;
	}

	private static int getRGB(int red, int green, int blue, int alpha) {
		return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
	}

	/**
	 * Returns color between two given colors with given percent factor
	 * 
	 * @param colorLow low color bound
	 * @param colorHigh high color bound
	 * @param percent color interpolation factor [0%;100%] between <code>colorFrom</code> and <code>colorHigh</code>
	 * @return new color: 0% - colorLow, 100% - colorHigh
	 */
	public static Color interpolate(Color colorLow, Color colorHigh, double percent) {
		percent = Math.max(Math.min(percent, 100), 0);

		int red = interpolate(colorLow.getRed(), colorHigh.getRed(), percent);
		int green = interpolate(colorLow.getGreen(), colorHigh.getGreen(), percent);
		int blue = interpolate(colorLow.getBlue(), colorHigh.getBlue(), percent);
		int alpha = interpolate(colorLow.getAlpha(), colorHigh.getAlpha(), percent);

		return getColor(red, green, blue, alpha);
	}

	private static int interpolate(int a, int b, double percent) {
		return (int)Math.round(a + ((b - a) * percent) / 100);
	}

	/**
	 * Creates a new <code>Color</code> that is a brighter version of the specified <code>Color</code>.
	 * <p>
	 * This method convert color's RGB components into an HSL color model.<br>
	 * After what the <strong>Lightness</strong> component is <strong>increased</strong> of 25.1% (BLACK is brightned to
	 * GRAY)
	 * <p>
	 * Be carefull, this method don't provide same result than the {@link Color#brighter()} method.
	 * 
	 * @return a new <code>Color</code> object that is a brighter version of the specified <code>Color</code>.
	 */
	public static Color brighter(Color color) {
		return brighter(color, 0.251f);
	}

	/**
	 * Creates a new <code>Color</code> that is a brighter version of the specified <code>Color</code>.
	 * <p>
	 * This method convert color's RGB components into an HSL color model.<br>
	 * After what the <strong>Lightness</strong> component is <strong>increased</strong> with the specified factor
	 * <code>(lightness = lightness * (1+factor))</code><br>
	 * <ul>
	 * <li>A factor of 0.0f will result in the same color as the original.</li>
	 * <li>A factor of 1.0f will result in the WHITE color</li>
	 * </ul>
	 * <p>
	 * Be carefull, this method don't provide same result than the {@link Color#brighter()} method.
	 * 
	 * @return a new <code>Color</code> object that is a brighter version of the specified <code>Color</code>.
	 */
	public static Color brighter(Color color, float factor) {
		if (factor == 0)
			return color;
		if (factor >= 1)
			return getColor(Color.white, color.getAlpha());

		float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

		hsb[2] = hsb[2] * (1 + factor);
		hsb[2] = (hsb[2] == 0) ? factor : ((hsb[2] < 1) ? hsb[2] : 1);

		return getColor((Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0x00FFFFFF) | (color.getAlpha() << 24));
	}
}
