/*
 * $Id: ProcessExitObserver.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.process.util;

public interface ProcessExitObserver {
    void processExited(int exitValue);

    class DefaultEmptyProcessExitObserver implements ProcessExitObserver {
        public void processExited(int exitValue) {
        }
    }
}
