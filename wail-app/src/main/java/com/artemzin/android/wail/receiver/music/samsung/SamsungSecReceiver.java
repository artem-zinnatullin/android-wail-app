package com.artemzin.android.wail.receiver.music.samsung;

import android.content.Context;
import android.content.Intent;

import com.artemzin.android.wail.receiver.music.CommonMusicAppReceiver;

public class SamsungSecReceiver extends CommonMusicAppReceiver {

    @Override
    protected Intent handleIntent(Context context, Intent originalIntent) {
        return super.handleIntent(context, originalIntent)
                .putExtra(EXTRA_PLAYER_PACKAGE_NAME, "com.samsung.sec");
    }
}
