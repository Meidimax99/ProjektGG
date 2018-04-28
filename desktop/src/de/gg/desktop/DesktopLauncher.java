package de.gg.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.gg.core.ProjektGG;
import de.gg.util.CrashLogUtils;
import de.gg.util.MicroOptions;

/**
 * Starts the application for the desktop-based builds.
 */
public class DesktopLauncher {

	/**
	 * The start-method for the whole application. Currently supported start
	 * arguments:
	 * <ul>
	 * <li>--debug: sets the game to debug mode.
	 * <li>--novid: skips the splash screen.
	 * <li>--fps: shows a fps counter in-game.
	 * </ul>
	 *
	 * @param args
	 *            The start arguments.
	 */
	public static void main(String[] args) {
		MicroOptions options = new MicroOptions();
		options.option("debug").describedAs("enables debugmode").isUnary();
		options.option("fps").describedAs("enables a fps counter").isUnary();
		options.option("novid").describedAs("no splashscreen").isUnary();
		options.option("width").describedAs("the width of the game's window");
		options.option("height").describedAs("the heigth of the game's window");
		try {
			options.parse(args);
		} catch (MicroOptions.OptionException e) {
			System.err.println("Usage:");
			exitWithError(options.usageString());
		}

		int width = 0, height = 0;
		try {
			width = Integer.valueOf(options.getArg("width", "1280")); // 1600
			height = Integer.valueOf(options.getArg("height", "720")); // 900
		} catch (NumberFormatException e) {
			exitWithError("the width and height parameter have to be integers");
		}

		if (width < 0 || height < 0)
			exitWithError(
					"the width and height parameter have to be positive integers");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = ProjektGG.name;
		config.height = height;
		config.width = width;
		config.resizable = false;
		config.addIcon("ui/images/icon16.png", Files.FileType.Absolute);
		config.addIcon("ui/images/icon32.png", Files.FileType.Absolute);
		config.addIcon("ui/images/icon48.png", Files.FileType.Absolute);

		try {
			// Start the game
			new LwjglApplication(new ProjektGG(options.has("debug"),
					!options.has("novid"), options.has("fps")), config);
		} catch (Exception e) {
			Gdx.app.error(ProjektGG.name,
					"An unexpected error occurred while starting the game", e);

			CrashLogUtils.writeCrashLogToFile(e, true);
		}
	}

	private static void exitWithError(String errorMsg) {
		System.err.println(errorMsg);
		System.exit(-1);
	}

}
