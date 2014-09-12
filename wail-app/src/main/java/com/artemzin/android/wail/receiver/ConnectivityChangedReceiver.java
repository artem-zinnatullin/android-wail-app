package com.artemzin.android.wail.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.util.NetworkUtil;

public class ConnectivityChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtil.isAvailable(context)) {
            if (!WAILSettings.isEnableScrobblingOverMobileNetwork(context)
                    && NetworkUtil.isMobileNetwork(context)) {
                // if mobile network scrobbling disabled
                // and current network is mobile
                // we should prevent starting of WAILService
                return;
            }

            context.startService(
                    new Intent(context, WAILService.class)
                            .setAction(WAILService.INTENT_ACTION_SCROBBLE_PENDING_TRACKS)
            );
        }
    }
}
