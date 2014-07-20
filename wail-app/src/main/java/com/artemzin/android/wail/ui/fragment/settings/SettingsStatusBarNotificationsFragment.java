package com.artemzin.android.wail.ui.fragment.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.notifications.StatusBarNotificationsManager;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * @author Ilya Murzinov [murz42@gmail.com]
 */
public class SettingsStatusBarNotificationsFragment extends BaseFragment {

    private final String GA_EVENT_SETTINGS_STATUS_BAR_NOTIFICATIONS = "SettingsStatusBarNotifications";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_status_bar_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Activity activity = getActivity();

        Switch trackNowScrobblingStatusBarNotificationsSwitch =
                (Switch) view.findViewById(R.id.settings_status_bar_notifications_track_now_scrobbling_switch);
        trackNowScrobblingStatusBarNotificationsSwitch.setChecked(WAILSettings.isStatusBarNotificationTrackScrobblingEnabled(activity));
        trackNowScrobblingStatusBarNotificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                WAILSettings.setStatusBarNotificationTrackScrobblingEnabled(activity, isChecked);

                StatusBarNotificationsManager.getInstance(getActivity()).hideTrackScrobblingStatusBarNotification();

                EasyTracker.getInstance(activity).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_STATUS_BAR_NOTIFICATIONS,
                        "nowPlayingStatusBarNotifications",
                        isChecked ? "enabled" : "disabled",
                        isChecked? 1L : 0L).build());
            }
        });
    }
}
