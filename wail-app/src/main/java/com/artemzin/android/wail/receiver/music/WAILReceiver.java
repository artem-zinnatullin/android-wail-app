package com.artemzin.android.wail.receiver.music;

import android.content.Context;
import android.content.Intent;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class WAILReceiver extends CommonMusicAppReceiver {

    private static final String EXTRA_WAIL_PLAYER_PACKAGE_NAME = "player_package_name";

    @Override
    protected Intent handleIntent(Context context, Intent originalIntent) {
        final Intent handleTrackIntent = super.handleIntent(context, originalIntent);

        if (originalIntent.hasExtra(EXTRA_WAIL_PLAYER_PACKAGE_NAME)) {
            try {
                handleTrackIntent.putExtra(EXTRA_PLAYER_PACKAGE_NAME, originalIntent.getStringExtra(EXTRA_WAIL_PLAYER_PACKAGE_NAME));
            } catch (Exception e) {
                handleTrackIntent.putExtra(EXTRA_PLAYER_PACKAGE_NAME, "unknown");
            }
        } else {
            handleTrackIntent.putExtra(EXTRA_PLAYER_PACKAGE_NAME, "unknown");
        }

        return handleTrackIntent;
    }
}
