package com.localhostloader.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.localhostloader.runtime.process.ProcessManager;
import com.localhostloader.runtime.process.ProcessRecord;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ProcessMonitorService extends Service {
    private ScheduledExecutorService scheduler;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (ProcessRecord r : ProcessManager.getInstance().getAllProcesses().values()) {
                    if (!r.getProcess().isAlive()) {
                        ProcessManager.getInstance().killProcess(r.getId());
                    }
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onDestroy() {
        if (scheduler != null) scheduler.shutdownNow();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
