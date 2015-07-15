package com.artemzin.android.wail.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.util.AsyncTaskExecutor;
import com.artemzin.android.wail.util.Loggi;
import com.artemzin.android.wail.util.ThreadUtil;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class SoundNotificationsManager {

    private static final Uri ASSET_SOUND_MARKED_SCROBBLED_URI =
            Uri.parse("file:///android_asset/track_marked_as_scrobbled");
    private static final Uri ASSET_SOUND_SKIPPED_URI =
            Uri.parse("file:///android_asset/track_skipped");

    private Context context;
    private volatile long lastTrackSkippedPlayTime;

    private static volatile SoundNotificationsManager instance;
    private static int sNextId;

    private SoundNotificationsManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static SoundNotificationsManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SoundNotificationsManager.class) {
                if (instance == null) {
                    instance = new SoundNotificationsManager(context);
                }
            }
        }
        return instance;
    }

    public void playTrackSkippedSound() {
        playTrackSkippedSound(false);
    }

    public void playTrackSkippedSound(boolean force) {
        if (!force && !WAILSettings.isSoundNotificationTrackSkippedEnabled(context)) {
            Loggi.w("SoundNotificationsManager.playTrackSkippedSound() disabled");
            return;
        }

        if (SystemClock.elapsedRealtime() - lastTrackSkippedPlayTime < 300) {
            return;
        }

        lastTrackSkippedPlayTime = SystemClock.elapsedRealtime();

        emitSoundNotification(ASSET_SOUND_SKIPPED_URI);
    }

    public void playTrackMarkedAsScrobbledSound() {
        playTrackMarkedAsScrobbledSound(false);
    }

    public void playTrackMarkedAsScrobbledSound(boolean force) {
        if (!force && !WAILSettings.isSoundNotificationTrackMarkedAsScrobbledEnabled(context)) {
            Loggi.w("SoundNotificationsManager.playTrackMarkedAsScrobbledSound() disabled");
            return;
        }
        emitSoundNotification(ASSET_SOUND_MARKED_SCROBBLED_URI);
    }

    private void emitSoundNotification(Uri soundUri) {
        Notification notification = new NotificationCompat.Builder(context)
                .setSound(soundUri)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(getNextId(), notification);
    }

    private static synchronized int getNextId() {
        return sNextId++;
    }
}
