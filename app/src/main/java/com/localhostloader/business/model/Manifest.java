package com.localhostloader.business.model;

import java.util.List;
import java.util.Map;

public final class Manifest {
    private final int manifestVersion;
    private final String appId;
    private final String name;
    private final String version;
    private final String description;
    private final String iconPath;
    private final String runtimeType;
    private final String runtimeVersion;
    private final String entry;
    private final int port;
    private final List<String> dependencies;
    private final List<String> permissions;
    private final Map<String, String> environment;
    private final boolean background;
    private final boolean autoStart;
    private final String rawJson;

    public Manifest(int manifestVersion, String appId, String name, String version,
                    String description, String iconPath, String runtimeType,
                    String runtimeVersion, String entry, int port,
                    List<String> dependencies, List<String> permissions,
                    Map<String, String> environment, boolean background,
                    boolean autoStart, String rawJson) {
        this.manifestVersion = manifestVersion;
        this.appId = appId;
        this.name = name;
        this.version = version;
        this.description = description;
        this.iconPath = iconPath;
        this.runtimeType = runtimeType;
        this.runtimeVersion = runtimeVersion;
        this.entry = entry;
        this.port = port;
        this.dependencies = dependencies;
        this.permissions = permissions;
        this.environment = environment;
        this.background = background;
        this.autoStart = autoStart;
        this.rawJson = rawJson;
    }

    public int getManifestVersion() { return manifestVersion; }
    public String getAppId() { return appId; }
    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getDescription() { return description; }
    public String getIconPath() { return iconPath; }
    public String getRuntimeType() { return runtimeType; }
    public String getRuntimeVersion() { return runtimeVersion; }
    public String getEntryPoint() { return entry; }
    public int getPort() { return port; }
    public List<String> getDependencies() { return dependencies; }
    public List<String> getPermissions() { return permissions; }
    public Map<String, String> getEnvironment() { return environment; }
    public boolean isBackground() { return background; }
    public boolean isAutoStart() { return autoStart; }
    public String getRawJson() { return rawJson; }
}
