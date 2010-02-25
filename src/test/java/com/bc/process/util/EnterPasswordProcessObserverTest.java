package com.bc.process.util;

import junit.framework.TestCase;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.io.IOException;

public class EnterPasswordProcessObserverTest extends TestCase {
    private EnterPasswordProcessObserver observer;
    private PipedInputStream handleOut;

    protected void setUp() throws Exception {
        PipedOutputStream out = new PipedOutputStream();
        handleOut = new PipedInputStream(out);

        observer = new EnterPasswordProcessObserver(out, "blabla");
    }

    public void testAnswersPromptAtColon() throws IOException {
        observer.processWroteToStream("text ohne doppelpunkt");
        assertEquals(0, handleOut.available());
        observer.processWroteToStream("\nneue zeile ohne doppelpunkt\n");
        assertEquals(0, handleOut.available());
        observer.processWroteToStream("prompt: ");
        assertEquals("blabla".length(), handleOut.available());
        byte[] buffer = new byte[100];
        int chars = handleOut.read(buffer);
        assertEquals("blabla", new String(buffer,0, chars));
    }

    public void testAnswersOnlyOnce() throws IOException {
        observer.processWroteToStream("prompt: ");
        assertEquals("blabla".length(), handleOut.available());
        byte[] buffer = new byte[100];
        int chars = handleOut.read(buffer);
        assertEquals("blabla", new String(buffer,0, chars));

        observer.processWroteToStream("second prompt: ");
        assertEquals(0, handleOut.available());

        observer.processWroteToStream("\nthird prompt: \n");
        assertEquals(0, handleOut.available());
    }
}
