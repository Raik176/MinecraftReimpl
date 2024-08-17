package org.rhm.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static void log(String type, String message) {
        log(type, message, true);
    }

    private static void log(String type, String message, boolean doFormat) {
        String prepend = ConsoleFormats.ANSI_RESET + DateTimeFormatter.ofPattern("[HH:mm:ss] ").format(LocalDateTime.now())
                + type + " ";
        message = message.replaceAll("\n", "\n" + prepend);

        System.out.println(prepend + (doFormat ? Utils.formatColorCodes(message) : message));
    }

    public static void info(Object obj) {
        info(obj, true);
    }

    public static void info(Object obj, boolean doFormat) {
        log((Utils.isColorSupported() ? ConsoleFormats.ANSI_WHITE : "") + "INFO ", obj.toString(), doFormat);
    }

    public static void warn(Object obj) {
        log((Utils.isColorSupported() ? ConsoleFormats.ANSI_YELLOW : "") + "WARN ", obj.toString());
    }

    public static void error(Object obj) {
        log((Utils.isColorSupported() ? ConsoleFormats.ANSI_RED : "") + "ERROR", obj.toString());
    }

    public static void error(Exception err) {
        error(err, "");
    }

    public static void error(Exception err, String context) {
        String prefix = (Utils.isColorSupported() ? ConsoleFormats.ANSI_RED : "") + "ERROR";
        if (err == null) {
            log(prefix, "An unknown error occurred.", false);
            return;
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        err.printStackTrace(pw);

        log(prefix,
                (context.isEmpty() ? "" : context + "\n") + sw.toString().trim(), false);
    }

    public static void debug(Object obj) {
        debug(obj, true);
    }

    public static void debug(Object obj, boolean doFormat) {
        log((Utils.isColorSupported() ? ConsoleFormats.ANSI_BLUE : "") + "DEBUG", obj.toString(), doFormat);
    }
}
