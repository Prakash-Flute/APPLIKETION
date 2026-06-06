package com.localhostloader.ui.shell;

import android.app.ProgressDialog;
import android.content.Context;

public final class LoadingManager {
    private ProgressDialog dialog;
    private final Context context;

    public LoadingManager(Context context) {
        this.context = context;
    }

    public void show(String message) {
        hide();
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void hide() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }
}
