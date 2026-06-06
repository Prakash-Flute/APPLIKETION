package com.localhostloader.business.model;

public final class InstallResult {
    private final boolean success;
    private final String appId;
    private final String message;
    private final String installedPath;
    public InstallResult(boolean success, String appId, String message, String installedPath) {
        if (message == null) throw new NullPointerException("message");
        this.success = success;
        this.appId = appId;
        this.message = message;
        this.installedPath = installedPath;
        if (success && (appId == null || appId.trim().isEmpty()))
            throw new IllegalArgumentException("success=true requires non-empty appId");
    }
    public boolean isSuccess() { return success; }
    public String getAppId() { return appId; }
    public String getMessage() { return message; }
    public String getInstalledPath() { return installedPath; }
}
