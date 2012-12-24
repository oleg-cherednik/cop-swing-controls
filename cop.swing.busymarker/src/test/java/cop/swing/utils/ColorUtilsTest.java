package cop.swing.utils;

import static cop.swing.utils.ColorUtils.*;
import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("static-method")
public class ColorUtilsTest {
	private static final int NO_ALPHA = 0xFF;
	private static final int ALPHA = 0xF;

	private static final int RED = 0x33;
	private static final int GREEN = 0x66;
	private static final int BLUE = 0x99;

	private static final Color COLOR_NO_ALPHA = new Color(RED, GREEN, BLUE, NO_ALPHA);
	private static final Color COLOR_ALPHA = new Color(RED, GREEN, BLUE, ALPHA);

	@Before
	public void setUp() throws Exception {
		Field field = ColorUtils.class.getDeclaredField("colors");
		field.setAccessible(true);
		((Map<?, ?>)field.get(null)).clear();
		field.setAccessible(false);
	}

	@Test
	public void testGetColor1() {
		assertEquals(getColor(COLOR_NO_ALPHA, NO_ALPHA).getRGB(), COLOR_NO_ALPHA.getRGB());
		assertEquals(getColor(COLOR_NO_ALPHA, ALPHA).getRGB(), COLOR_ALPHA.getRGB());

		assertEquals(getColor(COLOR_NO_ALPHA, NO_ALPHA), getColor(COLOR_NO_ALPHA, NO_ALPHA));
		assertEquals(getColor(COLOR_NO_ALPHA, ALPHA), getColor(COLOR_NO_ALPHA, ALPHA));
	}

	@Test
	public void testGetColor2() {
		assertEquals(getColor(RED, GREEN, BLUE, NO_ALPHA).getRGB(), COLOR_NO_ALPHA.getRGB());
		assertEquals(getColor(RED, GREEN, BLUE, ALPHA).getRGB(), COLOR_ALPHA.getRGB());

		assertEquals(getColor(RED, GREEN, BLUE, NO_ALPHA).getRGB(), getColor(RED, GREEN, BLUE, NO_ALPHA).getRGB());
		assertEquals(getColor(RED, GREEN, BLUE, ALPHA).getRGB(), getColor(RED, GREEN, BLUE, ALPHA).getRGB());
	}

	@Test
	public void testGetColor3() {
		assertEquals(getColor(COLOR_NO_ALPHA.getRGB()).getRGB(), COLOR_NO_ALPHA.getRGB());
		assertEquals(getColor(COLOR_ALPHA.getRGB()).getRGB(), COLOR_ALPHA.getRGB());

		assertEquals(getColor(COLOR_NO_ALPHA.getRGB()).getRGB(), getColor(COLOR_NO_ALPHA.getRGB()).getRGB());
		assertEquals(getColor(COLOR_ALPHA.getRGB()).getRGB(), getColor(COLOR_ALPHA.getRGB()).getRGB());
	}

	@Test
	public void testColor4() {
		assertEquals(getColor(Color.white.getRGB()), Color.white);
		assertEquals(getColor(Color.lightGray.getRGB()), Color.lightGray);
		assertEquals(getColor(Color.gray.getRGB()), Color.gray);
		assertEquals(getColor(Color.darkGray.getRGB()), Color.darkGray);
		assertEquals(getColor(Color.black.getRGB()), Color.black);
		assertEquals(getColor(Color.red.getRGB()), Color.red);
		assertEquals(getColor(Color.pink.getRGB()), Color.pink);
		assertEquals(getColor(Color.orange.getRGB()), Color.orange);
		assertEquals(getColor(Color.yellow.getRGB()), Color.yellow);
		assertEquals(getColor(Color.green.getRGB()), Color.green);
		assertEquals(getColor(Color.magenta.getRGB()), Color.magenta);
		assertEquals(getColor(Color.cyan.getRGB()), Color.cyan);
		assertEquals(getColor(Color.blue.getRGB()), Color.blue);
	}

	@Test
	public void testInterporate() {
		final Color color1 = getColor(Color.pink, 11);
		final Color color2 = getColor(Color.gray, 214);

		final Color color25 = new Color(223, 163, 163, 62);
		final Color color50 = new Color(192, 152, 152, 113);
		final Color color75 = new Color(160, 140, 140, 163);

		assertEquals(interpolate(color1, color2, 0).getRGB(), color1.getRGB());
		assertEquals(interpolate(color1, color2, 25).getRGB(), color25.getRGB());
		assertEquals(interpolate(color1, color2, 50).getRGB(), color50.getRGB());
		assertEquals(interpolate(color1, color2, 75).getRGB(), color75.getRGB());
		assertEquals(interpolate(color1, color2, 100).getRGB(), color2.getRGB());
	}
}
