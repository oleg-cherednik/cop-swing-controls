package cop.swing.painters.enums;

/**
 * @author Oleg Cherednik
 * @since 05.04.2012
 */
public enum VerticalAlignment {
	TOP {
		@Override
		public int getY(int imageHeight, int height, int insetTop, int insetBottom) {
			return insetTop;
		}
	},
	CENTER {
		@Override
		public int getY(int imageHeight, int height, int insetTop, int insetBottom) {
			return (height - imageHeight) / 2 + insetTop;
		}
	},
	BOTTOM {
		@Override
		public int getY(int imageHeight, int height, int insetTop, int insetBottom) {
			return height - imageHeight - insetBottom;
		}
	};

	public abstract int getY(int imageHeight, int height, int insetTop, int insetBottom);
}
