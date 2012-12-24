package cop.swing.painters.enums;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public enum FillStyle {
	/** Indicates that neither the fill area nor the outline should be painted */
	NONE,
	/** Indicates that both the outline, and the fill should be painted. <b>Default value.</b> */
	BOTH,
	/** Indicates that the shape should be filled, but no outline painted */
	FILLED,
	/** Specifies that the shape should be outlined, but not filled */
	OUTLINE
}
