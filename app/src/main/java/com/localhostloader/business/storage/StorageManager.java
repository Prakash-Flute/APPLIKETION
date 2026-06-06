package com.localhostloader.business.storage;

import android.content.Context;
import java.io.File;

public final class StorageManager {
    private final Context context;

    public StorageManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public File getTempDir() {
        File dir = new File(context.getCacheDir(), "temp");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public File getAppDir(String appId) {
        File dir = new File(context.getFilesDir(), "apps/" + appId);
        return dir;
    }

    public boolean deleteDir(File dir) {
        if (dir == null) return false;
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDir(child);
                }
            }
        }
        return dir.delete();
    }
}
