package com.localhostloader.runtime.process;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ProcessManager {
    private static final ProcessManager INSTANCE = new ProcessManager();
    private final Map<String, ProcessRecord> processes = new ConcurrentHashMap<>();

    private ProcessManager() {}
    public static ProcessManager getInstance() { return INSTANCE; }

    public ProcessRecord startProcess(String id, String[] command, File workingDir, Map<String,String> env) {
        ProcessBuilder pb = new ProcessBuilder(command);
        if (workingDir != null) pb.directory(workingDir);
        if (env != null) pb.environment().putAll(env);
        try {
            Process process = pb.start();
            ProcessRecord record = new ProcessRecord(id, process, String.join(" ", command), System.currentTimeMillis());
            processes.put(id, record);
            return record;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean killProcess(String id) {
        ProcessRecord record = processes.get(id);
        if (record != null) {
            record.getProcess().destroy();
            processes.remove(id);
            return true;
        }
        return false;
    }

    public Map<String, ProcessRecord> getAllProcesses() {
        return new ConcurrentHashMap<>(processes);
    }
}
