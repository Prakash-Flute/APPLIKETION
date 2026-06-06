package com.localhostloader.ui.shell;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface Screen {
    View render(Context context, LayoutInflater inflater, ViewGroup container, Bundle args);
    void bind(View rootView, Bundle args);
    void onResume();
    void onPause();
    void onDestroy();
    boolean onBackPressed();
    void update(Bundle args);
}
