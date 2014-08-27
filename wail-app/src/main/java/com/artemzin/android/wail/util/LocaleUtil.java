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

        String lang = language.substring(0, 2).toLowerCase();
        String newLang = newLanguage == null
                ? null
                : newLanguage.substring(0, 2).toLowerCase();

        if (TextUtils.isEmpty(language) && newLanguage == null) {
            configuration.locale = Locale.getDefault();
        } else if (newLanguage != null) {
            configuration.locale = new Locale(newLang);
            WAILSettings.setLanguage(context, newLanguage);
        } else if (!TextUtils.isEmpty(language)) {
            configuration.locale = new Locale(lang);
        }

        context.getResources().updateConfiguration(configuration, null);

        if (newLang != null && !lang.equals(newLang)) {
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

}
