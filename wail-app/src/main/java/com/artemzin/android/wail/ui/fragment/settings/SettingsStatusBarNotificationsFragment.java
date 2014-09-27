package com.artemzin.android.wail.ui.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.notifications.StatusBarNotificationsManager;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.ui.fragment.BaseFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;

/**
 * @author Ilya Murzinov [murz42@gmail.com]
 */
public class SettingsStatusBarNotificationsFragment extends BaseFragment {

    private final String GA_EVENT_SETTINGS_STATUS_BAR_NOTIFICATIONS = "SettingsStatusBarNotifications";

    @InjectView(R.id.settings_status_bar_notifications_track_now_scrobbling_switch)
    public Switch trackNowScrobblingStatusBarNotificationsSwitch;

    @OnCheckedChanged(R.id.settings_status_bar_notifications_track_now_scrobbling_switch)
    public void onStatusBarNotificationSwitchChanged(boolean isChecked) {
        WAILSettings.setStatusBarNotificationTrackScrobblingEnabled(getActivity(), isChecked);

        if (isChecked) {
            Track track = WAILSettings.getNowScrobblingTrack(getActivity());
            if (track != null) {
                StatusBarNotificationsManager.getInstance(getActivity())
                        .showTrackScrobblingStatusBarNotification(track);
            }
        } else {
            StatusBarNotificationsManager.getInstance(getActivity()).cancelAllNotifications();
        }

        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_STATUS_BAR_NOTIFICATIONS,
                "nowPlayingStatusBarNotifications",
                isChecked ? "enabled" : "disabled",
                isChecked ? 1L : 0L).build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_settings_status_bar_notifications, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        trackNowScrobblingStatusBarNotificationsSwitch
                .setChecked(WAILSettings.isStatusBarNotificationTrackScrobblingEnabled(getActivity()));
    }
}
