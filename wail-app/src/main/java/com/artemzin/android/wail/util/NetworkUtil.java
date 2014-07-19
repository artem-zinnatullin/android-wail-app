package com.artemzin.android.wail.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    private NetworkUtil() {}

    public static boolean isAvailable(Context context) {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); // could be null in airplane mode
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static boolean isMobileNetwork(Context context) {
        if (!isAvailable(context)) {
            return false;
        }

        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return networkInfo != null && !networkInfo.isConnected();

    }
}
