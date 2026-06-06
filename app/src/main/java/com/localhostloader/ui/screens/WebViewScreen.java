package com.localhostloader.ui.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.localhostloader.ui.shell.Screen;
import com.localhostloader.ui.shell.ScreenActions;
import com.localhostloader.ui.shell.ScreenId;
import com.localhostloader.ui.shell.UIShellCallback;
import java.io.File;

public class WebViewScreen implements Screen {
    private WebView webView;
    private UIShellCallback callback;

    @Override
    public View render(Context context, LayoutInflater inflater, ViewGroup container, Bundle args) {
        webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (callback != null) {
                    Bundle data = new Bundle();
                    data.putString("error", description);
                    callback.onScreenEvent(ScreenId.WEBVIEW, ScreenActions.WEBVIEW_ERROR, data);
                }
            }
        });
        return webView;
    }

    @Override
    public void bind(View rootView, Bundle args) {
        if (args == null) {
            showErrorPage("No arguments provided");
            return;
        }
        if (rootView.getContext() instanceof UIShellCallback)
            callback = (UIShellCallback) rootView.getContext();
        String targetUrl = args.getString("targetUrl");
        String appPath = args.getString("appPath");
        if (targetUrl != null && appPath != null && validate(targetUrl, appPath)) {
            webView.loadUrl(targetUrl);
        } else {
            showErrorPage("Invalid URL");
            if (callback != null) {
                Bundle data = new Bundle();
                data.putString("error", "Invalid URL");
                callback.onScreenEvent(ScreenId.WEBVIEW, ScreenActions.WEBVIEW_ERROR, data);
            }
        }
    }

    private void showErrorPage(String message) {
        String safeMsg = TextUtils.htmlEncode(message);
        webView.loadData("<html><body><h1>Error</h1><p>" + safeMsg + "</p></body></html>", "text/html", "UTF-8");
    }

    private boolean validate(String url, String appPath) {
        if (url.startsWith("file://")) {
            String path = url.substring("file://".length());
            File target = new File(path);
            File appDir = new File(appPath);
            try {
                String tCan = target.getCanonicalPath();
                String aCan = appDir.getCanonicalPath() + File.separator;
                return tCan.startsWith(aCan) && target.exists() && target.isFile();
            } catch (Exception e) { return false; }
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            int port = uri.getPort();
            return ("127.0.0.1".equals(host) || "localhost".equals(host)) &&
                    (port == -1 || (port >= 1024 && port <= 65535));
        }
        return false;
    }

    @Override public void onResume() { if (webView != null) webView.onResume(); }
    @Override public void onPause() { if (webView != null) webView.onPause(); }
    @Override public void onDestroy() { if (webView != null) webView.destroy(); webView = null; callback = null; }
    @Override public boolean onBackPressed() { if (webView != null && webView.canGoBack()) { webView.goBack(); return true; } return false; }
    @Override
    public void update(Bundle args) {
        if (args == null) return;
        String newUrl = args.getString("targetUrl");
        String newPath = args.getString("appPath");
        if (newUrl != null && newPath != null && validate(newUrl, newPath))
            webView.loadUrl(newUrl);
    }
}
