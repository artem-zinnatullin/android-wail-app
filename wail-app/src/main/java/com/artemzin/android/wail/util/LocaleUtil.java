package com.artemzin.android.wail.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.artemzin.android.wail.storage.WAILSettings;

import java.util.LinkedHashMap;
import java.util.Locale;

public class LocaleUtil {

    /**
     * Parses languages array where lang presented as: "android-lang-code|Display name",
     * for example: "en|English", "ru|Russian" and so on
     *
     * @param languagesArray array of languages mappings
     * @return Map(LangCode, LangDisplayName)
     */
    public static LinkedHashMap<String, String> parseLanguagesMapping(@NonNull String[] languagesArray) {
        LinkedHashMap<String, String> languages = new LinkedHashMap<>(languagesArray.length);

        for (String lang : languagesArray) {
            String[] langMapping = lang.split(";");
            languages.put(langMapping[0], langMapping[1]);
        }

        return languages;
    }

    @Nullable private static String lang(String originalLangName) {
        return originalLangName == null ? null : originalLangName.substring(0, 2).toLowerCase();
    }

    public static void setLanguage(@NonNull Context context, @Nullable String newLanguage) {
        Configuration configuration = new Configuration();
        String language = WAILSettings.getLanguage(context);

        String lang = lang(language);
        String newLang = lang(newLanguage);

        if (TextUtils.isEmpty(language) && newLanguage == null) {
            configuration.locale = Locale.getDefault();
        } else if (newLanguage != null) {
            configuration.locale = new Locale(newLang);
            WAILSettings.setLanguage(context, newLanguage);
        } else if (!TextUtils.isEmpty(language)) {
            configuration.locale = new Locale(lang);
        }

        context.getResources().updateConfiguration(configuration, null);

        if (newLang != null && !newLang.equals(lang)) {
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

}
