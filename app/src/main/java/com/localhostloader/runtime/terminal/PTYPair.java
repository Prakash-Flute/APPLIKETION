package com.localhostloader.runtime.terminal;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public final class PTYPair {
    private final PipedInputStream masterIn;
    private final PipedOutputStream masterOut;
    private final PipedInputStream slaveIn;
    private final PipedOutputStream slaveOut;

    public PTYPair() throws Exception {
        masterIn = new PipedInputStream();
        masterOut = new PipedOutputStream();
        slaveIn = new PipedInputStream(masterOut);
        slaveOut = new PipedOutputStream(masterIn);
    }

    public InputStream getMasterInput() { return masterIn; }
    public OutputStream getMasterOutput() { return masterOut; }
    public InputStream getSlaveInput() { return slaveIn; }
    public OutputStream getSlaveOutput() { return slaveOut; }

    public void close() {
        try { masterIn.close(); } catch (Exception e) {}
        try { masterOut.close(); } catch (Exception e) {}
        try { slaveIn.close(); } catch (Exception e) {}
        try { slaveOut.close(); } catch (Exception e) {}
    }
}
