package com.localhostloader.business.importers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Downloader {
    private static final int MAX_REDIRECTS = 5;
    private static final long DEFAULT_MAX_SIZE = 200 * 1024 * 1024;

    public static void download(String urlString, File destination) throws IOException {
        download(urlString, destination, DEFAULT_MAX_SIZE);
    }

    public static void download(String urlString, File destination, long maxSize) throws IOException {
        File parent = destination.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs())
            throw new IOException("Failed to create parent dir: " + parent);

        String currentUrl = urlString;
        int redirects = 0;
        while (redirects <= MAX_REDIRECTS) {
            HttpURLConnection conn = null;
            InputStream in = null;
            FileOutputStream out = null;
            boolean success = false;
            boolean isRedirect = false;
            try {
                URL url = new URL(currentUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.setRequestProperty("User-Agent", "LocalhostLoader/1.0");
                conn.setInstanceFollowRedirects(false);
                conn.connect();

                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_MOVED_PERM ||
                    code == HttpURLConnection.HTTP_MOVED_TEMP ||
                    code == HttpURLConnection.HTTP_SEE_OTHER ||
                    code == 307 || code == 308) {
                    String location = conn.getHeaderField("Location");
                    if (location == null) throw new IOException("Redirect without Location");
                    URL newUrl = new URL(url, location);
                    currentUrl = newUrl.toString();
                    redirects++;
                    isRedirect = true;
                    continue;
                }
                if (code != HttpURLConnection.HTTP_OK)
                    throw new IOException("HTTP error: " + code);

                long contentLength = conn.getContentLengthLong();
                if (contentLength > maxSize)
                    throw new IOException("Package too large: " + contentLength + " bytes (max " + maxSize + ")");

                in = conn.getInputStream();
                out = new FileOutputStream(destination);
                byte[] buf = new byte[8192];
                int len;
                long total = 0;
                while ((len = in.read(buf)) != -1) {
                    total += len;
                    if (total > maxSize) throw new IOException("Exceeded max size during download");
                    out.write(buf, 0, len);
                }
                success = true;
            } finally {
                if (in != null) try { in.close(); } catch (IOException e) {}
                if (out != null) try { out.close(); } catch (IOException e) {}
                if (conn != null) conn.disconnect();
                if (!success && !isRedirect && destination.exists()) destination.delete();
            }
            if (success) return;
        }
        throw new IOException("Too many redirects (max " + MAX_REDIRECTS + ")");
    }
}
