package com.bc.process.util;

import java.io.OutputStream;
import java.io.IOException;

/**
 * This processObserver waits for the first ':' on a process's output stream and interprets this as a
 * password prompt. The prompt is answered by providing the password to the given outputStream.
 * Note that Input- and OutputStream are non-intuitive in java.lang.Process - processOutputStream
 * (from the perspective of this application and java.lang.Process) translates to the
 * process's stdin stream.
 * This Observer is used (for example) when external processes (like oracle's sqlplus) need to
 * know a password in order to connect to a database, but the password must not be given on the
 * commandline. Note that the complete commandline might be visible to all users of a system
 * (on unix via ps), therefor no password should be given on the commandline but only via stdin.
 *
 * For use with Oracle sqlplus consider using SqlPlusProcessObserver
 * @see SqlPlusProcessObserver
 */

public class EnterPasswordProcessObserver  implements ProcessStreamObserver {
    private final OutputStream processOutputStream;
    private String password;

    /**
     *
     * @param processOutputStream
     * @param password String to pass to the external application. Note that terminating "\n" might be needed.
     */
    public EnterPasswordProcessObserver(OutputStream processOutputStream, String password) {
        this.processOutputStream = processOutputStream;
        this.password = password;
    }

    public void processWroteToStream(String characterData) {
        if(password != null && characterData.indexOf(":")>0) {
            try {
                processOutputStream.write((password).getBytes());
                processOutputStream.flush();
                password = null;
            } catch (IOException ignore) {
            }
        }
    }
}
