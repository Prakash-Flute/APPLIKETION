package com.localhostloader.business.model;

public final class UninstallResult {
    private final boolean success;
    private final String appId;
    private final String message;
    public UninstallResult(boolean success, String appId, String message) {
        if (appId == null) throw new NullPointerException("appId");
        if (message == null) throw new NullPointerException("message");
        this.success = success;
        this.appId = appId;
        this.message = message;
    }
    public boolean isSuccess() { return success; }
    public String getAppId() { return appId; }
    public String getMessage() { return message; }
}
