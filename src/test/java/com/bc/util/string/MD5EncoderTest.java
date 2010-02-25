package com.bc.util.string;

import junit.framework.TestCase;

import java.security.NoSuchAlgorithmException;

public class MD5EncoderTest extends TestCase {

    private MD5Encoder encoder;

    public void testConversion() throws NoSuchAlgorithmException {
        final String hallo = encoder.encode("hallo");
        final String welt = encoder.encode("welt");
        assertEquals(32, hallo.length());
        assertEquals(32, welt.length());
        assertEquals("598d4c200461b81522a3328565c25f7c", hallo);
        assertEquals("99329e3cb25a82f1506ade731612a715", welt);
        assertFalse(hallo.equals(welt));
    }

    public void testUpdateAndDigest() {
        final String hallo_full = encoder.encode("hallo, du alter sack");

        encoder.update("hallo, ".getBytes());
        encoder.update("du alter ".getBytes());
        encoder.update("sack".getBytes());
        final String hallo_in_parts = encoder.digest();

        assertEquals(hallo_full, hallo_in_parts);
    }

    ////////////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        encoder = new MD5Encoder();
    }
}

