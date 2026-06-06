package com.localhostloader.ui.shell;

import android.os.Bundle;

public interface UIShellCallback {
    void onScreenEvent(ScreenId source, String action, Bundle data);
    void onScreenTransition(ScreenId from, ScreenId to);
}
