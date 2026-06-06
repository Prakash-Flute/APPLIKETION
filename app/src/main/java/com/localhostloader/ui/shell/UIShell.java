package com.localhostloader.ui.shell;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import java.util.Stack;

public final class UIShell {
    private static Activity activity;
    private static UIShellCallback callback;
    private static FrameLayout container;
    private static final Stack<ScreenEntry> backStack = new Stack<>();
    private static DialogManager dialogManager;
    private static LoadingManager loadingManager;
    private static LayoutInflater inflater;

    private static class ScreenEntry {
        ScreenId id;
        Screen screen;
        View view;
        Bundle args;

        ScreenEntry(ScreenId id, Screen screen, View view, Bundle args) {
            this.id = id;
            this.screen = screen;
            this.view = view;
            this.args = args;
        }
    }

    public static void initialize(Activity act, UIShellCallback cb) {
        activity = act;
        callback = cb;
        inflater = LayoutInflater.from(act);
        container = new FrameLayout(act);
        act.setContentView(container);
        dialogManager = new DialogManager(act);
        loadingManager = new LoadingManager(act);
    }

    public static void showScreen(ScreenId id, Bundle args) {
        Screen screen = ScreenRegistry.create(id);
        if (screen == null) return;

        View view = screen.render(activity, inflater, container, args);
        screen.bind(view, args);

        if (!backStack.isEmpty()) {
            ScreenEntry current = backStack.peek();
            current.screen.onPause();
            container.removeView(current.view);
            if (callback != null) callback.onScreenTransition(current.id, id);
        }

        container.addView(view);
        backStack.push(new ScreenEntry(id, screen, view, args));
        screen.onResume();
    }

    public static void updateScreen(ScreenId id, Bundle args) {
        for (ScreenEntry entry : backStack) {
            if (entry.id == id) {
                entry.screen.update(args);
                break;
            }
        }
    }

    public static boolean onBackPressed() {
        if (backStack.isEmpty()) return false;
        ScreenEntry current = backStack.peek();
        if (current.screen.onBackPressed()) return true;
        backStack.pop();
        current.screen.onPause();
        current.screen.onDestroy();
        container.removeView(current.view);

        if (!backStack.isEmpty()) {
            ScreenEntry prev = backStack.peek();
            container.addView(prev.view);
            prev.screen.onResume();
            if (callback != null) callback.onScreenTransition(current.id, prev.id);
        }
        return !backStack.isEmpty();
    }

    public static DialogManager getDialogManager() {
        return dialogManager;
    }

    public static LoadingManager getLoadingManager() {
        return loadingManager;
    }

    public static void destroy() {
        while (!backStack.isEmpty()) {
            ScreenEntry entry = backStack.pop();
            entry.screen.onPause();
            entry.screen.onDestroy();
        }
        container.removeAllViews();
    }
}
