package com.artemzin.android.wail.receiver.music;

import android.content.Context;
import android.content.Intent;

public class AndroidMusicReceiver extends CommonMusicAppReceiver {

    @Override
    protected Intent handleIntent(Context context, Intent originalIntent) {
        final Intent handleTrackIntent = super.handleIntent(context, originalIntent);

        // fu*** power amp does not send its own intent!
        // but it puts this extra, so if intent has this extra - it is power amp
        if (handleTrackIntent != null && originalIntent.hasExtra("com.maxmpz.audioplayer.source")) {
            handleTrackIntent.putExtra(EXTRA_PLAYER_PACKAGE_NAME, "com.maxmpz.audioplayer");
        }

        return handleTrackIntent;
    }
}
