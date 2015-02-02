package com.artemzin.android.wail.ui.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.artemzin.android.bytes.ui.DisplayUnitsConverter;
import com.artemzin.android.bytes.ui.ViewUtil;
import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.db.AppDBManager;
import com.artemzin.android.wail.ui.activity.BaseActivity;
import com.artemzin.android.wail.ui.activity.NonAuthorizedActivity;
import com.artemzin.android.wail.ui.activity.settings.SettingsIgnoredPlayersActivity;
import com.artemzin.android.wail.ui.activity.settings.SettingsSelectLanguageActivity;
import com.artemzin.android.wail.ui.activity.settings.SettingsSoundNotificationsActivity;
import com.artemzin.android.wail.ui.activity.settings.SettingsStatusBarNotificationsActivity;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.artemzin.android.wail.ui.fragment.dialogs.DialogDecorator;
import com.artemzin.android.wail.ui.fragment.dialogs.DialogFragmentWithNumberPicker;
import com.artemzin.android.wail.ui.fragment.dialogs.DialogFragmentWithSeekBar;
import com.artemzin.android.wail.util.WordFormUtil;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsFragment extends BaseFragment implements DialogDecorator.Callback {

    public static final String GA_EVENT_SETTINGS_FRAGMENT = "SettingsFragment";

    @InjectView(R.id.settings_build_version_desc)
    public TextView buildVersionDescTextView;

    @InjectView(R.id.settings_select_language_description)
    public TextView languageMenuItemDescription;

    @InjectView(R.id.settings_disable_scrobbling_over_mobile_network_switch)
    public SwitchCompat isScrobblingOverMobileNetworkDisabledSwitch;

    @InjectView(R.id.settings_lastfm_update_nowplaying_switch)
    public SwitchCompat isLastfmUpdateNowplayingEnabledSwitch;

    @InjectView(R.id.settings_container)
    public View settingContainer;

    @InjectView(R.id.settings_min_track_duration_in_seconds_desc)
    public TextView minDurationInSecondsDescription;

    @InjectView(R.id.settings_min_track_duration_in_percents_desc)
    public TextView minDurationInPercentsDescription;

    @InjectView(R.id.settings_theme_switch)
    public SwitchCompat themeSwitch;

    @InjectView(R.id.settings_logout_description)
    public TextView logoutDescription;

    @OnClick(R.id.settings_ignored_players)
    public void onIgnoredPlayersClick() {
        startActivity(new Intent(getActivity(), SettingsIgnoredPlayersActivity.class));
    }

    @OnClick(R.id.settings_select_language_menu_item)
    public void onSelectLanguageClick() {
        startActivity(new Intent(getActivity(), SettingsSelectLanguageActivity.class));
    }

    @OnClick(R.id.settings_disable_scrobbling_over_mobile_network)
    public void onDisableScrobblingOverMobileChanged() {
        SwitchCompat switchView = (SwitchCompat) getActivity().findViewById(R.id.settings_disable_scrobbling_over_mobile_network_switch);
        onDisableScrobblingOverMobileChanged(switchView.isChecked());
        switchView.setChecked(!switchView.isChecked());
    }

    @OnCheckedChanged(R.id.settings_disable_scrobbling_over_mobile_network_switch)
    public void onDisableScrobblingOverMobileChanged(boolean isChecked) {
        if (isChecked == WAILSettings.isEnableScrobblingOverMobileNetwork(getActivity())) {
            return;
        }

        WAILSettings.setDisableScrobblingOverMobileNetwork(getActivity(), isChecked);

        final String toast = isChecked ? getString(R.string.settings_scrobbling_over_mobile_network_enabled_toast)
                : getString(R.string.settings_scrobbling_over_mobile_network_disabled_toast);

        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();

        EasyTracker.getInstance(getActivity()).send(
                MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT,
                        "Scrobbling over mobile network enabled: " + isChecked,
                        null,
                        isChecked ? 1L : 0L
                ).build()
        );
    }

    @OnClick(R.id.settings_lastfm_update_nowplaying)
    public void onLastfmUpdateNowPlayingChanged() {
        SwitchCompat switchView = (SwitchCompat) getActivity().findViewById(R.id.settings_lastfm_update_nowplaying_switch);
        onLastfmUpdateNowPlayingChanged(switchView.isChecked());
        switchView.setChecked(!switchView.isChecked());
    }

    @OnCheckedChanged(R.id.settings_lastfm_update_nowplaying_switch)
    public void onLastfmUpdateNowPlayingChanged(boolean isChecked) {
        if (isChecked == WAILSettings.isLastfmNowplayingUpdateEnabled(getActivity())) {
            return;
        }

        WAILSettings.setLastfmNowplayingUpdateEnabled(getActivity(), isChecked);

        final String toast = isChecked ? getString(R.string.settings_lastfm_update_nowplaying_enabled_toast)
                : getString(R.string.settings_lastfm_update_nowplaying_disabled_toast);

        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();

        EasyTracker.getInstance(getActivity()).send(
                MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT,
                        "lastFmUpdateNowPlaying enabled: " + isChecked,
                        null,
                        isChecked ? 1L : 0L
                ).build()
        );
    }

    @OnClick(R.id.settings_theme)
    public void onThemeChanged() {
        SwitchCompat switchView = (SwitchCompat) getActivity().findViewById(R.id.settings_theme_switch);
        onThemeChanged(switchView.isChecked());
        switchView.setChecked(!switchView.isChecked());
    }

    @OnCheckedChanged(R.id.settings_theme_switch)
    public void onThemeChanged(boolean isChecked) {
        if (isChecked == (WAILSettings.getTheme(getActivity()) == WAILSettings.Theme.DARK)) {
            return;
        }

        WAILSettings.setTheme(getActivity(), isChecked ? WAILSettings.Theme.DARK : WAILSettings.Theme.LIGHT);
        ((BaseActivity) getActivity()).setTheme();
        ((BaseActivity) getActivity()).restart();
    }

    @OnClick(R.id.settings_sound_notifications)
    public void onSoundNotificationSettingClick() {
        startActivity(new Intent(getActivity(), SettingsSoundNotificationsActivity.class));
    }

    @OnClick(R.id.settings_status_bar_notifications)
    public void onStatusBarNotificationSettingClick() {
        startActivity(new Intent(getActivity(), SettingsStatusBarNotificationsActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.settings_actionbar_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_settings, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_settings, menu);

        SwitchCompat isWailEnabledSwitch = (SwitchCompat) MenuItemCompat.getActionView(menu
                .findItem(R.id.main_settings_menu_is_wail_enabled));
        isWailEnabledSwitch.setChecked(WAILSettings.isEnabled(getActivity()));
        isWailEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WAILSettings.setEnabled(getActivity(), isChecked);

                final Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, (int) DisplayUnitsConverter.dpToPx(getActivity(), 60));

                if (isChecked) {
                    setUIStateWailEnabled();
                    toast.setText(R.string.settings_wail_enabled_toast);
                } else {
                    setUIStateWailDisabled();
                    toast.setText(R.string.settings_wail_disabled_toast);
                }

                toast.show();

                EasyTracker.getInstance(getActivity()).send(MapBuilder
                        .createEvent(
                                GA_EVENT_SETTINGS_FRAGMENT,
                                "isWailEnabled changed, enabled: " + isChecked,
                                null,
                                isChecked ? 1L : 0L).build()
                );
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        if (WAILSettings.isEnabled(getActivity())) {
            setUIStateWailEnabled();
        } else {
            setUIStateWailDisabled();
        }

        String lang = WAILSettings.getLanguage(getActivity());

        if (lang == null) {
            lang = getResources().getStringArray(R.array.settings_select_language_languages)[0];
        }

        languageMenuItemDescription.setText(lang);

        refreshMinTrackDurationInPercents();
        refreshMinTrackDurationInSeconds();

        isScrobblingOverMobileNetworkDisabledSwitch.setChecked(WAILSettings.isEnableScrobblingOverMobileNetwork(getActivity()));

        isLastfmUpdateNowplayingEnabledSwitch.setChecked(WAILSettings.isLastfmNowplayingUpdateEnabled(getActivity()));

        logoutDescription.setText(WAILSettings.getLastfmUserName(getActivity()));

        themeSwitch.setChecked(WAILSettings.getTheme(getActivity()) == WAILSettings.Theme.DARK);

        try {
            buildVersionDescTextView.setText(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (Exception e) {
            buildVersionDescTextView.setText("unknown");
            EasyTracker.getInstance(getActivity()).send(MapBuilder.createException("Can not set build version in settings: " + e.getMessage(), false).build());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT, "started", null, 1L).build());
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT, "stopped", null, 0L).build());
    }

    void setUIStateWailEnabled() {
        ViewUtil.setEnabledForAllChildrenRecursively((ViewGroup) settingContainer, true);
    }

    void setUIStateWailDisabled() {
        ViewUtil.setEnabledForAllChildrenRecursively((ViewGroup) settingContainer, false);
    }

    @OnClick(R.id.settings_min_track_duration_in_percents)
    void showMinTrackDurationInPercentsEditDialog() {
        final DialogFragmentWithSeekBar dialogFragmentWithSeekBar = DialogFragmentWithSeekBar.newInstance(
                getString(R.string.settings_min_track_elapsed_time_in_percent_dialog_title),
                getString(R.string.settings_min_track_elapsed_time_in_percent_dialog_description),
                WAILSettings.getMinTrackDurationInPercents(getActivity())
        );

        dialogFragmentWithSeekBar.setListener(this);

        dialogFragmentWithSeekBar.show(getFragmentManager(), "minTrackDurationInPercentsDialog");
    }

    @OnClick(R.id.settings_min_track_duration_in_seconds)
    void showMinTrackDurationInSecondsEditDialog() {
        final DialogFragmentWithNumberPicker minTrackDurationInSecondsDialog = DialogFragmentWithNumberPicker.newInstance(
                getString(R.string.settings_min_track_elapsed_time_in_seconds_dialog_title),
                30,
                600,
                WAILSettings.getMinTrackDurationInSeconds(getActivity()));

        minTrackDurationInSecondsDialog.setListener(this);

        minTrackDurationInSecondsDialog.show(getFragmentManager(), "minTrackDurationInSecondsDialog");
    }

    private void refreshMinTrackDurationInSeconds() {
        final int minTrackDurationInSeconds = WAILSettings.getMinTrackDurationInSeconds(getActivity());

        minDurationInSecondsDescription.setText(
                getString(
                        R.string.settings_min_track_elapsed_time_in_seconds_desc,
                        minTrackDurationInSeconds + " " + WordFormUtil.getWordForm(minTrackDurationInSeconds, getResources().getStringArray(R.array.word_forms_second))
                )
        );
    }

    private void refreshMinTrackDurationInPercents() {
        minDurationInPercentsDescription.setText(
                getString(
                        R.string.settings_min_track_elapsed_time_in_percent_desc,
                        WAILSettings.getMinTrackDurationInPercents(getActivity())
                )
        );
    }

    @OnClick(R.id.settings_logout_menu_item)
    public void logout() {
        new MaterialDialog.Builder(getActivity())
                .theme(Theme.DARK)
                .title(R.string.setting_logout_warning)
                .positiveText("Ok")
                .negativeText(R.string.dialog_cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        WAILSettings.clearAllSettings(getActivity());
                        AppDBManager.getInstance(getActivity()).clearAll();
                        startActivity(new Intent(getActivity(), NonAuthorizedActivity.class));
                        getActivity().finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).build().show();
    }

    @OnClick(R.id.settings_email_to_developers)
    public void emailToTheDeveloper() {
        try {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);

            String emailsString = getString(R.string.settings_developers_emails);
            String[] emails = emailsString.substring(0, emailsString.indexOf('(') - 1).split(",");

            emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_email_to_the_developer_subj) + " " + buildVersionDescTextView.getText().toString());
            emailIntent.setType("message/rfc822");

            startActivity(Intent.createChooser(emailIntent, getString(R.string.settings_email_dialog_title)));

            EasyTracker.getInstance(getActivity())
                    .send(MapBuilder.createEvent(GA_EVENT_SETTINGS_FRAGMENT,
                            "emailToTheDeveloperClicked",
                            null,
                            1L).build());
        } catch (Exception e) {
            EasyTracker.getInstance(getActivity())
                    .send(MapBuilder.createException("Can not send email to the developer: " + e, false).build());
        }
    }

    @Override
    public void onDismiss() {
        refreshMinTrackDurationInPercents();
        refreshMinTrackDurationInSeconds();
    }
}
