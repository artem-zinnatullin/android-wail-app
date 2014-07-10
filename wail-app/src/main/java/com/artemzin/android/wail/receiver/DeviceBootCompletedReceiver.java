package com.artemzin.android.wail.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.util.Loggi;

/**
 * Listening for device boot completed broadcast and starts WAILService if required
 */
public class DeviceBootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Loggi.i("BOOT COMPLETED received");

        if (WAILSettings.isStartOnBoot(context)) {
            Loggi.w("Starting WAILService after boot");
            context.startService(new Intent(context, WAILService.class));
        } else {
            Loggi.w("Skipping WAILService start after boot");
        }
    }
}
