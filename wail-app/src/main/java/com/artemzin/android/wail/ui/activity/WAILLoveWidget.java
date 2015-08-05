package com.artemzin.android.wail.ui.activity;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.artemzin.android.wail.R;
import com.artemzin.android.wail.service.WAILService;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.storage.db.LovedTracksDBHelper;
import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.util.AsyncTaskExecutor;

public class WAILLoveWidget extends AppWidgetProvider {

    private static final String LOVE_TRACK_BUTTON_CLICKED = "LOVE_TRACK_BUTTON_CLICKED";

    // Show info box only if widget has a min width of 3 cells, which needs to be calculated in terms of dp.
    // Formula provided in Widget Design Guideline is (70 x n - 30) where n is number of cells.
    // see: http://developer.android.com/guide/practices/ui_guidelines/widget_design.html
    private static final int MIN_WIDTH_TO_SHOW_INFO_BOX = 179;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
            onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, options);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.waillove_widget);

        int newMinWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        if (newMinWidth > MIN_WIDTH_TO_SHOW_INFO_BOX) {
            views.setViewVisibility(R.id.widget_infobox_layout, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.widget_infobox_layout, View.GONE);
        }

        Intent loveTrackIntent = new Intent(context, getClass());
        loveTrackIntent.setAction(LOVE_TRACK_BUTTON_CLICKED);
        PendingIntent pendingLoveTrackIntent = PendingIntent.getBroadcast(context, 0, loveTrackIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_love_current_track_button, pendingLoveTrackIntent);

        Intent appMainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingAppMainIntent = PendingIntent.getActivity(context, 0, appMainIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_infobox_layout, pendingAppMainIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        super.onReceive(context, intent);

        if (LOVE_TRACK_BUTTON_CLICKED.equals(intent.getAction())) {
            final Track track = WAILSettings.getNowScrobblingTrack(context);
            if (track == null) {
                Toast.makeText(context, context.getString(R.string.widget_nothing_to_love), Toast.LENGTH_SHORT).show();
                return;
            }

            AsyncTaskExecutor.executeConcurrently(new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... objects) {
                    LovedTracksDBHelper.getInstance(context).add(track);

                    Intent handleLovedTracksIntent = new Intent(context, WAILService.class);
                    handleLovedTracksIntent.setAction(WAILService.INTENT_ACTION_HANDLE_LOVED_TRACKS);
                    context.startService(handleLovedTracksIntent);

                    return null;
                }

                @Override
                protected void onPostExecute(Void o) {
                    Toast.makeText(context, context.getString(R.string.main_track_loved), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}


