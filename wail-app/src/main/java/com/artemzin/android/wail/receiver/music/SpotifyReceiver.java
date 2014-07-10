package com.artemzin.android.wail.receiver.music;

import android.content.Context;
import android.content.Intent;

import com.artemzin.android.wail.util.IntentUtil;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class SpotifyReceiver extends CommonMusicAppReceiver {
    @Override
    protected Intent handleIntent(Context context, Intent originalIntent) {
        //Intent intent = super.handleIntent(context, originalIntent);
        //intent.putExtra(EXTRA_DURATION, IntentUtil.getLongOrIntExtra(originalIntent, -1, "length"));
        EasyTracker.getInstance(context).send(MapBuilder.createEvent("SpotifyReceiver", "handleIntent", IntentUtil.getIntentAsString(originalIntent), 0L).build());
        return null;
    }
}
