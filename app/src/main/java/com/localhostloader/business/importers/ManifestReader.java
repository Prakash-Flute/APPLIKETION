package com.localhostloader.business.importers;

import com.localhostloader.business.model.Manifest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ManifestReader {
    public static Manifest read(File manifestFile) throws IOException, JSONException {
        String jsonText = readFileToString(manifestFile);
        JSONObject root = new JSONObject(jsonText);
        int version = root.optInt("manifest_version", 1);
        if (version != 1) throw new JSONException("Unsupported manifest version: " + version);
        JSONObject app = root.getJSONObject("app");
        String appId = app.optString("id", null);
        if (appId == null || appId.trim().isEmpty()) throw new JSONException("Missing app.id");
        String name = app.getString("name");
        String versionStr = app.optString("version", null);
        String description = app.optString("description", null);
        String iconPath = app.optString("icon", null);
        JSONObject runtime = root.getJSONObject("runtime");
        String runtimeType = runtime.getString("type");
        String runtimeVersion = runtime.optString("version", null);
        String entry = runtime.getString("entry");
        int port = runtime.optInt("port", 0);
        List<String> deps = new ArrayList<>();
        JSONArray depsArr = root.optJSONArray("dependencies");
        if (depsArr != null) for (int i = 0; i < depsArr.length(); i++) deps.add(depsArr.getString(i));
        List<String> perms = new ArrayList<>();
        JSONArray permsArr = root.optJSONArray("permissions");
        if (permsArr != null) for (int i = 0; i < permsArr.length(); i++) perms.add(permsArr.getString(i));
        Map<String, String> env = new HashMap<>();
        JSONObject envObj = root.optJSONObject("environment");
        if (envObj != null) {
            JSONArray keys = envObj.names();
            if (keys != null) for (int i = 0; i < keys.length(); i++) {
                String k = keys.getString(i);
                env.put(k, envObj.getString(k));
            }
        }
        boolean background = root.optBoolean("background", false);
        boolean autoStart = root.optBoolean("auto_start", false);
        return new Manifest(version, appId, name, versionStr, description, iconPath,
                runtimeType, runtimeVersion, entry, port, deps, perms, env, background, autoStart, jsonText);
    }
    private static String readFileToString(File f) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            char[] buf = new char[8192];
            int len;
            while ((len = r.read(buf)) != -1) sb.append(buf, 0, len);
        } finally {
            if (r != null) try { r.close(); } catch (IOException e) {}
        }
        return sb.toString();
    }
}
