package com.localhostloader.ui.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.localhostloader.ui.shell.Screen;

public class ImportScreen implements Screen {
    @Override
    public View render(Context context, LayoutInflater inflater, ViewGroup container, Bundle args) {
        TextView tv = new TextView(context);
        tv.setText("Import Screen");
        return tv;
    }
    @Override public void bind(View rootView, Bundle args) {}
    @Override public void onResume() {}
    @Override public void onPause() {}
    @Override public void onDestroy() {}
    @Override public boolean onBackPressed() { return false; }
    @Override public void update(Bundle args) {}
}
