package com.localhostloader.business.importers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ZipExtractor {
    private static final long MAX_EXTRACTED_SIZE = 500 * 1024 * 1024;

    public static void extract(File zipFile, File destDir) throws IOException {
        if (!destDir.exists() && !destDir.mkdirs())
            throw new IOException("Failed to create dest dir: " + destDir);

        String destCanonical = destDir.getCanonicalPath() + File.separator;
        ZipInputStream zis = null;
        long totalExtracted = 0;
        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry;
            byte[] buf = new byte[8192];
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                File target = new File(destDir, name);
                String targetCanonical = target.getCanonicalPath();
                if (!targetCanonical.startsWith(destCanonical))
                    throw new IOException("Zip Slip attempt: " + name);

                if (entry.isDirectory()) {
                    if (!target.exists() && !target.mkdirs())
                        throw new IOException("Failed to create dir: " + target);
                } else {
                    File parent = target.getParentFile();
                    if (parent != null && !parent.exists() && !parent.mkdirs())
                        throw new IOException("Failed to create parent: " + parent);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(target);
                        int len;
                        while ((len = zis.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            totalExtracted += len;
                            if (totalExtracted > MAX_EXTRACTED_SIZE)
                                throw new IOException("Extracted size exceeds limit: " + MAX_EXTRACTED_SIZE);
                        }
                    } finally {
                        if (fos != null) try { fos.close(); } catch (IOException e) {}
                    }
                }
                zis.closeEntry();
            }
        } finally {
            if (zis != null) try { zis.close(); } catch (IOException e) {}
        }
    }
}
