package com.bc.util.encoder;

import junit.framework.TestCase;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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

    private static final byte testBinaryFileContent[] = new byte[]{-85, 35, -123, 100, -105, 30, 63, 85, -125, -3, -118, -80, 69, 8, -105, -68, -7, -83, -21, -126, 124, -36, -57, 45, -111, 6, 87, -91, 81, -6, 13, -41, -115, 117, 8, 1, -113, -53, -85, 33, -11, -109, -26, 107, -38, -18, -30, -127, 33, 24};

//    //this is just here for reference in case the testBinaryFile field needs to be regenerated
//    private static void printRandomBinary() {
//        SecureRandom r = new SecureRandom();
//        for(int i=0; i<50; i++) {
//            int rand = r.nextInt(255);
//            System.out.print(rand-127 + ", ");
//        }
//    }

    public void testUpdateAndDigest_binary() {
        encoder.update(testBinaryFileContent);
        String hashResult = encoder.digest();

        assertEquals("d57c03c25ea4bf4ad45870a2837731c3", hashResult);
    }

    public void testUpdateAndDigest_binary_in_parts() {
        byte[][] brokenUp = new byte[][]{
                Arrays.copyOfRange(testBinaryFileContent, 0, 8),
                Arrays.copyOfRange(testBinaryFileContent, 8, 24),
                Arrays.copyOfRange(testBinaryFileContent, 24, 30),
                Arrays.copyOfRange(testBinaryFileContent, 30, 31),
                Arrays.copyOfRange(testBinaryFileContent, 31, 39),
                Arrays.copyOfRange(testBinaryFileContent, 39, 47),
                Arrays.copyOfRange(testBinaryFileContent, 47, 50)
        };

        for (byte[] bytes : brokenUp) {
            encoder.update(bytes);
        }
        String hashResult = encoder.digest();

        assertEquals("d57c03c25ea4bf4ad45870a2837731c3", hashResult);
    }

    public void testUpdateAndDigest_binary_in_parts_using_offset() {
        encoder.update(testBinaryFileContent, 0, 8);
        encoder.update(testBinaryFileContent, 8, 24 - 8);
        encoder.update(testBinaryFileContent, 24, 30 - 24);
        encoder.update(testBinaryFileContent, 30, 31 - 30);
        encoder.update(testBinaryFileContent, 31, 39 - 31);
        encoder.update(testBinaryFileContent, 39, 47 - 39);
        encoder.update(testBinaryFileContent, 47, 50 - 47);

        String hashResult = encoder.digest();

        assertEquals("d57c03c25ea4bf4ad45870a2837731c3", hashResult);
    }

    public void testEncodeInputStream() throws Exception {
        InputStream bytesIn = new ByteArrayInputStream(testBinaryFileContent);

        String hashResult = encoder.encode(bytesIn);
        assertEquals("d57c03c25ea4bf4ad45870a2837731c3", hashResult);
    }

    public void testEncodeFile() throws Exception {
        final File dummyFile = new File("dummyFile") {
            @Override
            public String getPath() {
                return super.getPath();
            }
        };
        final File[] incomingFile = new File[]{null};
        final int[] methodCalls = new int[]{0};

        final MD5Encoder encoder = new MD5Encoder() {
            @Override
            protected InputStream getInputStreamFromFile(File inputFile) throws FileNotFoundException {
                incomingFile[0] = inputFile;
                methodCalls[0]++;
                return new ByteArrayInputStream(testBinaryFileContent);
            }
        };

        String hashResult = encoder.encode(dummyFile);

        assertEquals("d57c03c25ea4bf4ad45870a2837731c3", hashResult);
        assertSame(dummyFile, incomingFile[0]);
        assertEquals(1, methodCalls[0]);
    }

    ////////////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        encoder = new MD5Encoder();
    }
}

