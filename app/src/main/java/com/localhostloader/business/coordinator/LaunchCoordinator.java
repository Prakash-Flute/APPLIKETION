package com.localhostloader.business.coordinator;

import com.localhostloader.business.data.DbHelper;
import com.localhostloader.business.model.AppEntry;
import com.localhostloader.business.model.LaunchResult;
import java.io.File;

public final class LaunchCoordinator {
    private final DbHelper db;
    public LaunchCoordinator(DbHelper db) { this.db = db; }
    public LaunchResult prepareLaunch(String appId) {
        AppEntry app = db.getApp(appId);
        if (app == null) return new LaunchResult(false, null, null, null, null, "App not found");
        db.updateLastLaunched(appId, System.currentTimeMillis());
        String runtime = app.getRuntimeType();
        String targetUrl;
        if ("static".equals(runtime)) {
            File entry = new File(app.getPath(), app.getEntryFile());
            if (!entry.exists() || !entry.isFile())
                return new LaunchResult(false, null, null, null, null, "Entry file missing");
            targetUrl = "file://" + entry.getAbsolutePath();
        } else {
            int port = app.getPort();
            if (port <= 0) return new LaunchResult(false, null, null, null, null, "Invalid port");
            targetUrl = "http://127.0.0.1:" + port;
        }
        return new LaunchResult(true, targetUrl, runtime, appId, app.getPath(), null);
    }
}
