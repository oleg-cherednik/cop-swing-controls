package cop.swing.painters.enums;

/**
 * @author Oleg Cherednik
 * @since 05.04.2012
 */
public enum HorizontalAlignment {
	LEFT {
		@Override
		public int getX(int imageWidth, int width, int insetLeft, int insetRight) {
			return insetLeft;
		}
	},
	CENTER {
		@Override
		public int getX(int imageWidth, int width, int insetLeft, int insetRight) {
			return (width - imageWidth) / 2 + insetLeft;
		}
	},
	RIGHT {
		@Override
		public int getX(int imageWidth, int width, int insetLeft, int insetRight) {
			return (width - imageWidth) - insetRight;
		}
	};

	public abstract int getX(int imageWidth, int width, int insetLeft, int insetRight);
}
