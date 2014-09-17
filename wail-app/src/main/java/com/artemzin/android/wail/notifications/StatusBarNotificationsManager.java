package com.artemzin.android.wail.notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.receiver.NotificationActionsReceiver;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.ui.activity.MainActivity;
import com.artemzin.android.wail.util.Loggi;

/**
 * @author Ilya Murzinov [murz42@gmail.com]
 */

public class StatusBarNotificationsManager {
    private static final int TRACK_SCROBBLING_NOTIFICATION_ID = 1;
    private static final int TRACK_LOVED_NOTIFICATION_ID = 2;

    private static volatile StatusBarNotificationsManager instance;
    private Context context;

    private StatusBarNotificationsManager(Context context) {
        this.context = context;
    }

    public static StatusBarNotificationsManager getInstance(Context context) {
        if (instance == null) {
            synchronized (StatusBarNotificationsManager.class) {
                if (instance == null) {
                    instance = new StatusBarNotificationsManager(context);
                }
            }
        }
        return instance;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    public void showTrackScrobblingStatusBarNotification(Track track) {
        if (!WAILSettings.isStatusBarNotificationTrackScrobblingEnabled(context)) {
            Loggi.i("StatusBarNotificationsManager: Status bar notifications are disabled, skipping");
            return;
        }

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(resultIntent);
        PendingIntent intent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent loveIntent = new Intent(context, NotificationActionsReceiver.class);
        PendingIntent lovePendingIntent = PendingIntent.getBroadcast(context, 0, loveIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notifications_now_scrobbling))
                .setContentText(track.getArtist() + " - " + track.getTrack())
                .setSmallIcon(R.drawable.ic_status_wail_notifications)
                .setContentIntent(intent)
                .addAction(
                        R.drawable.ic_status_wail_love_track,
                        context.getString(R.string.notifications_love_current_track),
                        lovePendingIntent
                )
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        getNotificationManager().notify(TRACK_SCROBBLING_NOTIFICATION_ID, notification);
    }

    public void hideTrackScrobblingStatusBarNotification() {
        if (!WAILSettings.isStatusBarNotificationTrackScrobblingEnabled(context)) {
            Loggi.i("StatusBarNotificationsManager: Status bar notifications are disabled, skipping");
            return;
        }

        getNotificationManager().cancel(TRACK_SCROBBLING_NOTIFICATION_ID);
    }

    public void showTrackLovedStatusBarNotification(Track track) {
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notifications_track_loved))
                .setContentText(track.getArtist() + " - " + track.getTrack())
                .setSmallIcon(R.drawable.ic_status_wail_notifications)
                .build();

        getNotificationManager().notify(TRACK_LOVED_NOTIFICATION_ID, notification);
    }

    public void hideTrackLovedStatusBarNotification() {
        getNotificationManager().cancel(TRACK_LOVED_NOTIFICATION_ID);
    }

    public void cancelAllNotifications() {
        getNotificationManager().cancelAll();
    }
}
