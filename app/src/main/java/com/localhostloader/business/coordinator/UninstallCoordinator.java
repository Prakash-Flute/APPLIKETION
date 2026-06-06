package com.localhostloader.business.coordinator;

import com.localhostloader.business.data.DbHelper;
import com.localhostloader.business.model.AppEntry;
import com.localhostloader.business.model.UninstallResult;
import com.localhostloader.business.storage.StorageManager;
import java.io.File;

public final class UninstallCoordinator {
    private final StorageManager storage;
    private final DbHelper db;
    public UninstallCoordinator(StorageManager storage, DbHelper db) {
        this.storage = storage;
        this.db = db;
    }
    public UninstallResult uninstall(String appId) {
        AppEntry app = db.getApp(appId);
        if (app == null) return new UninstallResult(false, appId, "Not found");
        try {
            db.deleteApp(appId);
        } catch (Exception e) {
            return new UninstallResult(false, appId, "DB error: " + e.getMessage());
        }
        boolean ok = storage.deleteDir(new File(app.getPath()));
        String msg = ok ? "Uninstalled" : "Uninstalled (files may remain)";
        return new UninstallResult(true, appId, msg);
    }
}
