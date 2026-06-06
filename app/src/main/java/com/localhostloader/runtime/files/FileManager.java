package com.localhostloader.runtime.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class FileManager {
    public static boolean copy(File src, File dst) throws IOException {
        if (src.isDirectory()) {
            if (!dst.exists()) dst.mkdirs();
            File[] files = src.listFiles();
            if (files != null) {
                for (File f : files) {
                    copy(f, new File(dst, f.getName()));
                }
            }
            return true;
        } else {
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(src);
                out = new FileOutputStream(dst);
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
                return true;
            } finally {
                if (in != null) try { in.close(); } catch (IOException e) {}
                if (out != null) try { out.close(); } catch (IOException e) {}
            }
        }
    }

    public static boolean delete(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File c : files) delete(c);
            }
        }
        return f.delete();
    }

    public static long size(File f) {
        if (f.isFile()) return f.length();
        long size = 0;
        File[] files = f.listFiles();
        if (files != null) {
            for (File c : files) size += size(c);
        }
        return size;
    }
}
