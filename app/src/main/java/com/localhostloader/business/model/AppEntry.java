package com.localhostloader.business.model;

public final class AppEntry {
    private final String appId;
    private final String name;
    private final String version;
    private final String path;
    private final String entryFile;
    private final String iconPath;
    private final String rawJson;
    private final long installedAt;
    private String runtimeType;
    private int port;
    private long lastLaunched;

    public AppEntry(String appId, String name, String version, String path,
                    String entryFile, String iconPath, String rawJson, long installedAt) {
        this.appId = appId;
        this.name = name;
        this.version = version;
        this.path = path;
        this.entryFile = entryFile;
        this.iconPath = iconPath;
        this.rawJson = rawJson;
        this.installedAt = installedAt;
    }

    public String getAppId() { return appId; }
    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getPath() { return path; }
    public String getEntryFile() { return entryFile; }
    public String getIconPath() { return iconPath; }
    public String getRawJson() { return rawJson; }
    public long getInstalledAt() { return installedAt; }
    public String getRuntimeType() { return runtimeType; }
    public int getPort() { return port; }
    public long getLastLaunched() { return lastLaunched; }

    public void setRuntimeType(String runtimeType) { this.runtimeType = runtimeType; }
    public void setPort(int port) { this.port = port; }
    public void setLastLaunched(long lastLaunched) { this.lastLaunched = lastLaunched; }
}
