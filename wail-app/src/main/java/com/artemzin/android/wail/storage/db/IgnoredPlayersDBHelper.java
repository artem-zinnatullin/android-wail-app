package com.artemzin.android.wail.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;

import com.artemzin.android.wail.util.Loggi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya Murzinov [murz42@gmail.com]
 */
public class IgnoredPlayersDBHelper {
    private static volatile IgnoredPlayersDBHelper instance;
    private Context context;

    private IgnoredPlayersDBHelper(Context context) {
        this.context = context;
    }

    public static IgnoredPlayersDBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (IgnoredPlayersDBHelper.class) {
                if (instance == null) {
                    instance = new IgnoredPlayersDBHelper(context);
                }
            }
        }
        return instance;
    }

    public synchronized long add(String packageName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableInfo.COLUMN_PACKAGE_NAME, packageName);

        final long rowId = AppDBManager.getInstance(context).getWritableDatabase()
                .insert(TableInfo.TABLE_NAME, null, contentValues);

        if (rowId == -1) {
            Loggi.e("IgnoredPlayersDBHelper can not add ignored player: " + packageName);
        }

        return rowId;
    }

    public synchronized List<ApplicationInfo> getAll() {
        List<ApplicationInfo> result = new ArrayList<>();
        Cursor cursor = AppDBManager.getInstance(context).getReadableDatabase()
                .query(false, TableInfo.TABLE_NAME, new String[]{TableInfo.COLUMN_PACKAGE_NAME}, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {
                String packageName = cursor.getString(TableInfo.NUM_COLUMN_PACKAGE_NAME);
                ApplicationInfo applicationInfo;
                try {
                    applicationInfo =
                            context.getPackageManager().getApplicationInfo(packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    Loggi.w("IgnoredPlayersDBHelper could not get application info: " + packageName);
                    applicationInfo = new ApplicationInfo();
                    applicationInfo.packageName = packageName;
                }
                result.add(applicationInfo);
                cursor.moveToNext();
            }
        }

        return result;
    }

    public synchronized boolean contains(String packageName) {
        return AppDBManager.getInstance(context).getReadableDatabase()
                .query(
                        TableInfo.TABLE_NAME,
                        new String[] {TableInfo.COLUMN_PACKAGE_NAME},
                        TableInfo.COLUMN_PACKAGE_NAME + "=?",
                        new String[]{String.valueOf(packageName)},
                        null,
                        null,
                        null
                ).getCount() >= 1;
    }

    public synchronized int deleteAll() {
        return AppDBManager.getInstance(context).getWritableDatabase()
                .delete(TableInfo.TABLE_NAME, null, null);
    }

    public synchronized int delete(String packageName) {
        return AppDBManager.getInstance(context).getWritableDatabase()
                .delete(TableInfo.TABLE_NAME,
                        TableInfo.COLUMN_PACKAGE_NAME + "=?",
                        new String[]{String.valueOf(packageName)});
    }

    public static class TableInfo {
        private static final String TABLE_NAME = "ignored_players";

        // region columns

        private static final String COLUMN_PACKAGE_NAME = "package_name";

        private static final int NUM_COLUMN_PACKAGE_NAME = 0;

        // endregion

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_PACKAGE_NAME + " TEXT);";
    }
}
