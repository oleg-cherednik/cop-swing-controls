package cop.swing.painters.enums;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * An enum representing the possible interpolation values of Bicubic, Bilinear, and Nearest Neighbor. These map to the
 * underlying RenderingHints, but are easier to use and serialization safe.
 * 
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public enum Interpolation {
	BICUBIC(RenderingHints.VALUE_INTERPOLATION_BICUBIC),
	BILINEAR(RenderingHints.VALUE_INTERPOLATION_BILINEAR),
	NEAREST_NEIGHBOR(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

	private final Object hint;

	Interpolation(Object hint) {
		this.hint = hint;
	}

	public void configureGraphics(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
	}
}
