package com.artemzin.android.wail.ui.fragment.settings;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artemzin.android.bytes.ui.ViewUtil;
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
import butterknife.OnClick;

/**
 * @author Ilya Murzinov [murz42@gmail.com]
 */
public class SettingsStatusBarNotificationsFragment extends BaseFragment {

    private final String GA_EVENT_SETTINGS_STATUS_BAR_NOTIFICATIONS = "SettingsStatusBarNotifications";

    @InjectView(R.id.settings_status_bar_notifications_track_now_scrobbling_switch)
    public SwitchCompat trackNowScrobblingStatusBarNotificationsSwitch;

    @InjectView(R.id.settings_status_bar_notifications_min_priority)
    public View statusBarNotificationsMinPriority;

    @InjectView(R.id.settings_status_bar_notifications_min_priority_switch)
    public SwitchCompat trackNowScrobblingStatusBarNotificationsMinPriority;

    @OnClick(R.id.settings_status_bar_notifications_track_now_scrobbling)
    public void onStatusBarNotificationClick() {
        View switchView = getActivity().findViewById(R.id.settings_status_bar_notifications_track_now_scrobbling_switch);
        onStatusBarNotificationSwitchChanged(((SwitchCompat) switchView).isChecked());
        ((SwitchCompat) switchView).setChecked(!((SwitchCompat) switchView).isChecked());
    }

    @OnCheckedChanged(R.id.settings_status_bar_notifications_track_now_scrobbling_switch)
    public void onStatusBarNotificationSwitchChanged(boolean isChecked) {
        if (isChecked == WAILSettings.isStatusBarNotificationTrackScrobblingEnabled(getActivity())) {
            return;
        }

        WAILSettings.setStatusBarNotificationTrackScrobblingEnabled(getActivity(), isChecked);

        if (isChecked) {
            Track track = WAILSettings.getNowScrobblingTrack(getActivity());
            if (track != null) {
                StatusBarNotificationsManager.getInstance(getActivity())
                        .showTrackScrobblingStatusBarNotification(track);
            }
            setMinPriorityEnabled(true);
        } else {
            StatusBarNotificationsManager.getInstance(getActivity()).cancelAllNotifications();
            setMinPriorityEnabled(false);
        }

        EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(GA_EVENT_SETTINGS_STATUS_BAR_NOTIFICATIONS,
                "nowPlayingStatusBarNotifications",
                isChecked ? "enabled" : "disabled",
                isChecked ? 1L : 0L).build());
    }

    @OnClick(R.id.settings_status_bar_notifications_min_priority)
    public void onStatusBarNotificationMinPriorityClick() {
        View switchView = getActivity().findViewById(R.id.settings_status_bar_notifications_min_priority_switch);
        onStatusBarNotificationMinPrioritySwitchChanged(((SwitchCompat) switchView).isChecked());
        ((SwitchCompat) switchView).setChecked(!((SwitchCompat) switchView).isChecked());
    }

    @OnCheckedChanged(R.id.settings_status_bar_notifications_min_priority_switch)
    public void onStatusBarNotificationMinPrioritySwitchChanged(boolean isChecked) {
        if (isChecked == WAILSettings.isStatusBarNotificationMinPriority(getActivity())) {
            return;
        }

        WAILSettings.setStatusBarNotificationMinPriority(getActivity(), isChecked);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_status_bar_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        boolean statusBarNotificationTrackScrobblingEnabled = WAILSettings.isStatusBarNotificationTrackScrobblingEnabled(getActivity());
        trackNowScrobblingStatusBarNotificationsSwitch
                .setChecked(statusBarNotificationTrackScrobblingEnabled);
        trackNowScrobblingStatusBarNotificationsMinPriority
                .setChecked(WAILSettings.isStatusBarNotificationMinPriority(getActivity()));

        if (!statusBarNotificationTrackScrobblingEnabled) {
            setMinPriorityEnabled(false);
        }
    }

    private void setMinPriorityEnabled(boolean enabled) {
        ViewUtil.setEnabledForAllChildrenRecursively((ViewGroup) statusBarNotificationsMinPriority, enabled);
        statusBarNotificationsMinPriority.setEnabled(enabled);
    }
}
