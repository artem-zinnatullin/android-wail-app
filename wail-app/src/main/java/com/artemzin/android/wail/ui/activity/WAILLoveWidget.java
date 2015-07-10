package com.artemzin.android.wail.ui.activity;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
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

public class WAILLoveWidget extends AppWidgetProvider {

    private static final String LOVE_CLICKED = "LOVE_CLICKED";

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
        if (newMinWidth > 179) {
            views.setViewVisibility(R.id.widget_app_shortcut, View.GONE);
            views.setViewVisibility(R.id.widget_infobox_layout, View.VISIBLE);
        } else if (newMinWidth > 109) {
            views.setViewVisibility(R.id.widget_app_shortcut, View.VISIBLE);
            views.setViewVisibility(R.id.widget_infobox_layout, View.GONE);
        } else if (newMinWidth > 39) {
            views.setViewVisibility(R.id.widget_app_shortcut, View.GONE);
            views.setViewVisibility(R.id.widget_infobox_layout, View.GONE);
        }

        Intent loveTrackIntent = new Intent(context, getClass());
        loveTrackIntent.setAction(LOVE_CLICKED);
        PendingIntent pendingLoveTrackIntent = PendingIntent.getBroadcast(context, 0, loveTrackIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_love_current_track_button, pendingLoveTrackIntent);

        Intent appMainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingAppMainIntent = PendingIntent.getActivity(context, 0, appMainIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_app_shortcut, pendingAppMainIntent);

        Track track = WAILSettings.getNowScrobblingTrack(context);
        if (track != null) {
            views.setTextViewText(R.id.widget_infobox_track_text, track.getTrack());
            views.setTextViewText(R.id.widget_infobox_artist_text, track.getArtist());
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (LOVE_CLICKED.equals(intent.getAction())) {
            Track track = WAILSettings.getNowScrobblingTrack(context);
            if (track == null) {
                Toast.makeText(context, "nufin to luv T__T", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.main_track_loved), Toast.LENGTH_SHORT).show();
                LovedTracksDBHelper.getInstance(context).add(track);

                Intent handleLovedTracksIntent = new Intent(context, WAILService.class);
                handleLovedTracksIntent.setAction(WAILService.INTENT_ACTION_HANDLE_LOVED_TRACKS);
                context.startService(handleLovedTracksIntent);
            }
        }
    }

}


