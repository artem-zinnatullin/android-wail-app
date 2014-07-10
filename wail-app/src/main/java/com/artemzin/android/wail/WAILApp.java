package com.artemzin.android.wail;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.db.PlayersDBHelper;
import com.artemzin.android.wail.ui.activity.MainActivity;
import com.artemzin.android.wail.util.AsyncTaskExecutor;
import com.artemzin.android.wail.util.Loggi;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class WAILApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Loggi.w("WAILApp onCreate()");
        updateSupportedPlayersDB();
    }

    private void updateSupportedPlayersDB() {
        AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final long startTime = SystemClock.elapsedRealtime();

                    PlayersDBHelper.getInstance(getApplicationContext()).updateSupportedPlayers();

                    final long updateDurationMillis = SystemClock.elapsedRealtime() - startTime;

                    Loggi.i("Supported players db update duration: " + updateDurationMillis + "ms");
                    EasyTracker.getInstance(WAILApp.this).send(MapBuilder.createTiming(
                            "WAILApp",
                            updateDurationMillis,
                            "updateSupportedPlayersDB",
                            null
                    ).build());
                } catch (Exception e) {
                    Loggi.e("Can not update players db!!!");
                    EasyTracker.getInstance(WAILApp.this).send(MapBuilder.createException("Can not update supported players db!", true)
                            .build()
                    );
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Context context = getApplicationContext();
                if (context == null) return;
                startService(new Intent(context, WAILService.class));
            }
        });
    }
}
