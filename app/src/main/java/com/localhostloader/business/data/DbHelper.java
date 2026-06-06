package com.localhostloader.business.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.localhostloader.business.model.AppEntry;
import java.util.ArrayList;
import java.util.List;

public final class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "localhostloader.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_APPS = "apps";

    public DbHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_APPS + " (" +
                "app_id TEXT PRIMARY KEY," +
                "name TEXT," +
                "version TEXT," +
                "path TEXT," +
                "entry_file TEXT," +
                "icon_path TEXT," +
                "raw_json TEXT," +
                "installed_at INTEGER," +
                "runtime_type TEXT," +
                "port INTEGER," +
                "last_launched INTEGER" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPS);
        onCreate(db);
    }

    public void insertApp(AppEntry app) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("app_id", app.getAppId());
        cv.put("name", app.getName());
        cv.put("version", app.getVersion());
        cv.put("path", app.getPath());
        cv.put("entry_file", app.getEntryFile());
        cv.put("icon_path", app.getIconPath());
        cv.put("raw_json", app.getRawJson());
        cv.put("installed_at", app.getInstalledAt());
        cv.put("runtime_type", app.getRuntimeType());
        cv.put("port", app.getPort());
        cv.put("last_launched", app.getLastLaunched());
        db.insert(TABLE_APPS, null, cv);
    }

    public AppEntry getApp(String appId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_APPS, null, "app_id=?", new String[]{appId}, null, null, null);
        if (c != null && c.moveToFirst()) {
            AppEntry app = cursorToApp(c);
            c.close();
            return app;
        }
        if (c != null) c.close();
        return null;
    }

    public List<AppEntry> getAllApps() {
        List<AppEntry> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_APPS, null, null, null, null, null, "installed_at DESC");
        if (c != null) {
            while (c.moveToNext()) {
                list.add(cursorToApp(c));
            }
            c.close();
        }
        return list;
    }

    public void deleteApp(String appId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_APPS, "app_id=?", new String[]{appId});
    }

    public void updateLastLaunched(String appId, long time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("last_launched", time);
        db.update(TABLE_APPS, cv, "app_id=?", new String[]{appId});
    }

    private AppEntry cursorToApp(Cursor c) {
        AppEntry app = new AppEntry(
                c.getString(c.getColumnIndexOrThrow("app_id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getString(c.getColumnIndexOrThrow("version")),
                c.getString(c.getColumnIndexOrThrow("path")),
                c.getString(c.getColumnIndexOrThrow("entry_file")),
                c.getString(c.getColumnIndexOrThrow("icon_path")),
                c.getString(c.getColumnIndexOrThrow("raw_json")),
                c.getLong(c.getColumnIndexOrThrow("installed_at"))
        );
        app.setRuntimeType(c.getString(c.getColumnIndexOrThrow("runtime_type")));
        app.setPort(c.getInt(c.getColumnIndexOrThrow("port")));
        app.setLastLaunched(c.getLong(c.getColumnIndexOrThrow("last_launched")));
        return app;
    }
}
