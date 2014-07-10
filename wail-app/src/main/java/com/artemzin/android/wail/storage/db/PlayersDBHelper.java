package com.artemzin.android.wail.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.artemzin.android.wail.util.Loggi;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.ArrayList;
import java.util.List;

public class PlayersDBHelper {

    public static class Player {

        private String packageName;
        private boolean isEnabled;
        private String displayName;
        private int scrobbledTracksCount;
        private String link;

        public Player() {

        }

        public Player(String packageName, String displayName, String link) {
            this.packageName = packageName;
            this.displayName = displayName;
            this.link        = link;
            this.isEnabled   = true;
        }

        public String getPackageName() {
            return packageName;
        }

        public Player setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public boolean isEnabled() {
            return isEnabled;
        }

        public Player setEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Player setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public int getScrobbledTracksCount() {
            return scrobbledTracksCount;
        }

        public Player setScrobbledTracksCount(int scrobbledTracksCount) {
            this.scrobbledTracksCount = scrobbledTracksCount;
            return this;
        }

        public String getLink() {
            return link;
        }

        public Player setLink(String link) {
            this.link = link;
            return this;
        }
    }

    private Context context;
    private static volatile PlayersDBHelper instance;

    private PlayersDBHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public static PlayersDBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (PlayersDBHelper.class) {
                if (instance == null) {
                    instance = new PlayersDBHelper(context);
                }
            }
        }

        return instance;
    }

    public synchronized boolean add(Player player) {
        final ContentValues contentValues = new ContentValues();

        contentValues.put(TableInfo.COLUMN_PACKAGE_NAME, player.getPackageName());
        contentValues.put(TableInfo.COLUMN_IS_ENABLED, player.isEnabled());
        contentValues.put(TableInfo.COLUMN_DISPLAY_NAME, player.getDisplayName());
        contentValues.put(TableInfo.COLUMN_SCROBBLED_TRACKS_COUNT, player.getScrobbledTracksCount());
        contentValues.put(TableInfo.COLUMN_LINK, player.getLink());

        return AppDBManager.getInstance(context).getWritableDatabase().insert(TableInfo.TABLE_NAME, null, contentValues) != -1;
    }

    public synchronized Player findPlayerByPackageName(String packageName) {
        final Cursor cursor = AppDBManager.getInstance(context).getReadableDatabase().query(
                TableInfo.TABLE_NAME,
                null,
                TableInfo.COLUMN_PACKAGE_NAME + " = ?",
                new String[] { packageName },
                null,
                null,
                TableInfo.COLUMN_ID
        );

        Player player = null;

        if (cursor.moveToFirst()) {
            player = parseFromCursor(cursor);
        }

        cursor.close();
        return player;
    }

    public synchronized List<Player> getAll() {

        final Cursor cursor = AppDBManager.getInstance(context).getReadableDatabase()
                .query(TableInfo.TABLE_NAME, null, null, null, null, null, TableInfo.COLUMN_ID);

        final List<Player> players = new ArrayList<Player>(cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                players.add(parseFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return players;
    }

    public synchronized int removeAll() {
        return AppDBManager.getInstance(context).getWritableDatabase().delete(TableInfo.TABLE_NAME, null, null);
    }

    private static Player parseFromCursor(Cursor cursor) {
        final Player player = new Player();

        player.setPackageName(cursor.getString(TableInfo.NUM_COLUMN_PACKAGE_NAME));
        player.setEnabled(cursor.getInt(TableInfo.NUM_COLUMN_IS_ENABLED) == TableInfo.DEFAULT_COLUMN_IS_ENABLED);
        player.setDisplayName(cursor.getString(TableInfo.NUM_COLUMN_DISPLAY_NAME));
        player.setScrobbledTracksCount(cursor.getInt(TableInfo.NUM_COLUMN_SCROBBLED_TRACKS_COUNT));
        player.setLink(cursor.getString(TableInfo.NUM_COLUMN_LINK));

        return player;
    }

    public synchronized void updateSupportedPlayers() {
        final Player[] defaultSupportedPlayers = {
                new Player("com.maxmpz.audioplayer", "Poweramp", "https://play.google.com/store/apps/details?id=com.maxmpz.audioplayer"),
                new Player("com.jrtstudio.music", "Android music player (not standard!)", "https://play.google.com/store/apps/details?id=com.jrtstudio.music"),
                new Player("com.htc.music", "HTC Music player", null),
                new Player("com.miui.player", "MIU player", "http://en.miui.com/"),
                new Player("com.sonyericsson.music", "Sony music player", null),
                new Player("com.rdio.android", "Rdio", "https://play.google.com/store/apps/details?id=com.rdio.android.ui"),
                new Player("com.samsung.sec.android.MusicPlayer", "Samsung music player", null),
                new Player("com.sec.android.app.music", "Samsung music player (another one)", null),
                new Player("com.nullsoft.winamp", "Winamp", "https://play.google.com/store/apps/details?id=com.nullsoft.winamp"),
                new Player("com.amazon.mp3", "Amazon MP3 — play and download", "https://play.google.com/store/apps/details?id=com.amazon.mp3"),
                new Player("com.rhapsody", "Rhapsody", "https://play.google.com/store/apps/details?id=com.rhapsody"),
                new Player("com.andrew.appolo", "Appolo music player (CyanogenMod)", "https://f-droid.org/wiki/page/com.andrew.apollo"),
                new Player("com.android.music", "Android default music player", null),
                new Player("com.jetappfactory.jetaudio", "jetAudio music player", "https://play.google.com/store/apps/details?id=com.jetappfactory.jetaudio"),
                new Player("com.tbig.playerprotrial", "PlayerPro Trial Music Player", "https://play.google.com/store/apps/details?id=com.tbig.playerprotrial"),
                new Player("com.tbig.playerpro", "PlayerPro Music Player", "https://play.google.com/store/apps/details?id=com.tbig.playerpro"),
                new Player("com.lge.music", "LG music player", null),
        };

        AppDBManager.getInstance(context).getWritableDatabase().beginTransaction();

        try {
            for (Player defaultSupportedPlayer : defaultSupportedPlayers) {
                if (findPlayerByPackageName(defaultSupportedPlayer.getPackageName()) == null) {
                    add(defaultSupportedPlayer);
                }
            }

            AppDBManager.getInstance(context).getWritableDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            EasyTracker.getInstance(context).send(MapBuilder.createException(
                    "Can not update supported players: " + e.getMessage(),
                    false)
                    .build()
            );
            Loggi.e("Can not update supported players: " + e.getMessage());
        } finally {
            AppDBManager.getInstance(context).getWritableDatabase().endTransaction();
        }
    }

    public static class TableInfo {

        private static final String TABLE_NAME = "players";

        // region columns

        private static final String COLUMN_ID                     = "_id";
        private static final String COLUMN_PACKAGE_NAME           = "package_name";
        private static final String COLUMN_IS_ENABLED             = "is_enabled";
        private static final String COLUMN_DISPLAY_NAME           = "display_name";
        private static final String COLUMN_SCROBBLED_TRACKS_COUNT = "scrobbled_tracks_count";
        private static final String COLUMN_LINK                   = "link";

        private static final int NUM_COLUMN_ID                     = 0;
        private static final int NUM_COLUMN_PACKAGE_NAME           = 1;
        private static final int NUM_COLUMN_IS_ENABLED             = 2;
        private static final int NUM_COLUMN_DISPLAY_NAME           = 3;
        private static final int NUM_COLUMN_SCROBBLED_TRACKS_COUNT = 4;
        private static final int NUM_COLUMN_LINK                   = 5;

        // endregion

        private static final int DEFAULT_COLUMN_IS_ENABLED = 1;

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PACKAGE_NAME + " TEXT, " +
                COLUMN_IS_ENABLED + " INTEGER DEFAULT " + DEFAULT_COLUMN_IS_ENABLED + ", " +
                COLUMN_DISPLAY_NAME + " TEXT, " +
                COLUMN_SCROBBLED_TRACKS_COUNT + " INTEGER DEFAULT 0, " +
                COLUMN_LINK + " TEXT);";
    }
}
