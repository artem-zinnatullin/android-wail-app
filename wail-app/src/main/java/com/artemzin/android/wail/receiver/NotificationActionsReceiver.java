package com.artemzin.android.wail.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.artemzin.android.wail.notifications.StatusBarNotificationsManager;
import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.model.Track;

/**
 * Created by Ilya Murzinov [murz42@gmail.com]
 */
public class NotificationActionsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Track track = WAILSettings.getNowScrobblingTrack(context);
        if (track != null) {
            context.startService(
                    new Intent(context, WAILService.class)
                            .setAction(WAILService.INTENT_ACTION_HANDLE_LOVED_TRACK)
            );
            StatusBarNotificationsManager.getInstance(context).showTrackLovedStatusBarNotification(track);
        }
    }
}
