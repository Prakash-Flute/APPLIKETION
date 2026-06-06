package com.localhostloader.runtime.process;

public final class ProcessRecord {
    private final String id;
    private final Process process;
    private final String command;
    private final long startTime;
    private volatile boolean running;

    public ProcessRecord(String id, Process process, String command, long startTime) {
        this.id = id;
        this.process = process;
        this.command = command;
        this.startTime = startTime;
        this.running = true;
    }

    public String getId() { return id; }
    public Process getProcess() { return process; }
    public String getCommand() { return command; }
    public long getStartTime() { return startTime; }
    public boolean isRunning() { return running && process.isAlive(); }
    public void setRunning(boolean running) { this.running = running; }
}
