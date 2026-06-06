package com.localhostloader.business.coordinator;

import android.content.Context;
import android.net.Uri;
import com.localhostloader.business.data.DbHelper;
import com.localhostloader.business.importers.Downloader;
import com.localhostloader.business.importers.ManifestReader;
import com.localhostloader.business.importers.ZipExtractor;
import com.localhostloader.business.model.AppEntry;
import com.localhostloader.business.model.InstallResult;
import com.localhostloader.business.model.Manifest;
import com.localhostloader.business.storage.StorageManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

public final class InstallCoordinator {
    private static final long MAX_PACKAGE_SIZE = 200 * 1024 * 1024;

    private final Context context;
    private final StorageManager storage;
    private final DbHelper db;

    public InstallCoordinator(Context context, StorageManager storage, DbHelper db) {
        this.context = context.getApplicationContext();
        this.storage = storage;
        this.db = db;
    }

    public InstallResult installFromUrl(final String url) {
        return performInstall(new InstallAction() {
            @Override
            public File run(File tempDir) throws Exception {
                File zip = new File(tempDir, "app.zip");
                Downloader.download(url, zip, MAX_PACKAGE_SIZE);
                File extracted = new File(tempDir, "extracted");
                ZipExtractor.extract(zip, extracted);
                return extracted;
            }
        });
    }

    public InstallResult installFromUri(final Uri uri) {
        return performInstall(new InstallAction() {
            @Override
            public File run(File tempDir) throws Exception {
                File zip = new File(tempDir, "app.zip");
                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    is = context.getContentResolver().openInputStream(uri);
                    if (is == null) throw new Exception("Cannot open URI");
                    fos = new FileOutputStream(zip);
                    byte[] buf = new byte[8192];
                    int len;
                    long total = 0;
                    while ((len = is.read(buf)) != -1) {
                        total += len;
                        if (total > MAX_PACKAGE_SIZE)
                            throw new Exception("Package too large (>" + MAX_PACKAGE_SIZE + " bytes)");
                        fos.write(buf, 0, len);
                    }
                } finally {
                    if (is != null) try { is.close(); } catch (Exception e) {}
                    if (fos != null) try { fos.close(); } catch (Exception e) {}
                }
                File extracted = new File(tempDir, "extracted");
                ZipExtractor.extract(zip, extracted);
                return extracted;
            }
        });
    }

    private interface InstallAction {
        File run(File tempDir) throws Exception;
    }

    private InstallResult performInstall(InstallAction action) {
        File tempWorkspace = null;
        boolean moveSucceeded = false;
        File targetDir = null;
        try {
            tempWorkspace = new File(storage.getTempDir(), UUID.randomUUID().toString());
            if (!tempWorkspace.mkdirs()) throw new Exception("Cannot create temp workspace");
            File extractedDir = action.run(tempWorkspace);
            File manifestFile = new File(extractedDir, "manifest.json");
            Manifest manifest = ManifestReader.read(manifestFile);
            String appId = manifest.getAppId().toLowerCase().trim();
            if (db.getApp(appId) != null)
                return new InstallResult(false, appId, "App already installed", null);
            targetDir = storage.getAppDir(appId);
            if (targetDir.exists())
                return new InstallResult(false, appId, "Install directory exists", null);
            if (!extractedDir.renameTo(targetDir)) {
                copyRecursively(extractedDir, targetDir);
                deleteRecursively(extractedDir);
            }
            moveSucceeded = true;
            long now = System.currentTimeMillis();
            AppEntry entry = new AppEntry(appId, manifest.getName(), manifest.getVersion(),
                    targetDir.getAbsolutePath(), manifest.getEntryPoint(),
                    manifest.getIconPath(), manifest.getRawJson(), now);
            entry.setRuntimeType(manifest.getRuntimeType());
            entry.setPort(manifest.getPort());
            db.insertApp(entry);
            deleteRecursively(tempWorkspace);
            return new InstallResult(true, appId, "Installation successful", targetDir.getAbsolutePath());
        } catch (Exception e) {
            if (moveSucceeded && targetDir != null) deleteRecursively(targetDir);
            if (tempWorkspace != null) deleteRecursively(tempWorkspace);
            return new InstallResult(false, null, e.getMessage(), null);
        }
    }

    private void copyRecursively(File src, File dst) throws Exception {
        if (src.isDirectory()) {
            if (!dst.exists() && !dst.mkdirs()) throw new Exception("Cannot create dir " + dst);
            File[] children = src.listFiles();
            if (children != null) {
                for (File child : children) copyRecursively(child, new File(dst, child.getName()));
            }
        } else {
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(src);
                out = new FileOutputStream(dst);
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
            } finally {
                if (in != null) try { in.close(); } catch (Exception e) {}
                if (out != null) try { out.close(); } catch (Exception e) {}
            }
        }
    }

    private void deleteRecursively(File f) {
        if (f == null) return;
        if (f.isDirectory()) {
            File[] kids = f.listFiles();
            if (kids != null) {
                for (File k : kids) deleteRecursively(k);
            }
        }
        f.delete();
    }
}
