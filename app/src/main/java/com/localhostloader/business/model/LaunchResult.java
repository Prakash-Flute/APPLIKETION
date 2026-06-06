package com.localhostloader.business.model;

public final class LaunchResult {
    private final boolean success;
    private final String targetUrl;
    private final String runtimeType;
    private final String appId;
    private final String appPath;
    private final String errorMessage;
    public LaunchResult(boolean success, String targetUrl, String runtimeType,
                        String appId, String appPath, String errorMessage) {
        this.success = success;
        if (success) {
            if (targetUrl == null || runtimeType == null || appId == null || appPath == null)
                throw new IllegalArgumentException("success=true requires targetUrl, runtimeType, appId, appPath");
            if (errorMessage != null)
                throw new IllegalArgumentException("success=true requires errorMessage == null");
            this.targetUrl = targetUrl;
            this.runtimeType = runtimeType;
            this.appId = appId;
            this.appPath = appPath;
            this.errorMessage = null;
        } else {
            if (errorMessage == null)
                throw new IllegalArgumentException("success=false requires errorMessage non-null");
            this.targetUrl = null;
            this.runtimeType = null;
            this.appId = null;
            this.appPath = null;
            this.errorMessage = errorMessage;
        }
    }
    public boolean isSuccess() { return success; }
    public String getTargetUrl() { return targetUrl; }
    public String getRuntimeType() { return runtimeType; }
    public String getAppId() { return appId; }
    public String getAppPath() { return appPath; }
    public String getErrorMessage() { return errorMessage; }
}
