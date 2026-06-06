package com.localhostloader.business.coordinator;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.localhostloader.business.callbacks.InstallCallback;
import com.localhostloader.business.callbacks.LaunchCallback;
import com.localhostloader.business.callbacks.UninstallCallback;
import com.localhostloader.business.data.DbHelper;
import com.localhostloader.business.model.InstallResult;
import com.localhostloader.business.model.LaunchResult;
import com.localhostloader.business.model.UninstallResult;
import com.localhostloader.business.storage.StorageManager;
import java.lang.ref.WeakReference;

public final class ActionDispatcher {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final InstallCoordinator installCoordinator;
    private final LaunchCoordinator launchCoordinator;
    private final UninstallCoordinator uninstallCoordinator;

    public ActionDispatcher(Context appContext) {
        Context ctx = appContext.getApplicationContext();
        StorageManager storage = new StorageManager(ctx);
        DbHelper db = new DbHelper(ctx);
        this.installCoordinator = new InstallCoordinator(ctx, storage, db);
        this.launchCoordinator = new LaunchCoordinator(db);
        this.uninstallCoordinator = new UninstallCoordinator(storage, db);
    }

    public void startInstall(final String url, final InstallCallback callback) {
        final WeakReference<InstallCallback> weakCb = new WeakReference<>(callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final InstallResult result = installCoordinator.installFromUrl(url);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        InstallCallback cb = weakCb.get();
                        if (cb != null) cb.onInstallComplete(result);
                    }
                });
            }
        }).start();
    }

    public void startInstall(final Uri uri, final InstallCallback callback) {
        final WeakReference<InstallCallback> weakCb = new WeakReference<>(callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final InstallResult result = installCoordinator.installFromUri(uri);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        InstallCallback cb = weakCb.get();
                        if (cb != null) cb.onInstallComplete(result);
                    }
                });
            }
        }).start();
    }

    public void prepareLaunch(final String appId, final LaunchCallback callback) {
        final WeakReference<LaunchCallback> weakCb = new WeakReference<>(callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final LaunchResult result = launchCoordinator.prepareLaunch(appId);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LaunchCallback cb = weakCb.get();
                        if (cb != null) cb.onLaunchPrepared(result);
                    }
                });
            }
        }).start();
    }

    public void uninstall(final String appId, final UninstallCallback callback) {
        final WeakReference<UninstallCallback> weakCb = new WeakReference<>(callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final UninstallResult result = uninstallCoordinator.uninstall(appId);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        UninstallCallback cb = weakCb.get();
                        if (cb != null) cb.onUninstallComplete(result);
                    }
                });
            }
        }).start();
    }
}
