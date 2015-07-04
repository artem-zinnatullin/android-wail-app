package com.artemzin.android.wail.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;

import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.util.Loggi;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.List;

public class TracksDBHelper {

    public static final String INTENT_TRACKS_CHANGED = "INTENT_TRACKS_CHANGED";

    private static volatile TracksDBHelper instance;
    private Context context;

    private TracksDBHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public static TracksDBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (TracksDBHelper.class) {
                if (instance == null) {
                    instance = new TracksDBHelper(context);
                }
            }
        }

        return instance;
    }

    public synchronized long add(Track track) {
        ContentValues contentValues = asContentValues(track);

        final long rowId = AppDBManager.getInstance(context).getWritableDatabase().insert(TableInfo.TABLE_NAME, null, contentValues);

        if (rowId == -1 ) {
            Loggi.e("TracksDBHelper can not add track info, track: " + track);
        }

        sendBroadcastTracksChanged();

        return rowId;
    }

    public synchronized boolean update(Track track) {
        ContentValues contentValues = asContentValues(track);

        final boolean result = AppDBManager.getInstance(context).getWritableDatabase().update(
                TableInfo.TABLE_NAME,
                contentValues,
                TableInfo.COLUMN_INTERNAL_ID + " = ?",
                new String[] { String.valueOf(track.getInternalDBId()) }
        ) >= 1;

        if (!result) {
            Loggi.e("TracksDBHelper can not update track info, track: " + track);
        }

        sendBroadcastTracksChanged();

        return result;
    }

    public synchronized int delete(Track track) {
        return AppDBManager.getInstance(context).getWritableDatabase()
                .delete(TableInfo.TABLE_NAME,
                        TableInfo.COLUMN_INTERNAL_ID + "=?",
                        new String[]{String.valueOf(track.getInternalDBId())});
    }

    public synchronized void updateAll(List<Track> tracks) {
        AppDBManager.getInstance(context).getWritableDatabase().beginTransaction();

        try {
            for (Track track : tracks) {
                final ContentValues contentValues = asContentValues(track);
                AppDBManager.getInstance(context).getWritableDatabase().update(
                        TableInfo.TABLE_NAME,
                        contentValues,
                        TableInfo.COLUMN_INTERNAL_ID + " = ?",
                        new String[] { String.valueOf(track.getInternalDBId()) }
                );
            }

            AppDBManager.getInstance(context).getWritableDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            Loggi.e("TracksDBHelper.updateAll() can not perform action: " + e);
        } finally {
            AppDBManager.getInstance(context).getWritableDatabase().endTransaction();
        }

        sendBroadcastTracksChanged();
    }

    public synchronized Cursor getAllDesc() {
        return AppDBManager.getInstance(context).getReadableDatabase()
                .query(TableInfo.TABLE_NAME, null, null, null, null, null, TableInfo.COLUMN_INTERNAL_ID + " DESC");
    }

    public synchronized Track getLastAddedTrack() {
        Cursor cursor = AppDBManager.getInstance(context).getReadableDatabase()
                .rawQuery("SELECT * FROM " + TableInfo.TABLE_NAME + " ORDER BY " + TableInfo.COLUMN_INTERNAL_ID + " DESC LIMIT 1",
                        null
                );

        Track lastAddedTrack = null;

        if (cursor.moveToFirst()) {
            lastAddedTrack = parseFromCursor(cursor);
        }

        cursor.close();

        return lastAddedTrack;
    }

    public synchronized int deleteAll() {
        final int count = AppDBManager.getInstance(context).getWritableDatabase().delete(TableInfo.TABLE_NAME, null, null);
        sendBroadcastTracksChanged();
        return count;
    }

    public synchronized int removeOldOrInconsistentTracks(int maxLocalTracksCount) {
        AppDBManager.getInstance(context).getWritableDatabase().beginTransaction();

        int removedTracksCount = 0;

        try {
            Cursor cursor = getAllDesc();

            if (cursor.moveToFirst()) {
                do {
                    Track track = parseFromCursor(cursor);

                    if (track.getState() == Track.STATE_SCROBBLING
                            && System.currentTimeMillis() - track.getStateTimestamp() > 86400000) {
                        removedTracksCount += delete(track);
                    }
                } while (cursor.moveToNext());
            }

            final int count = cursor.getCount();

            if (count > maxLocalTracksCount) {
                for (int i = maxLocalTracksCount; i < count; i++) {
                    cursor.moveToPosition(i);

                    final Track track = parseFromCursor(cursor);

                    if (System.currentTimeMillis() - track.getStateTimestamp() > 86400000
                            && (track.getState() == Track.STATE_SCROBBLE_SUCCESS
                            || track.getState() == Track.STATE_SCROBBLE_ERROR
                            || track.getState() == Track.STATE_SCROBBLING)) {
                        removedTracksCount += delete(track);
                    }
                }
            }

            AppDBManager.getInstance(context).getWritableDatabase().setTransactionSuccessful();

            cursor.close();
        } catch (Exception e) {
            String errorMessage = "TracksDBHelper.removeOldOrInconsistentTracks() exception: " + e;
            Loggi.e(errorMessage);
            EasyTracker.getInstance(context).send(MapBuilder.createException(errorMessage, false).build());
        } finally {
            AppDBManager.getInstance(context).getWritableDatabase().endTransaction();
        }

        if (removedTracksCount > 0) {
            try {
                AppDBManager.getInstance(context).getWritableDatabase().execSQL("VACUUM");
            } catch (Exception e) {
                Loggi.e("Can not perform VACUUM on database: " + e.getMessage());
            }
        }

        sendBroadcastTracksChanged();

        return removedTracksCount;
    }

    private void sendBroadcastTracksChanged() {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(INTENT_TRACKS_CHANGED));
    }

    public static ContentValues asContentValues(Track track) {
        final ContentValues contentValues = new ContentValues();

        contentValues.put(TableInfo.COLUMN_PLAYER_PACKAGE_NAME, track.getPlayerPackageName());
        contentValues.put(TableInfo.COLUMN_TRACK, track.getTrack());
        contentValues.put(TableInfo.COLUMN_ARTIST, track.getArtist());
        contentValues.put(TableInfo.COLUMN_ALBUM, track.getAlbum());
        contentValues.put(TableInfo.COLUMN_DURATION, track.getDuration());
        contentValues.put(TableInfo.COLUMN_TIMESTAMP, track.getTimestamp());
        contentValues.put(TableInfo.COLUMN_STATE, track.getState());
        contentValues.put(TableInfo.COLUMN_STATE_TIMESTAMP, track.getStateTimestamp());

        return contentValues;
    }

    public static Track parseFromCursor(Cursor cursor) {
        final Track track = new Track();

        track.setInternalDBId(cursor.getLong(TableInfo.NUM_COLUMN_INTERNAL_ID));
        track.setPlayerPackageName(cursor.getString(TableInfo.NUM_COLUMN_PLAYER_PACKAGE_NAME));
        track.setTrack(cursor.getString(TableInfo.NUM_COLUMN_TRACK));
        track.setArtist(cursor.getString(TableInfo.NUM_COLUMN_ARTIST));
        track.setAlbum(cursor.getString(TableInfo.NUM_COLUMN_ALBUM));
        track.setDuration(cursor.getLong(TableInfo.NUM_COLUMN_DURATION));
        track.setTimestamp(cursor.getLong(TableInfo.NUM_COLUMN_TIMESTAMP));
        track.setState(cursor.getInt(TableInfo.NUM_COLUMN_STATE));
        track.setStateTimestamp(cursor.getLong(TableInfo.NUM_COLUMN_STATE_TIMESTAMP));

        return track;
    }

    public static class TableInfo {

        private static final String TABLE_NAME = "tracks";

        // region columns

        private static final String COLUMN_INTERNAL_ID         = "internal_id";
        private static final String COLUMN_PLAYER_PACKAGE_NAME = "player_package_name";
        private static final String COLUMN_TRACK               = "track";
        private static final String COLUMN_ARTIST              = "artist";
        private static final String COLUMN_ALBUM               = "album";
        private static final String COLUMN_DURATION            = "duration";
        private static final String COLUMN_TIMESTAMP           = "timestamp";
        private static final String COLUMN_STATE               = "state";
        private static final String COLUMN_STATE_TIMESTAMP     = "state_timestamp";

        private static final int NUM_COLUMN_INTERNAL_ID         = 0;
        private static final int NUM_COLUMN_PLAYER_PACKAGE_NAME = 1;
        private static final int NUM_COLUMN_TRACK               = 2;
        private static final int NUM_COLUMN_ARTIST              = 3;
        private static final int NUM_COLUMN_ALBUM               = 4;
        private static final int NUM_COLUMN_DURATION            = 5;
        private static final int NUM_COLUMN_TIMESTAMP           = 6;
        private static final int NUM_COLUMN_STATE               = 7;
        private static final int NUM_COLUMN_STATE_TIMESTAMP     = 8;

        // endregion

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAYER_PACKAGE_NAME + " TEXT, " +
                COLUMN_TRACK + " TEXT, " +
                COLUMN_ARTIST + " TEXT, " +
                COLUMN_ALBUM + " TEXT, " +
                COLUMN_DURATION + " INTEGER, " +
                COLUMN_TIMESTAMP + " INTEGER, " +
                COLUMN_STATE + " INTEGER, " +
                COLUMN_STATE_TIMESTAMP + " INTEGER);";
    }
}
