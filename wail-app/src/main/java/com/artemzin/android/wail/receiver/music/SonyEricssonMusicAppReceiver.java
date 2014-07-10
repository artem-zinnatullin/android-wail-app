package com.artemzin.android.wail.receiver.music;

import android.content.Context;
import android.content.Intent;

import com.artemzin.android.wail.util.Loggi;

public class SonyEricssonMusicAppReceiver extends CommonMusicAppReceiver {

    @Override
    protected Intent handleIntent(Context context, Intent originalIntent) {
        final Intent handleTrackIntent = newIntentForWAILService(context);

        handleTrackIntent.putExtra(EXTRA_PLAYER_PACKAGE_NAME, "com.sonyericsson.music");
        final String actionSuffix;

        try {
            actionSuffix = originalIntent.getAction().substring(originalIntent.getAction().lastIndexOf('.') + 1);
        } catch (Exception e) {
            Loggi.e("Can not parse action suffix for intent action: " + originalIntent.getAction());
            return null;
        }

        if (actionSuffix.equals("ACTION_TRACK_STARTED")) {
            handleTrackIntent.putExtra(EXTRA_PLAYING, true);
        } else if (actionSuffix.equals("TRACK_COMPLETED") || actionSuffix.equals("ACTION_PAUSED")) {
            handleTrackIntent.putExtra(EXTRA_PLAYING, false);
        } else {
            Loggi.w("SonyEricssonMusicAppReceiver track info does not contains playing state, ignoring");
            return null;
        }

        handleTrackIntent.putExtra(EXTRA_ID, (long) originalIntent.getIntExtra("TRACK_ID", -1));
        handleTrackIntent.putExtra(EXTRA_ALBUM_ID, (long) originalIntent.getIntExtra("ALBUM_ID", -1));
        handleTrackIntent.putExtra(EXTRA_TRACK, originalIntent.getStringExtra("TRACK_NAME"));
        handleTrackIntent.putExtra(EXTRA_ARTIST, originalIntent.getStringExtra("ARTIST_NAME"));
        handleTrackIntent.putExtra(EXTRA_ALBUM, originalIntent.getStringExtra("ALBUM_NAME"));
        handleTrackIntent.putExtra(EXTRA_DURATION, (long) originalIntent.getIntExtra("TRACK_DURATION", -1));

        return handleTrackIntent;
    }
}
