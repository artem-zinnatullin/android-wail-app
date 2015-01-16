package com.artemzin.android.wail.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.artemzin.android.wail.storage.WAILSettings;

import java.util.Locale;

public class LocaleUtil {

    public static void updateLanguage(Context context, String newLanguage) {
        Configuration configuration = new Configuration();
        String language = WAILSettings.getLanguage(context);

        if (TextUtils.isEmpty(language) && newLanguage == null) {
            configuration.locale = Locale.getDefault();
        } else if (newLanguage != null) {
            configuration.locale = new Locale(newLanguage);
            WAILSettings.setLanguage(context, newLanguage);
        } else if (!TextUtils.isEmpty(language)) {
            configuration.locale = new Locale(language);
        }

        context.getResources().updateConfiguration(configuration, null);

        if (newLanguage != null && !language.equals(newLanguage)) {
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

}
