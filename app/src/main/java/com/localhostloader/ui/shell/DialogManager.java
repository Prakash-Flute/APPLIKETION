package com.localhostloader.ui.shell;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public final class DialogManager {
    private final Context context;

    public DialogManager(Context context) {
        this.context = context;
    }

    public void showAlert(String title, String message, String positiveText, final Runnable positive, String negativeText, final Runnable negative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        if (positiveText != null && positive != null) {
            builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positive.run();
                }
            });
        }
        if (negativeText != null && negative != null) {
            builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    negative.run();
                }
            });
        }
        builder.show();
    }

    public void showConfirm(String title, String message, final Runnable positive, final Runnable negative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (positive != null) positive.run();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (negative != null) negative.run();
            }
        });
        builder.show();
    }

    public void showError(String title, String message, final Runnable dismiss) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dismiss != null) dismiss.run();
            }
        });
        builder.show();
    }
}
