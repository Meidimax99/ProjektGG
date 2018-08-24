package de.gg.util;

import java.util.Date;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

/**
 * This utility class takes care of the various logging frameworks. First either
 * {@link #enableDebugLogging()} or {@link #disableDebugLogging()} should get
 * called to set an active log level.
 */
public class Log {

	public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static final String INFO_TAG_FORMAT = "%tT - [INFO ] [%S]";
	private static final String ERROR_TAG_FORMAT = "%tT - [ERROR] [%S]";
	private static final String DEBUG_TAG_FORMAT = "%tT - [DEBUG] [%S]";

	private Log() {
		// not used
	}

	/**
	 * Enables debug log messages.
	 */
	public static void enableDebugLogging() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		com.esotericsoftware.minlog.Log.INFO();
	}

	/**
	 * Disabled debug logging. Only normal informational messages are shown.
	 */
	public static void disableDebugLogging() {
		Gdx.app.setLogLevel(Application.LOG_INFO);
		com.esotericsoftware.minlog.Log.ERROR();
	}

	/**
	 * Logs an informal message. The message is formatted via
	 * {@link String#format(String, Object...)}.
	 *
	 * @param tag
	 *            The tag in front of the message. Is usually used to denote the
	 *            logging entity.
	 * @param message
	 *            The actual log message.
	 * @param args
	 *            The arguments referenced by the format specifiers in the
	 *            message string. If there are more arguments than format
	 *            specifiers, the extra arguments are ignored. The number of
	 *            arguments is variable and may be zero.
	 * @see Formatter
	 */
	public static void info(String tag, String message, Object... args) {
		if (Gdx.app.getLogLevel() >= Application.LOG_INFO)
			Gdx.app.log(getTag(INFO_TAG_FORMAT, tag),
					String.format(message, args));
	}

	/**
	 * Logs an <i>error</i> message. The message is formatted via
	 * {@link String#format(String, Object...)}.
	 *
	 * @param tag
	 *            The tag in front of the message. Is usually used to denote the
	 *            logging entity.
	 * @param message
	 *            The actual error message.
	 * @param args
	 *            The arguments referenced by the format specifiers in the
	 *            message string. If there are more arguments than format
	 *            specifiers, the extra arguments are ignored. The number of
	 *            arguments is variable and may be zero.
	 * @see Formatter
	 */
	public static void error(String tag, String message, Object... args) {
		if (Gdx.app.getLogLevel() >= Application.LOG_ERROR)
			Gdx.app.error(getTag(ERROR_TAG_FORMAT, tag),
					String.format(message, args));
	}

	/**
	 * Logs a <i>debug</i> message. The message is formatted via
	 * {@link String#format(String, Object...)}.
	 *
	 * @param tag
	 *            The tag in front of the message. Is usually used to denote the
	 *            logging entity.
	 * @param message
	 *            The actual log message.
	 * @param args
	 *            The arguments referenced by the format specifiers in the
	 *            message string. If there are more arguments than format
	 *            specifiers, the extra arguments are ignored. The number of
	 *            arguments is variable and may be zero.
	 * @see Formatter
	 * @see #enableDebugLogging()
	 */
	public static void debug(String tag, String message, Object... args) {
		if (Gdx.app.getLogLevel() >= Application.LOG_DEBUG)
			Gdx.app.debug(getTag(DEBUG_TAG_FORMAT, tag),
					String.format(message, args));
	}

	private static final String getTag(String formatString, String tag) {
		return String.format(formatString, new Date(), tag);
	}

}
