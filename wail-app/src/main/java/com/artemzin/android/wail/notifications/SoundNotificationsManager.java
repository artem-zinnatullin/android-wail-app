package com.artemzin.android.wail.notifications;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.util.Loggi;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class SoundNotificationsManager {

    private static final Uri RES_URI = Uri.parse("android.resource://com.artemzin.android.wail");
    private static final Uri ASSET_SOUND_MARKED_SCROBBLED_URI =
            Uri.withAppendedPath(RES_URI, String.valueOf(R.raw.track_marked_as_scrobbled));
    private static final Uri ASSET_SOUND_SKIPPED_URI =
            Uri.withAppendedPath(RES_URI, String.valueOf(R.raw.track_skipped));

    private Context context;
    private volatile long lastTrackSkippedPlayTime;

    private static volatile SoundNotificationsManager instance;

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
        playTrackSkippedSound(false, 0 /* delay */);
    }

    public void playTrackSkippedSound(boolean force, long delay) {
        if (!force && !WAILSettings.isSoundNotificationTrackSkippedEnabled(context)) {
            Loggi.w("SoundNotificationsManager.playTrackSkippedSound() disabled");
            return;
        }

        if (SystemClock.elapsedRealtime() - lastTrackSkippedPlayTime < 300) {
            return;
        }

        lastTrackSkippedPlayTime = SystemClock.elapsedRealtime();

        emitSoundNotification(ASSET_SOUND_SKIPPED_URI, delay);
    }

    public void playTrackMarkedAsScrobbledSound() {
        playTrackMarkedAsScrobbledSound(false, 0 /* delay */);
    }

    public void playTrackMarkedAsScrobbledSound(boolean force, long delay) {
        if (!force && !WAILSettings.isSoundNotificationTrackMarkedAsScrobbledEnabled(context)) {
            Loggi.w("SoundNotificationsManager.playTrackMarkedAsScrobbledSound() disabled");
            return;
        }
        emitSoundNotification(ASSET_SOUND_MARKED_SCROBBLED_URI, delay);
    }

    /**
     * Use the {@link RingtoneManager} to play the given sound, after a given delay.
     * @param soundUri The Uri of the sound to play
     * @param delay Delay to wait for, before playing the sound.
     */
    private void emitSoundNotification(final Uri soundUri, long delay) {
        if (delay <= 0) {
            RingtoneManager.getRingtone(context, soundUri).play();
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    RingtoneManager.getRingtone(context, soundUri).play();
                }
            }, delay);
        }
    }
}
