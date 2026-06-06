package com.localhostloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import com.localhostloader.business.callbacks.InstallCallback;
import com.localhostloader.business.callbacks.LaunchCallback;
import com.localhostloader.business.callbacks.UninstallCallback;
import com.localhostloader.business.coordinator.ActionDispatcher;
import com.localhostloader.business.data.DbHelper;
import com.localhostloader.business.model.AppEntry;
import com.localhostloader.business.model.InstallResult;
import com.localhostloader.business.model.LaunchResult;
import com.localhostloader.business.model.UninstallResult;
import com.localhostloader.ui.screens.HomeScreen;
import com.localhostloader.ui.screens.ImportScreen;
import com.localhostloader.ui.screens.WebViewScreen;
import com.localhostloader.ui.shell.ScreenActions;
import com.localhostloader.ui.shell.ScreenFactory;
import com.localhostloader.ui.shell.ScreenId;
import com.localhostloader.ui.shell.ScreenRegistry;
import com.localhostloader.ui.shell.UIShell;
import com.localhostloader.ui.shell.UIShellCallback;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class MainActivity extends Activity implements UIShellCallback {
    private ActionDispatcher dispatcher;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenRegistry.register(ScreenId.HOME, new ScreenFactory() {
            @Override
            public com.localhostloader.ui.shell.Screen create() {
                return new HomeScreen();
            }
        });
        ScreenRegistry.register(ScreenId.IMPORT, new ScreenFactory() {
            @Override
            public com.localhostloader.ui.shell.Screen create() {
                return new ImportScreen();
            }
        });
        ScreenRegistry.register(ScreenId.WEBVIEW, new ScreenFactory() {
            @Override
            public com.localhostloader.ui.shell.Screen create() {
                return new WebViewScreen();
            }
        });
        UIShell.initialize(this, this);
        dispatcher = new ActionDispatcher(getApplicationContext());
        dbHelper = new DbHelper(getApplicationContext());
        refreshHomeScreen();
        UIShell.showScreen(ScreenId.HOME, null);
    }

    @Override
    public void onScreenEvent(ScreenId source, String action, Bundle data) {
        if (ScreenActions.IMPORT.equals(action)) {
            UIShell.getDialogManager().showAlert("Import App", "Enter URL",
                    "URL", new Runnable() {
                        @Override
                        public void run() {
                            showUrlInputDialog();
                        }
                    },
                    "File", new Runnable() {
                        @Override
                        public void run() {
                            openFilePicker();
                        }
                    });
        } else if (ScreenActions.LAUNCH.equals(action)) {
            final String appId = data.getString("appId");
            if (appId == null) return;
            UIShell.getLoadingManager().show("Preparing...");
            dispatcher.prepareLaunch(appId, new LaunchCallback() {
                @Override
                public void onLaunchPrepared(LaunchResult result) {
                    UIShell.getLoadingManager().hide();
                    if (result.isSuccess()) {
                        Bundle args = new Bundle();
                        args.putString("targetUrl", result.getTargetUrl());
                        args.putString("appPath", result.getAppPath());
                        UIShell.showScreen(ScreenId.WEBVIEW, args);
                    } else {
                        UIShell.getDialogManager().showError("Launch Failed", result.getErrorMessage(), null);
                    }
                }
            });
        } else if (ScreenActions.UNINSTALL.equals(action)) {
            final String appId = data.getString("appId");
            if (appId == null) return;
            UIShell.getDialogManager().showConfirm("Uninstall", "Remove this app?",
                    new Runnable() {
                        @Override
                        public void run() {
                            UIShell.getLoadingManager().show("Uninstalling...");
                            dispatcher.uninstall(appId, new UninstallCallback() {
                                @Override
                                public void onUninstallComplete(UninstallResult result) {
                                    UIShell.getLoadingManager().hide();
                                    refreshHomeScreen();
                                }
                            });
                        }
                    }, null);
        }
    }

    private void refreshHomeScreen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<AppEntry> apps = dbHelper.getAllApps();
                JSONArray arr = new JSONArray();
                try {
                    for (AppEntry app : apps) {
                        JSONObject obj = new JSONObject();
                        obj.put("id", app.getAppId());
                        obj.put("name", app.getName());
                        obj.put("version", app.getVersion());
                        obj.put("iconPath", app.getIconPath() != null ? app.getIconPath() : "");
                        arr.put(obj);
                    }
                } catch (Exception e) { /* ignore */ }
                final Bundle update = new Bundle();
                update.putString("apps_json", arr.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UIShell.updateScreen(ScreenId.HOME, update);
                    }
                });
            }
        }).start();
    }

    private void showUrlInputDialog() {
        final EditText input = new EditText(this);
        input.setHint("https://example.com/app.zip");
        new AlertDialog.Builder(this)
            .setTitle("Import from URL")
            .setView(input)
            .setPositiveButton("Install", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String url = input.getText().toString().trim();
                    if (!url.isEmpty()) {
                        UIShell.getLoadingManager().show("Installing...");
                        dispatcher.startInstall(url, new InstallCallback() {
                            @Override
                            public void onInstallComplete(InstallResult result) {
                                UIShell.getLoadingManager().hide();
                                if (result.isSuccess()) {
                                    refreshHomeScreen();
                                } else {
                                    UIShell.getDialogManager().showError("Install Failed", result.getMessage(), null);
                                }
                            }
                        });
                    }
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            final Uri uri = data.getData();
            if (uri != null) {
                UIShell.getLoadingManager().show("Installing...");
                dispatcher.startInstall(uri, new InstallCallback() {
                    @Override
                    public void onInstallComplete(InstallResult result) {
                        UIShell.getLoadingManager().hide();
                        if (result.isSuccess()) {
                            refreshHomeScreen();
                        } else {
                            UIShell.getDialogManager().showError("Install Failed", result.getMessage(), null);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onScreenTransition(ScreenId from, ScreenId to) {}

    @Override
    public void onBackPressed() {
        if (!UIShell.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        UIShell.destroy();
        super.onDestroy();
    }
}
