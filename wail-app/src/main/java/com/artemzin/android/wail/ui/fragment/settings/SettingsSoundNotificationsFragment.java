package com.artemzin.android.wail.ui.fragment.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.notifications.SoundNotificationsManager;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.artemzin.android.wail.util.Loggi;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class SettingsSoundNotificationsFragment extends BaseFragment {

    private final String GA_EVENT_SETTINGS_SOUND_NOTIFICATIONS = "SettingsSoundNotifications";

    @InjectView(R.id.settings_sound_notifications_track_marked_as_scrobbled_switch)
    public SwitchCompat trackMarkedAsScrobbledSoundSwitch;

    @InjectView(R.id.settings_sound_notifications_track_skipped_switch)
    public SwitchCompat trackSkippedSoundSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_sound_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        final Activity activity = getActivity();

        trackMarkedAsScrobbledSoundSwitch.setChecked(WAILSettings.isSoundNotificationTrackMarkedAsScrobbledEnabled(activity));

        trackSkippedSoundSwitch.setChecked(WAILSettings.isSoundNotificationTrackSkippedEnabled(activity));
    }

    @OnCheckedChanged(R.id.settings_sound_notifications_track_marked_as_scrobbled_switch)
    public void onTrackMarkedAsScrobbledChanged(boolean isChecked) {
        final Activity activity = getActivity();

        if (isChecked == WAILSettings.isSoundNotificationTrackMarkedAsScrobbledEnabled(activity)) {
            return;
        }

        WAILSettings.setSoundNotificationTrackMarkedAsScrobbledEnabled(activity, isChecked);
        EasyTracker.getInstance(activity).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_SOUND_NOTIFICATIONS,
                "trackMarkedAsScrobbledSoundSwitch",
                isChecked ? "enabled" : "disabled",
                isChecked ? 1L : 0L).build());
    }

    @OnCheckedChanged(R.id.settings_sound_notifications_track_skipped_switch)
    public void onTrackSkippedChanged(boolean isChecked) {
        final Activity activity = getActivity();

        if (isChecked == WAILSettings.isSoundNotificationTrackSkippedEnabled(activity)) {
            return;
        }

        WAILSettings.setSoundNotificationTrackSkippedEnabled(activity, isChecked);
        EasyTracker.getInstance(activity).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_SOUND_NOTIFICATIONS,
                "trackSkippedSoundSwitch",
                isChecked ? "enabled" : "disabled",
                isChecked ? 1L : 0L).build());
    }

    @OnClick(R.id.settings_sound_notifications_track_marked_as_scrobbled)
    public void tryToPlayTrackMarkedAsScrobbledSound() {
        Activity activity = getActivity();
        EasyTracker.getInstance(activity).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_SOUND_NOTIFICATIONS,
                "playTrackMarkedAsScrobbledSound",
                null,
                1L).build());

        try {
            SoundNotificationsManager.getInstance(activity).playTrackMarkedAsScrobbledSound(true);
        } catch (Exception e) {
            Loggi.e("SettingsSoundNotificationsFragment.tryToPlayTrackMarkedAsScrobbledSound() exception: " + e);
            Toast.makeText(activity, R.string.settings_sound_notifications_toast_can_not_play_sound, Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.settings_sound_notifications_track_skipped)
    public void tryToPlayTrackSkippedSound() {
        Activity activity = getActivity();
        EasyTracker.getInstance(activity).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_SOUND_NOTIFICATIONS,
                "playTrackSkippedSound",
                null,
                1L).build());

        try {
            SoundNotificationsManager.getInstance(activity).playTrackSkippedSound(true);
        } catch (Exception e) {
            Loggi.e("SettingsSoundNotificationsFragment.tryToPlayTrackSkippedSound() exception: " + e);
            Toast.makeText(activity, R.string.settings_sound_notifications_toast_can_not_play_sound, Toast.LENGTH_LONG).show();
        }
    }
}
