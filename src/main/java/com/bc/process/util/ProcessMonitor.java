/*
 * $Id: ProcessMonitor.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.process.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is currently being reorganized: Implementation moves from the "old"
 * ProcessObserver to three observers (2*ProcessStreamObserver, ProcessExitObserver)
 * The default implementation uses only the new Observers to signal events. As
 * this class is already used compatibility with ProcessObserver is maintained - the
 * default implementation for the new Observers delegates to ProcessObserver - guessing
 * that all current users of this class create objects of this type initialize it
 * using ProcessObserver.
 */

public class ProcessMonitor {

    /**
     * @deprecated use constructor without ProcessObserver and set one of three possible observers using set*Observer()
     */
    public ProcessMonitor(Process process, ProcessObserver observer) {
        this(process, observer, false);
    }

    /**
     * @deprecated use constructor without ProcessObserver and set one of three possible observers using set*Observer()
     */
    public ProcessMonitor(Process process, ProcessObserver observer, boolean blocking) {
        this.process = process;
        this.blocking = blocking;
        this.processExited = false;
        this.monitorActive = false;
        stdoutObserver = new StdoutObserverAdapter(observer);
        stderrObserver = new StderrObserverAdapter(observer);
        exitObserver = new ExitObserverAdapter(observer);
    }

    public ProcessMonitor(Process process) {
        this.process = process;
        this.blocking = false;
        this.monitorActive = false;
        this.processExited = false;
        // Default behaviour is empty - must be overridden by set*Observer()
        stdoutObserver = new ProcessStreamObserver.DefaultEmptyProcessStreamObserver();
        stderrObserver = new ProcessStreamObserver.DefaultEmptyProcessStreamObserver();
        exitObserver = new ProcessExitObserver.DefaultEmptyProcessExitObserver();
    }

