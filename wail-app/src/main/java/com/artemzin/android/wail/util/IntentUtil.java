package com.artemzin.android.wail.util;

import android.content.Intent;
import android.os.Bundle;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class IntentUtil {

    private IntentUtil() {}

    public static long getLongOrIntExtra(Intent intent, long defaultValue, String... possibleExtraNames) {
        if (intent == null || possibleExtraNames == null || possibleExtraNames.length == 0) return defaultValue;

        Bundle extras = intent.getExtras();

        if (extras == null || extras.isEmpty()) return defaultValue;

        for (String possibleExtraName : possibleExtraNames) {
            if (extras.containsKey(possibleExtraName)) {
                Object object = extras.get(possibleExtraName);

                if (object instanceof Long) {
                    return (Long) object;
                } else if (object instanceof  Integer) {
                    return (Integer) object;
                } else if (object instanceof Short) {
                    return (Short) object;
                }
            }
        }

        return defaultValue;
    }

    public static Boolean getBoolOrNumberAsBoolExtra(Intent intent, Boolean defaultValue, String... possibleExtraNames) {
        if (intent == null || possibleExtraNames == null || possibleExtraNames.length == 0) return defaultValue;

        Bundle extras = intent.getExtras();

        if (extras == null || extras.isEmpty()) return defaultValue;

        for (String possibleExtraName : possibleExtraNames) {
            if (extras.containsKey(possibleExtraName)) {
                Object object = extras.get(possibleExtraName);

                if (object instanceof Boolean) {
                    return (Boolean) object;
                } else if (object instanceof Integer) {
                    return (Integer) object > 0;
                } else if (object instanceof Long){
                    return (Long) object > 0;
                } else if (object instanceof Short) {
                    return (Short) object > 0;
                } else if (object instanceof Byte) {
                    return (Byte) object > 0;
                }
            }
        }

        return defaultValue;
    }

    public static String getIntentAsString(Intent intent) {
        if (intent == null) return "null intent";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Intent action: ").append(intent.getAction());

        if (intent.getExtras() == null || intent.getExtras().isEmpty()) {
            stringBuilder.append(", no extras");
        } else {
            stringBuilder.append(", extras: ");
            for (String key : intent.getExtras().keySet()) {
                stringBuilder.append("(" + key + ", " + intent.getExtras().get(key) + ")")
                .append(", ");
            }
        }

        return stringBuilder.toString();
    }
}
