package shape.tos;

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;

public final class TranslucencyManager {
	private static final TranslucencyManager INSTANCE = new TranslucencyManager();

	public static TranslucencyManager getInstance() {
		return INSTANCE;
	}

	private final boolean supported;

	private TranslucencyManager() {
		supported = isTranslucencySupported();
	}

	// ========== static ==========

	private static boolean isTranslucencySupported() {
		if (isTranslucencySupported(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()))
			return true;

		for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
			if (isTranslucencySupported(device))
				return true;

		return false;
	}

	public static boolean isTranslucencySupported(GraphicsDevice device) {
		if (device == null)
			return false;

		for (WindowTranslucency translucency : WindowTranslucency.values())
			if (!device.isWindowTranslucencySupported(translucency))
				return false;

		return true;

	}
}
