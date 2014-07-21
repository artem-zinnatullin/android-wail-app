package com.artemzin.android.wail.ui.fragment.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.notifications.SoundNotificationsManager;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.artemzin.android.wail.util.Loggi;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class SettingsSoundNotificationsFragment extends BaseFragment implements View.OnClickListener {

    private final String GA_EVENT_SETTINGS_SOUND_NOTIFICATIONS = "SettingsSoundNotifications";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_sound_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Activity activity = getActivity();

        view.findViewById(R.id.settings_sound_notifications_track_marked_as_scrobbled).setOnClickListener(this);
        Switch trackMarkedAsScrobbledSoundSwitch = (Switch) view.findViewById(R.id.settings_sound_notifications_track_marked_as_scrobbled_switch);

        trackMarkedAsScrobbledSoundSwitch.setChecked(WAILSettings.isSoundNotificationTrackMarkedAsScrobbledEnabled(activity));
        trackMarkedAsScrobbledSoundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WAILSettings.setSoundNotificationTrackMarkedAsScrobbledEnabled(activity, isChecked);
                EasyTracker.getInstance(activity).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_SOUND_NOTIFICATIONS,
                        "trackMarkedAsScrobbledSoundSwitch",
                        isChecked ? "enabled" : "disabled",
                        isChecked ? 1L : 0L).build());
            }
        });

        view.findViewById(R.id.settings_sound_notifications_track_skipped).setOnClickListener(this);
        Switch trackSkippedSoundSwitch = (Switch) view.findViewById(R.id.settings_sound_notifications_track_skipped_switch);

        trackSkippedSoundSwitch.setChecked(WAILSettings.isSoundNotificationTrackSkippedEnabled(activity));
        trackSkippedSoundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WAILSettings.setSoundNotificationTrackSkippedEnabled(activity, isChecked);
                EasyTracker.getInstance(activity).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_SOUND_NOTIFICATIONS,
                        "trackSkippedSoundSwitch",
                        isChecked ? "enabled" : "disabled",
                        isChecked ? 1L : 0L).build());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.settings_sound_notifications_track_marked_as_scrobbled) {
            tryToPlayTrackMarkedAsScrobbledSound();
        } else if (v.getId() == R.id.settings_sound_notifications_track_skipped) {
            tryToPlayTrackSkippedSound();
        }
    }

    private void tryToPlayTrackMarkedAsScrobbledSound() {
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

    private void tryToPlayTrackSkippedSound() {
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
