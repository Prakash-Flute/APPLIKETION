package com.localhostloader.runtime.launcher;

import com.localhostloader.business.model.AppEntry;
import com.localhostloader.runtime.process.ProcessManager;
import com.localhostloader.runtime.process.ProcessRecord;
import java.io.File;

public final class AppLauncher {
    public static LaunchSession launch(AppEntry app) {
        if ("static".equals(app.getRuntimeType())) {
            return new LaunchSession(null, "file://" + new File(app.getPath(), app.getEntryFile()).getAbsolutePath());
        } else if ("shell".equals(app.getRuntimeType())) {
            String[] cmd = {"sh", new File(app.getPath(), app.getEntryFile()).getAbsolutePath()};
            ProcessRecord pr = ProcessManager.getInstance().startProcess(app.getAppId(), cmd, new File(app.getPath()), null);
            return new LaunchSession(pr, "http://127.0.0.1:" + app.getPort());
        }
        return new LaunchSession(null, null);
    }

    public static class LaunchSession {
        private final ProcessRecord process;
        private final String url;

        public LaunchSession(ProcessRecord process, String url) {
            this.process = process;
            this.url = url;
        }

        public ProcessRecord getProcess() { return process; }
        public String getUrl() { return url; }
    }
}
