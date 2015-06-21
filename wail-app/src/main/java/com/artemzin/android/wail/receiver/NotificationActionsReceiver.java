package com.artemzin.android.wail.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.notifications.StatusBarNotificationsManager;
import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.db.LovedTracksDBHelper;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.util.AsyncTaskExecutor;

/**
 * Created by Ilya Murzinov [murz42@gmail.com]
 */
public class NotificationActionsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        final Track track = WAILSettings.getNowScrobblingTrack(context);
        if (track != null) {
            AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... objects) {
                    LovedTracksDBHelper.getInstance(context).add(track);

                    Intent intent = new Intent(context, WAILService.class);
                    intent.setAction(WAILService.INTENT_ACTION_HANDLE_LOVED_TRACKS);
                    context.startService(intent);

                    return null;
                }

                @Override
                protected void onPostExecute(Void o) {
                    StatusBarNotificationsManager.getInstance(context)
                            .showTrackLovedStatusBarNotification(track);
                }
            });
        }
    }
}
