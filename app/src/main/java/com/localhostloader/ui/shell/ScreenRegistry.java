package com.localhostloader.ui.shell;

import java.util.HashMap;
import java.util.Map;

public final class ScreenRegistry {
    private static final Map<ScreenId, ScreenFactory> registry = new HashMap<>();

    public static void register(ScreenId id, ScreenFactory factory) {
        registry.put(id, factory);
    }

    public static Screen create(ScreenId id) {
        ScreenFactory factory = registry.get(id);
        if (factory != null) return factory.create();
        return null;
    }

    private ScreenRegistry() {}
}
