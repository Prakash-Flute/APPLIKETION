package com.localhostloader.runtime.terminal;

import java.util.ArrayList;
import java.util.List;

public final class TerminalBuffer {
    private final List<String> lines = new ArrayList<>();
    private final int maxLines = 1000;

    public synchronized void append(String text) {
        String[] parts = text.split("\n", -1);
        for (String part : parts) {
            lines.add(part);
        }
        while (lines.size() > maxLines) {
            lines.remove(0);
        }
    }

    public synchronized List<String> getLines() {
        return new ArrayList<>(lines);
    }

    public synchronized void clear() {
        lines.clear();
    }
}
