/*
 * Created at 24.03.2004 09:45:29
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.geom;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class TFWTest extends TestCase {

    public TFWTest(String s) {
        super(s);
    }

    public void testLoadWithValidCoefficient() throws IOException,
                                                      ParseException {
        StringReader reader = new StringReader("12.3 2.34 34.5\n" +
                                               "0.45 -5.6 6.78");
        final com.bc.util.geom.TFW tfw = com.bc.util.geom.TFW.load(reader);
        assertEquals(12.3, tfw.getScaleX(), 1e-10);
        assertEquals(2.34, tfw.getShearY(), 1e-10);
        assertEquals(34.5, tfw.getShearX(), 1e-10);
        assertEquals(0.45, tfw.getScaleY(), 1e-10);
        assertEquals(-5.6, tfw.getTranslateX(), 1e-10);
        assertEquals(6.78, tfw.getTranslateY(), 1e-10);
    }

    public void testLoadWithMissingCoefficient() throws IOException {
        try {
            StringReader reader = new StringReader("12.3 2.34 34.5\n" +
                                                   "0.456");
            com.bc.util.geom.TFW.load(reader);
            fail();
        } catch (ParseException expected) {
        }
    }

    public void testLoadWithOneMoreCoefficient() throws IOException {
        try {
            StringReader reader = new StringReader("12.3 2.34 34.5\n" +
                                                   "0.45 -5.6 6.78 7.89");
            com.bc.util.geom.TFW.load(reader);
            fail();
        } catch (ParseException expected) {
        }
    }

    public void testLoadWithInvalidCoefficient() throws IOException {
        try {
            StringReader reader = new StringReader("12.3 2.34 abc\n" +
                                                   "0.456 5.6 6.78");
            com.bc.util.geom.TFW.load(reader);
            fail();
        } catch (ParseException expected) {
        }
    }
}

