package com.bc.process.util;

import junit.framework.TestCase;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

public class ProcessMonitorTest extends TestCase {
    private PipedOutputStream outHandle;
    private PipedOutputStream errHandle;
    private PipedInputStream inHandle;
    private DummyProcess dummyProcess;
    private DummyStreamObserver stdoutObserver;
    private DummyStreamObserver stderrObserver;
    private ProcessMonitor monitor;
    // there seems to be a bug in ProcessMonitor.readCharacterData:
    // Events are fired when '\n' is received. They should rather be fired when no more input
    // is available (via InputStream.available) to enable password prompts to be answered by
    // sending passwords to the process.
    // Also it seems, that a linebuffer is passed to fireCharacterData over and over again.
    // (Though the same linebuffer is sent over and over to fireCharacterData, the method - as
    // side effect - sets the size of linebuffer (StringBuffer) to 0. This way the result is
    // ok, but the code looks strange.
    // todo: eliminate side effect in fireCharacterData.

    static class DummyProcess extends Process {
        private final OutputStream outputStream;
        private final InputStream inputStream;
        private final InputStream errorStream;
        private final int exitValue;

        public DummyProcess(int exitValue, InputStream inputStream, InputStream errorStream, OutputStream outputStream) {
            this.exitValue = exitValue;
            this.inputStream = inputStream;
            this.errorStream = errorStream;
            this.outputStream = outputStream;
        }

        public OutputStream getOutputStream() {
            return outputStream;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public InputStream getErrorStream() {
            return errorStream;
        }

        public int waitFor() throws InterruptedException {
            return 0;
        }

        public int exitValue() {
            return exitValue;
        }

        public void destroy() {
        }
    }

    static class DummyStreamObserver implements ProcessStreamObserver {
        public List strings = new LinkedList();
        public void processWroteToStream(String characterData) {
            strings.add(characterData);
        }
    }

    /**
     * Due to the needs of the Process interface, setup is a bit more complicated...
     * Process's streams are piped in order to be triggered from tests. Data written
     * to outHandle will appear as if sent by the process to stdout, data written to
     * errHandle will appear as if sent by the process to stderr.
     * @throws IOException
     */
    public void setUp() throws IOException {
        PipedInputStream out = new PipedInputStream();
        PipedInputStream err = new PipedInputStream();
        PipedOutputStream in = new PipedOutputStream();
        // handles used in tests to simulate process out/input
        outHandle = new PipedOutputStream(out);
        errHandle = new PipedOutputStream(err);
        inHandle = new PipedInputStream();
        // create monitor and observers.
        stdoutObserver = new DummyStreamObserver();
        stderrObserver = new DummyStreamObserver();
        dummyProcess = new DummyProcess(0, out, err, in);
        monitor = new ProcessMonitor(dummyProcess);
        monitor.setStdoutObserver(stdoutObserver);
        monitor.setStderrObserver(stderrObserver);
        monitor.start();
    }

    protected void tearDown() throws Exception {
        inHandle.close();
        outHandle.close();
        errHandle.close();
    }

    public void testObserverIsTriggeredOnAvailableData() throws IOException, InterruptedException {
        outHandle.write("part1".getBytes());
        outHandle.flush();
        Thread.sleep(25);
        outHandle.write("part2".getBytes());
        outHandle.flush();
        Thread.sleep(25);
        outHandle.write("part3".getBytes());
        outHandle.flush();
        Thread.sleep(25);
        outHandle.write("part4".getBytes());
        outHandle.flush();
        Thread.sleep(25);

        assertEquals(4, stdoutObserver.strings.size());
        assertEquals("part1", stdoutObserver.strings.get(0));
        assertEquals("part2", stdoutObserver.strings.get(1));
        assertEquals("part3", stdoutObserver.strings.get(2));
        assertEquals("part4", stdoutObserver.strings.get(3));
    }

    public void testObserverIsTriggeredOnLineBreak() throws IOException, InterruptedException {
        outHandle.write("line1\nline2\nline3\nline4".getBytes());
        outHandle.flush();
        Thread.sleep(25);

        assertEquals(4, stdoutObserver.strings.size());
        assertEquals("line1\n", stdoutObserver.strings.get(0));
        assertEquals("line2\n", stdoutObserver.strings.get(1));
        assertEquals("line3\n", stdoutObserver.strings.get(2));
        assertEquals("line4", stdoutObserver.strings.get(3));
    }

}
