package com.artemzin.android.wail.ui.fragment.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.artemzin.android.wail.util.LocaleUtil;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.LinkedHashMap;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class SettingsSelectLanguageFragment extends BaseFragment {

    private final String GA_EVENT_SETTINGS_SELECT_LANGUAGE = "SettingsSelectLanguage";

    @InjectView(R.id.settings_select_language_list_view)
    public ListView languagesList;

    /**
     * Map (LangAndroidCode, LangDisplayName)
     */
    private LinkedHashMap<String, String> languagesMapping;

    @NonNull private static String[] markCurrentLanguageAsSelected(@NonNull Context context, @NonNull LinkedHashMap<String, String> languagesMapping) {
        final String currentLanguage = WAILSettings.getLanguage(context);
        final String[] langsDisplayNames = new String[languagesMapping.size()];

        int i = 0;

        for (String langCode : languagesMapping.keySet()) {
            final String langDisplayName = languagesMapping.get(langCode);

            if (currentLanguage.equalsIgnoreCase(langCode)) {
                langsDisplayNames[i] = context.getString(R.string.settings_select_language_current_lang, langDisplayName);
            } else {
                langsDisplayNames[i] = langDisplayName;
            }

            i++;
        }

        return langsDisplayNames;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languagesMapping = LocaleUtil.parseLanguagesMapping(getResources().getStringArray(R.array.supported_languages_mapping));
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_select_language, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        String[] languagesDisplayNames = markCurrentLanguageAsSelected(getActivity(), languagesMapping);

        BaseAdapter adapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.settings_select_language_item,
                languagesDisplayNames
        );

        languagesList.setAdapter(adapter);
    }

    @OnItemClick(R.id.settings_select_language_list_view)
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final String langCodeForAnalytics;

        switch (i) {
            case 0:
                LocaleUtil.setLanguage(getActivity(), Locale.getDefault().getDisplayLanguage());
                langCodeForAnalytics = "Auto";
                break;
            default:
                String languageCode = (String) languagesMapping.keySet().toArray()[i]; // thanks to LinkedHashMap "good" API
                LocaleUtil.setLanguage(getActivity(), languageCode);
                langCodeForAnalytics = languageCode;
                break;
        }

        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(
                GA_EVENT_SETTINGS_SELECT_LANGUAGE,
                "languageChangedTo",
                langCodeForAnalytics,
                0L
        ).build());

        getActivity().finish();
    }
}
