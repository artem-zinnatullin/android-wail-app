package com.artemzin.android.wail.util;

import android.os.SystemClock;

public class ThreadUtil {

    private ThreadUtil() {}

    /**
     * If action happens faster then required it will call sleep for the thread
     * Usefully for better user experience from UI
     * @param startTimeInMillis please use SystemClock.elapsedRealtime() to prevent bug when user changing datetime in system
     * @param minDurationInMillis action's minimal required duration in millis
     */
    public static void sleepIfRequired(long startTimeInMillis, long minDurationInMillis) {
        final long realDurationInMillis = SystemClock.elapsedRealtime() - startTimeInMillis;

        if (realDurationInMillis < minDurationInMillis - 100) {
            SystemClock.sleep(minDurationInMillis - realDurationInMillis);
        }
    }
}