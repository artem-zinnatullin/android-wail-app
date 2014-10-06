package com.artemzin.android.wail.ui.fragment.settings;

import android.content.Context;
import android.os.Bundle;
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
import com.artemzin.android.wail.util.Loggi;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class SettingsSelectLanguageFragment extends BaseFragment implements ListView.OnItemClickListener {

    private final String GA_EVENT_SETTINGS_SELECT_LANGUAGE = "SettingsSelectLanguage";
    @InjectView(R.id.settings_select_language_list_view)
    public ListView languagesList;
    private String[] languages;

    private static String[] markCurrentLanguageAsSelected(Context context, String[] languages) {
        try {
            final String currentLanguage = WAILSettings.getLanguage(context);

            boolean languageWasSelected = false;

            for (int i = 0; i < languages.length; i++) {
                final String lang = languages[i];

                if (currentLanguage.equalsIgnoreCase(lang)) {
                    languages[i] = context.getString(R.string.settings_select_language_current_lang, lang);
                    languageWasSelected = true;
                    break;
                }
            }

            if (!languageWasSelected) {
                languages[0] = context.getString(R.string.settings_select_language_current_lang, languages[0]);
            }

            return languages;
        } catch (Exception e) {
            Loggi.e(e.toString());
            return languages;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_select_language, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        languages = markCurrentLanguageAsSelected(getActivity(), getResources().getStringArray(R.array.settings_select_language_languages));

        BaseAdapter adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                languages
        );

        languagesList.setAdapter(adapter);
        languagesList.setOnItemClickListener(this);
    }

    @OnItemClick(R.id.settings_select_language_list_view)
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                LocaleUtil.updateLanguage(getActivity(), Locale.getDefault().getDisplayLanguage());
                break;
            default:
                LocaleUtil.updateLanguage(getActivity(), languages[i]);
                break;
        }

        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(
                GA_EVENT_SETTINGS_SELECT_LANGUAGE,
                "languageChangedTo",
                i == 0 ? "default" : languages[i],
                0L
        ).build());

        getActivity().finish();
    }
}
