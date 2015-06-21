package com.artemzin.android.wail.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.artemzin.android.wail.storage.model.Track;
import com.artemzin.android.wail.util.Loggi;

/**
 * Created by Ilya Murzinov [murz42@gmail.com]
 */
public class LovedTracksDBHelper {
    private static volatile LovedTracksDBHelper instance;
    private Context context;

    private LovedTracksDBHelper(Context context) {
        this.context = context;
    }

    public static LovedTracksDBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (LovedTracksDBHelper.class) {
                if (instance == null) {
                    instance = new LovedTracksDBHelper(context);
                }
            }
        }
        return instance;
    }

    public synchronized long add(Track track) {
        ContentValues contentValues = asContentValues(track);

        final long rowId = AppDBManager.getInstance(context).getWritableDatabase()
                .insert(TableInfo.TABLE_NAME, null, contentValues);

        if (rowId == -1 ) {
            Loggi.e("LovedTracksDBHelper can not add track info, track: " + track);
        }

        return rowId;
    }

    public synchronized Cursor getAllDesc() {
        return AppDBManager.getInstance(context).getReadableDatabase()
                .query(TableInfo.TABLE_NAME, null, null, null, null, null, TableInfo.COLUMN_INTERNAL_ID + " DESC");
    }

    public synchronized int deleteAll() {
        final int count = AppDBManager.getInstance(context).getWritableDatabase().delete(TableInfo.TABLE_NAME, null, null);
        return count;
    }

    public synchronized int delete(Track track) {
        return AppDBManager.getInstance(context).getWritableDatabase()
                .delete(TableInfo.TABLE_NAME,
                        TableInfo.COLUMN_INTERNAL_ID + "=?",
                        new String[]{String.valueOf(track.getInternalDBId())});
    }

    public static Track parseFromCursor(Cursor cursor) {
        final Track track = new Track();

        track.setInternalDBId(cursor.getLong(TableInfo.NUM_COLUMN_INTERNAL_ID));
        track.setTrack(cursor.getString(TableInfo.NUM_COLUMN_TRACK));
        track.setArtist(cursor.getString(TableInfo.NUM_COLUMN_ARTIST));

        return track;
    }

    private static ContentValues asContentValues(Track track) {
        final ContentValues contentValues = new ContentValues();

        contentValues.put(TableInfo.COLUMN_TRACK, track.getTrack());
        contentValues.put(TableInfo.COLUMN_ARTIST, track.getArtist());

        return contentValues;
    }

    public static class TableInfo {
        private static final String TABLE_NAME = "loved_tracks";

        // region columns

        private static final String COLUMN_INTERNAL_ID         = "internal_id";
        private static final String COLUMN_TRACK               = "track";
        private static final String COLUMN_ARTIST              = "artist";

        private static final int NUM_COLUMN_INTERNAL_ID         = 0;
        private static final int NUM_COLUMN_TRACK               = 1;
        private static final int NUM_COLUMN_ARTIST              = 2;

        // endregion

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRACK + " TEXT, " +
                COLUMN_ARTIST + " TEXT);";
    }
}
