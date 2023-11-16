/*
 * $Id: ProcessStreamObserver.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.process.util;

public interface ProcessStreamObserver {
    void processWroteToStream(String characterData);

    class DefaultEmptyProcessStreamObserver implements ProcessStreamObserver {
        public void processWroteToStream(String characterData) {
        }
    }
}
