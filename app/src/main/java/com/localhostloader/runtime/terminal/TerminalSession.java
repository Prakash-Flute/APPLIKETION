package com.localhostloader.runtime.terminal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TerminalSession {
    private Process process;
    private Writer inputWriter;
    private final TerminalBuffer buffer = new TerminalBuffer();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final StringBuilder output = new StringBuilder();

    public void start(String shell) {
        try {
            ProcessBuilder pb = new ProcessBuilder(shell);
            pb.redirectErrorStream(true);
            process = pb.start();
            inputWriter = new OutputStreamWriter(process.getOutputStream());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        char[] buf = new char[1024];
                        int len;
                        while ((len = reader.read(buf)) != -1) {
                            synchronized (output) {
                                output.append(buf, 0, len);
                            }
                            buffer.append(new String(buf, 0, len));
                        }
                    } catch (Exception e) {
                        // Ignored
                    }
                }
            });
        } catch (Exception e) {
            // Ignored
        }
    }

    public void write(String text) {
        try {
            if (inputWriter != null) {
                inputWriter.write(text);
                inputWriter.flush();
            }
        } catch (Exception e) {
            // Ignored
        }
    }

    public String readOutput() {
        synchronized (output) {
            String result = output.toString();
            output.setLength(0);
            return result;
        }
    }

    public TerminalBuffer getBuffer() {
        return buffer;
    }

    public void destroy() {
        if (process != null) {
            process.destroy();
        }
        executor.shutdownNow();
    }
}
