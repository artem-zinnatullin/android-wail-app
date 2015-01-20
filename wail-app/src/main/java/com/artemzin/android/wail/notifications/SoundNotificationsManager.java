package com.artemzin.android.wail.notifications;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.util.AsyncTaskExecutor;
import com.artemzin.android.wail.util.Loggi;
import com.artemzin.android.wail.util.ThreadUtil;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class SoundNotificationsManager {

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

        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Object, Object, Object>() {
            volatile MediaPlayer mediaPlayer;

            @Override
            protected Object doInBackground(Object... params) {
                final long startTime = SystemClock.elapsedRealtime();

                try {
                    mediaPlayer = MediaPlayer.create(context, R.raw.track_skipped);
                } catch (Exception e) {
                    Loggi.e("SoundNotificationsManager.playTrackMarkedAsScrobbledSound() exception: " + e);
                }

                ThreadUtil.sleepIfRequired(startTime, 250);

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                if (mediaPlayer == null) return;

                try {
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            tryToReleaseMediaPlayer(mediaPlayer);
                        }
                    });

                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            tryToReleaseMediaPlayer(mediaPlayer);
                            return false;
                        }
                    });

                    mediaPlayer.setVolume(0.07f, 0.07f);
                    mediaPlayer.start();
                } catch (Exception e) {
                    Loggi.e("SoundNotificationsManager.playTrackSkippedSound() can not play sound: " + e);
                }
            }
        });
    }

    public void playTrackMarkedAsScrobbledSound() {
        playTrackMarkedAsScrobbledSound(false);
    }

    public void playTrackMarkedAsScrobbledSound(boolean force) {
        if (!force && !WAILSettings.isSoundNotificationTrackMarkedAsScrobbledEnabled(context)) {
            Loggi.w("SoundNotificationsManager.playTrackMarkedAsScrobbledSound() disabled");
            return;
        }

        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Void>() {

            volatile MediaPlayer mediaPlayer;

            @Override
            protected Void doInBackground(Void... params) {
                final long startTime = SystemClock.elapsedRealtime();

                try {
                    mediaPlayer = MediaPlayer.create(context, R.raw.track_marked_as_scrobbled);
                } catch (Exception e) {
                    Loggi.e("SoundNotificationsManager.playTrackMarkedAsScrobbledSound() exception: " + e);
                }

                ThreadUtil.sleepIfRequired(startTime, 350);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (mediaPlayer == null) return;

                try {
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            tryToReleaseMediaPlayer(mediaPlayer);
                        }
                    });

                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            tryToReleaseMediaPlayer(mediaPlayer);
                            return false;
                        }
                    });

                    mediaPlayer.setVolume(0.18f, 0.18f);
                    mediaPlayer.start();
                } catch (Exception e) {
                    Loggi.e("SoundNotificationsManager.playTrackMarkedAsScrobbledSound() can not play sound: " + e);
                }
            }
        });
    }

    private static void tryToReleaseMediaPlayer(MediaPlayer mediaPlayer) {
        try {
            mediaPlayer.reset();
            mediaPlayer.release();
        } catch (Exception e) {
            Loggi.w("SoundNotificationsManager can not release media player");
        }
    }
}
