package com.artemzin.android.wail.util;

import android.util.Log;

public class Loggi {

    private static final String TAG = "WAIL";
    private static final boolean isEnabled = true;

    private Loggi() {}

    public static void d(String message) {
        if (isEnabled) {
            Log.d(TAG, message);
        }
    }

    public static void v(String message) {
        if (isEnabled) {
            Log.v(TAG, message);
        }
    }

    public static void i(String message) {
        if (isEnabled) {
            Log.i(TAG, message);
        }
    }

    public static void w(String message) {
        if (isEnabled) {
            Log.w(TAG, message);
        }
    }

    public static void e(String message) {
        if (isEnabled) {
            Log.e(TAG, message);
        }
    }
}
