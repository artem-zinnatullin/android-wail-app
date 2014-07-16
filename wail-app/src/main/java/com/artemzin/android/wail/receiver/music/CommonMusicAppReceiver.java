package com.artemzin.android.wail.receiver.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.artemzin.android.bytes.common.StringUtil;
import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.util.AsyncTaskExecutor;
import com.artemzin.android.wail.util.IntentUtil;
import com.artemzin.android.wail.util.Loggi;
import com.artemzin.android.wail.util.StackTraceUtil;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public abstract class CommonMusicAppReceiver extends BroadcastReceiver {

    public static final String EXTRA_ACTION              = "EXTRA_ACTION";
    public static final String EXTRA_PLAYER_PACKAGE_NAME = "EXTRA_PLAYER_PACKAGE_NAME";
    public static final String EXTRA_ID                  = "EXTRA_ID";
    public static final String EXTRA_PLAYING             = "EXTRA_PLAYING";
    public static final String EXTRA_ALBUM_ID            = "EXTRA_ALBUM_ID";
    public static final String EXTRA_TRACK               = "EXTRA_TRACK";
    public static final String EXTRA_ARTIST              = "EXTRA_ARTIST";
    public static final String EXTRA_ALBUM               = "EXTRA_ALBUM";
    public static final String EXTRA_DURATION            = "EXTRA_DURATION";

    public static final String EXTRA_TIMESTAMP           = "EXTRA_TIMESTAMP";

    @Override
    public final void onReceive(Context context, Intent intent) {
        asyncProcessTheIntent(context, intent);
    }

    protected final Intent newIntentForWAILService(Context context) {
        return new Intent(context, WAILService.class);
    }

    private void asyncProcessTheIntent(final Context context, final Intent intent) {
        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                try {
                    try {
                        Loggi.d("CommonMusicAppReceiver.onReceive() intent: " + IntentUtil.getIntentAsString(intent));
                    } catch (Exception e) {
                        Loggi.e("CommonMusicAppReceiver.onReceive() can not display intent info");
                    }

                    if (intent == null) {
                        Loggi.e("CommonMusicAppReceiver.onReceive() intent is null");
                        return null;
                    }

                    if (StringUtil.isNullOrEmpty(intent.getAction())
                            || intent.getAction().indexOf('.') == -1) {
                        Loggi.e("CommonMusicAppReceiver.onReceive() intent action is corrupted: " + intent.getAction());
                        return null;
                    }

                    if (intent.getExtras() == null || intent.getExtras().size() == 0) {
                        Loggi.e("CommonMusicAppReceiver.onReceive() intent extras are null or empty, skipping intent");
                        return null;
                    }

                    if (isInitialStickyBroadcast()) {
                        Loggi.w("CommonMusicAppReceiver.onReceive() received cached sticky broadcast, WAIL won't process it");
                        return null;
                    }

                    final Intent intentForWAILService = handleIntent(context, intent);

                    if (intentForWAILService != null) {
                        intentForWAILService.setAction(WAILService.INTENT_ACTION_HANDLE_TRACK);
                        intentForWAILService.putExtra(EXTRA_ACTION, intent.getAction());
                        intentForWAILService.putExtra(EXTRA_TIMESTAMP, System.currentTimeMillis());

                        return intentForWAILService;
                    } else {
                        Loggi.w("CommonMusicAppReceiver.onReceive() did not send intent for service, handleIntent() returns null, skipping intent");
                        return null;
                    }
                } catch (Exception e) {
                    final String log = "CommonMusicAppReceiver.onReceive() exception while handleIntent(): " + StackTraceUtil.getStackTrace(e);
                    Loggi.e(log);
                    EasyTracker.getInstance(context).send(MapBuilder.createException(log, false).build());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Intent intentForWAILService) {
                if (intentForWAILService != null) {
                    context.startService(intentForWAILService);
                }
            }
        });
    }

    protected Intent handleIntent(Context context, Intent originalIntent) {
        final Intent handleTrackIntent = newIntentForWAILService(context);

        handleTrackIntent.putExtra(EXTRA_PLAYER_PACKAGE_NAME, originalIntent.getAction().substring(0, originalIntent.getAction().lastIndexOf('.')));

        handleTrackIntent.putExtra(EXTRA_ID, IntentUtil.getLongOrIntExtra(originalIntent, -1, "id", "trackid", "trackId"));

        final Boolean isPlaying = IntentUtil.getBoolOrNumberAsBoolExtra(originalIntent, null, "playing", "playstate", "isPlaying", "isplaying", "is_playing");

        if (isPlaying == null) {
            Loggi.w("CommonMusicAppReceiver track info does not contains playing state, ignoring");
            return null;
        } else {
            handleTrackIntent.putExtra(EXTRA_PLAYING, isPlaying);
        }

        handleTrackIntent.putExtra(EXTRA_ALBUM_ID, IntentUtil.getLongOrIntExtra(originalIntent, -1, "albumid", "albumId"));
        handleTrackIntent.putExtra(EXTRA_TRACK,    originalIntent.getStringExtra("track"));
        handleTrackIntent.putExtra(EXTRA_ARTIST,   originalIntent.getStringExtra("artist"));
        handleTrackIntent.putExtra(EXTRA_ALBUM,    originalIntent.getStringExtra("album"));

        long duration = IntentUtil.getLongOrIntExtra(originalIntent, -1, "duration");

        if (duration != -1) {
            if (duration < 30000) { // it is in seconds, we should convert it to millis
                duration *= 1000;
            }
        }

        handleTrackIntent.putExtra(EXTRA_DURATION, duration);

        return handleTrackIntent;
    }

    public static Intent addTrackDataToTheIntent(Track track, Intent intent) {
        intent.putExtra(EXTRA_PLAYER_PACKAGE_NAME, track.getPlayerPackageName());
        intent.putExtra(EXTRA_TRACK, track.getTrack());
        intent.putExtra(EXTRA_ARTIST, track.getArtist());
        intent.putExtra(EXTRA_ALBUM, track.getAlbum());
        intent.putExtra(EXTRA_DURATION, track.getDuration());
        intent.putExtra(EXTRA_TIMESTAMP, track.getTimestamp());

        return intent;
    }

    public static Track parseFromIntentExtras(final Intent intent) {
        final Track track = new Track();

        track.setPlayerPackageName(intent.getStringExtra(EXTRA_PLAYER_PACKAGE_NAME));
        track.setTrack(intent.getStringExtra(EXTRA_TRACK));
        track.setArtist(intent.getStringExtra(EXTRA_ARTIST));
        track.setAlbum(intent.getStringExtra(EXTRA_ALBUM));
        track.setDuration(intent.getLongExtra(EXTRA_DURATION, -1L));
        track.setTimestamp(intent.getLongExtra(EXTRA_TIMESTAMP, -1L));

        return track;
    }
}
