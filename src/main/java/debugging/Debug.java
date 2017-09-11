package debugging;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * A class for debugging functions.
 * @author N. H. Weideman
 */

public class Debug {

	public static final boolean DEBUG = false;

	private static final String DEBUG_PREFIX = "[DEBUG] ";

	public static void debug(Object o) {
		if (DEBUG) {
			System.err.print(DEBUG_PREFIX + o);
		}
	}

	public static void debugln(Object o) {
		if (DEBUG) {
			System.err.println(DEBUG_PREFIX + o);
		}
	}

	public static void debugStackTrace(Exception e) {
		if (DEBUG) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			String stackTraceStr = stringWriter.toString();

			String stackTraceDebugStr = stackTraceStr.replaceAll("\n", "\n" + DEBUG_PREFIX);
			System.err.println(stackTraceDebugStr);
		}
	}
	
}