    /**
     * Sets the standard output observer.<br>
     * <code>null</code> is an illegal value.<br>
     * Changing of the standard output observer only allowet before the observing has been started.
     *
     * @param observer
     * @throws IllegalArgumentException if the given observer is <code>null</code>
     * @throws IllegalStateException    if the observer wants to be changed after starting.
     */
    public void setStdoutObserver(ProcessStreamObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer must not be null");
        }
        if (monitorActive) {
            throw new IllegalStateException("The method was called after the Monitoring has beeing started.");
        }
        stdoutObserver = observer;
    }

    /**
     * Sets the standard error observer.<br>
     * <code>null</code> is an illegal value.<br>
     * Changing of the standard error observer only allowet before the observing has been started.
     *
     * @param observer
     * @throws IllegalArgumentException if the given observer is <code>null</code>
     * @throws IllegalStateException    if the observer wants to be changed after starting.
     */
    public void setStderrObserver(ProcessStreamObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer must not be null");
        }
        if (monitorActive) {
            throw new IllegalStateException("The method was called after the Monitoring has beeing started.");
        }
        stderrObserver = observer;
    }

    /**
     * Sets the exit observer.<br>
     * <code>null</code> is an illegal value.<br>
     * Changing of the exit observer only allowet before the observing has been started.
     *
     * @param observer
     * @throws IllegalArgumentException if the given observer is <code>null</code>
     * @throws IllegalStateException    if the observer wants to be changed after starting.
     */
    public void setExitObserver(ProcessExitObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer must not be null");
        }
        if (monitorActive) {
            throw new IllegalStateException("The method was called after the Monitoring has beeing started.");
        }
        exitObserver = observer;
    }

    /**
     * Sets the blocking value. If the blocking is set to <code>true</code> the current thread was
     * waiting until the process has exitted.
     * Setting of the blocking value only allowed before the moitorig was started.
     *
     * @param blocking
     * @throws IllegalStateException if the blocking value wants to be set after starting.
     */
    public void setBlocking(boolean blocking) {
        if (monitorActive) {
            throw new IllegalStateException("The method was called after the Monitoring has beeing started.");
        }
        this.blocking = blocking;
    }

    public void start() {
        if (monitorActive) {
            throw new IllegalStateException("monitor is already active");
        }
        monitorActive = true;

        final Thread sendOut = new Thread("stdoutObserverThread") {
            public void run() {
                observeStream(process.getInputStream(), stdoutObserver);
            }
        };
        sendOut.start();

        final Thread sendErr = new Thread("stderrObserverThread") {
            public void run() {
                observeStream(process.getErrorStream(), stderrObserver);
            }
        };
        sendErr.start();

        if (blocking) {
            waitForExit();
        } else {
            final Thread sendExit = new Thread("exitObserverThread") {
                public void run() {
                    waitForExit();
                }
            };
            sendExit.start();
        }
    }

    public void stop() {
        monitorActive = false;
    }

    public boolean isMonitorActive() {
        return monitorActive;
    }

    public boolean isProcessExited() {
        return processExited;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private static final boolean DEBUG = false;

    private final Process process;

    private ProcessStreamObserver stdoutObserver;
    private ProcessStreamObserver stderrObserver;
    private ProcessExitObserver exitObserver;

    private boolean blocking = false;
    private boolean processExited;
    private boolean monitorActive;

    private void observeStream(InputStream is, ProcessStreamObserver observer) {
        final StringBuffer lineBuffer = new StringBuffer();
        try {
            while (true) {
                if (processExited) {
                    debugReadCharacterData("break 1");
//                    break;
                }
                if (!monitorActive) {
                    debugReadCharacterData("break 2");
                    break;
                }
                if (is.available() == 0) {
                    fireOutput(observer, lineBuffer.toString());
                    lineBuffer.setLength(0);
                }
                int c = is.read();
                if (c >= 0) {
                    lineBuffer.append((char) c);
                    if (c == '\n') {
                        fireOutput(observer, lineBuffer.toString());
                        lineBuffer.setLength(0);
                    }
                } else {
                    debugReadCharacterData("break 3");
                    break;
                }
            }
            is.close();
        } catch (IOException e) {
            debugReadCharacterData("IOException: " + e.getMessage());
        }
        fireOutput(observer, lineBuffer.toString());
    }

    private void fireOutput(ProcessStreamObserver observer, String characterData) {
        if (characterData.length() > 0)
            observer.processWroteToStream(characterData);
    }

    private void debugReadCharacterData(String m) {
        if (DEBUG) {
            System.out.println(process.toString() + ": readCharacterData: " + m);
        }
    }

    private void waitForExit() {
        try {
            process.waitFor();
        } catch (InterruptedException ignore) {
        }
        processExited = true;
        exitObserver.processExited(process.exitValue());
    }


    /**
     * Adapter class to delegate events to ProcessObserver, used internally as standard
     * implementation for stdoutObserver
     */
    @SuppressWarnings("deprecation")
    private class StdoutObserverAdapter implements ProcessStreamObserver {
        private final ProcessObserver observer;

        public StdoutObserverAdapter(ProcessObserver observer) {
            this.observer = observer;
        }

        public void processWroteToStream(String characterData) {
            observer.processWroteToStdout(characterData);
        }
    }

    /**
     * Adapter class to delegate events to ProcessObserver, used internally as standard
     * implementation for stderrObserver
     */
    @SuppressWarnings("deprecation")
    private class StderrObserverAdapter implements ProcessStreamObserver {
        private final ProcessObserver observer;

        public StderrObserverAdapter(ProcessObserver observer) {
            this.observer = observer;
        }

        public void processWroteToStream(String characterData) {
            observer.processWroteToStderr(characterData);
        }
    }

    /**
     * Adapter class to delegate events to ProcessObserver, used internally as standard
     * implementation for exitObserver
     */
    @SuppressWarnings("deprecation")
    private class ExitObserverAdapter implements ProcessExitObserver {
        private final ProcessObserver observer;

        public ExitObserverAdapter(ProcessObserver observer) {
            this.observer = observer;
        }

        public void processExited(int exitValue) {
            observer.processExited(exitValue);
        }
    }
}
