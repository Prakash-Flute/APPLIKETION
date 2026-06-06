package com.localhostloader.runtime.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ShellRuntime {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void execute(final String command, final File workingDir, final OutputCallback stdout, final OutputCallback stderr, final ExitCallback exit) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
                    if (workingDir != null) pb.directory(workingDir);
                    Process p = pb.start();

                    BufferedReader outReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    String line;
                    while ((line = outReader.readLine()) != null) {
                        if (stdout != null) stdout.onOutput(line);
                    }
                    while ((line = errReader.readLine()) != null) {
                        if (stderr != null) stderr.onOutput(line);
                    }

                    int code = p.waitFor();
                    if (exit != null) exit.onExit(code);
                } catch (Exception e) {
                    if (exit != null) exit.onExit(-1);
                }
            }
        });
    }

    public interface OutputCallback {
        void onOutput(String line);
    }

    public interface ExitCallback {
        void onExit(int code);
    }
}
