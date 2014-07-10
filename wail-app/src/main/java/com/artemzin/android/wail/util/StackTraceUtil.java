package com.artemzin.android.wail.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class StackTraceUtil {

    private StackTraceUtil() {}

    public static String getStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);

        String stackTrace = stringWriter.toString();

        try {
            stringWriter.close();
        } catch (Exception e1) {

        }

        try {
            printWriter.close();
        } catch (Exception e2) {

        }

        return stackTrace;
    }
}
