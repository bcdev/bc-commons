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
 * This code is copied from EnterPasswordProcessObserver and enhanced by some code to answer more
 * sqlplus questions if connection could not be established. Otherwise the process would block waiting
 * for more input.
 *
 * This is some sample output from sqlplus when oracle is unreachable (given an 'ENTER' on every
 * prompt sqlplus displays). Similar output is done when the tns name is unknown etc.:
 * <code>
 * SQL*Plus: Release 10.2.0.1.0 - Production on Di Okt 4 10:57:05 2005
 * Copyright (c) 1982, 2005, Oracle.  All rights reserved.
 *
 * Kennwort eingeben:
 * ERROR:
 * ORA-12170: TNS: Connect Timeout aufgetreten
 *
 *
 * Benutzernamen eingeben:
 * ERROR:
 * ORA-12560: TNS: Fehler bei Protokolladapter
 *
 *
 * Benutzernamen eingeben:
 * ERROR:
 * ORA-12560: TNS: Fehler bei Protokolladapter
 *
 *
 * SP2-0157: CONNECT-Versuch zu ORACLE nach 3 Versuchen aufgegeben, SQL*Plus wird verlassen
 * </code>
 * @see EnterPasswordProcessObserver
 */

public class SqlPlusProcessObserver  implements ProcessStreamObserver {
    private OutputStream processOutputStream;
    private String password;
    private boolean hasError = false;

    /**
     *
     * @param processOutputStream
     * @param password String to pass to the external application. Note that terminating "\n" might be needed.
     */
    public SqlPlusProcessObserver(OutputStream processOutputStream, String password) {
        this.processOutputStream = processOutputStream;
        this.password = password;
    }

    public void processWroteToStream(String characterData) {
        boolean hasColon = characterData.indexOf(":")>-1;
        if(password != null && hasColon) {
            try {
                processOutputStream.write((password).getBytes());
                processOutputStream.flush();
                password = null;
            } catch (IOException ignore) {
            }
        } else if(hasColon) {
            try {
                processOutputStream.write("\n".getBytes());
                processOutputStream.flush();
            } catch (IOException ignore) {
            }
        }
        if(characterData.indexOf("ERROR") > -1) {
            hasError = true;
        }
    }

    public boolean hasError() {
        return hasError;
    }

}
