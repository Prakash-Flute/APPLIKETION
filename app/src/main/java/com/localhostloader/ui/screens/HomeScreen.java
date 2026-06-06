package com.localhostloader.ui.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.localhostloader.ui.components.ComponentExtensions;
import com.localhostloader.ui.components.ComponentRegistry;
import com.localhostloader.ui.shell.Screen;
import com.localhostloader.ui.shell.ScreenActions;
import com.localhostloader.ui.shell.ScreenId;
import com.localhostloader.ui.shell.UIShellCallback;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeScreen implements Screen {
    private UIShellCallback callback;
    private LinearLayout rootContainer;

    @Override
    public View render(Context context, LayoutInflater inflater, ViewGroup container, Bundle args) {
        rootContainer = new LinearLayout(context);
        rootContainer.setOrientation(LinearLayout.VERTICAL);
        return rootContainer;
    }

    @Override
    public void bind(View rootView, Bundle args) {
        if (rootView.getContext() instanceof UIShellCallback)
            callback = (UIShellCallback) rootView.getContext();
        updateUI("[]");
    }

    @Override
    public void update(Bundle args) {
        if (args == null) return;
        String appsJson = args.getString("apps_json");
        if (appsJson != null) updateUI(appsJson);
    }

    private void updateUI(String appsJson) {
        if (rootContainer == null) return;
        rootContainer.removeAllViews();
        View importBtn = ComponentRegistry.createPrimaryButton(rootContainer.getContext(),
                "Import App", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null)
                            callback.onScreenEvent(ScreenId.HOME, ScreenActions.IMPORT, new Bundle());
                    }
                });
        rootContainer.addView(importBtn);
        try {
            JSONArray arr = new JSONArray(appsJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                final String id = obj.getString("id");
                String name = obj.getString("name");
                String version = obj.optString("version");
                View card = ComponentExtensions.createAppCard(rootContainer.getContext(), name, version,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle b = new Bundle();
                                b.putString("appId", id);
                                if (callback != null)
                                    callback.onScreenEvent(ScreenId.HOME, ScreenActions.LAUNCH, b);
                            }
                        },
                        new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                Bundle b = new Bundle();
                                b.putString("appId", id);
                                if (callback != null)
                                    callback.onScreenEvent(ScreenId.HOME, ScreenActions.UNINSTALL, b);
                                return true;
                            }
                        });
                rootContainer.addView(card);
            }
        } catch (Exception e) { /* ignore */ }
    }

    @Override public void onResume() {}
    @Override public void onPause() {}
    @Override public void onDestroy() { callback = null; rootContainer = null; }
    @Override public boolean onBackPressed() { return false; }
}
