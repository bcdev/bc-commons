package com.bc.util.math;

import junit.framework.TestCase;

public class MathUtilsTest extends TestCase {

    public void testClipToThreeDigits() {
        final double input_1 = 123.456789;
        final double expected_1 = 123.456;
        final double input_2 = 0.45346346;
        final double expected_2 = 0.453;
        final double input_3 = -78.993453;
        final double expected_3 = -78.993;

        double result = MathUtils.clipToThreeDigits(input_1);
        assertEquals(expected_1, result, 1e-8);

        result = MathUtils.clipToThreeDigits(input_2);
        assertEquals(expected_2, result, 1e-8);

        result = MathUtils.clipToThreeDigits(input_3);
        assertEquals(expected_3, result, 1e-8);
    }

    public void testIntToByteArray() {
        byte[] result = MathUtils.intToByteArray(4);
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);
        assertEquals(0, result[2]);
        assertEquals(4, result[3]);

        result = MathUtils.intToByteArray(128);
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);
        assertEquals(0, result[2]);
        assertEquals(-128, result[3]);

        result = MathUtils.intToByteArray(196734);
        assertEquals(0, result[0]);
        assertEquals(3, result[1]);
        assertEquals(0, result[2]);
        assertEquals(126, result[3]);

        result = MathUtils.intToByteArray(23196734);
        assertEquals(1, result[0]);
        assertEquals(97, result[1]);
        assertEquals(-12, result[2]);
        assertEquals(62, result[3]);
    }

    public void testByteArrayToInt() {
        int result = MathUtils.byteArrayToInt(new byte[] {0, 0, 2, 2});
        assertEquals(514, result);

        result = MathUtils.byteArrayToInt(new byte[] {0, 1, 0, 127});
        assertEquals(65663, result);

        result = MathUtils.byteArrayToInt(new byte[] {4, 0, -18, 127});
        assertEquals(67169919, result);
    }

    public void testByteArrayToInt_exceptionWhenArrayTooShort() {
        try {
            MathUtils.byteArrayToInt(new byte[]{1, 2, 3});
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testByteArrayIntConversionIsReversible() {
        final byte[] start = new byte[] {1, 34, -124, 19};

        final int intResult = MathUtils.byteArrayToInt(start);
        assertEquals(19039251, intResult);

        final byte[] byteResult = MathUtils.intToByteArray(intResult);
        assertEquals(4, byteResult.length);
        for (int i = 0; i < byteResult.length; i++) {
            assertEquals(start[i], byteResult[i]);            
        }
    }

    public void testByteArrayToIntArray() {
        byte[] input = new byte[] {1, 34, -124, 19, 1, 34, -124, 20};
        int[] result = MathUtils.byteArrayToIntArray(input);
        assertEquals(2, result.length);
        assertEquals(19039251, result[0]);
        assertEquals(19039252, result[1]);

        input = new byte[] {0, 0, 2, 2};
        result = MathUtils.byteArrayToIntArray(input);
        assertEquals(1, result.length);
        assertEquals(514, result[0]);

        input = new byte[] {0, 0, 2, 2, 0, 1, 2, 2};
        result = MathUtils.byteArrayToIntArray(input);
        assertEquals(2, result.length);
        assertEquals(514, result[0]);
        assertEquals(66050, result[1]);

        input = new byte[] {0, 0, 2, 2, 0, 1, 2, 2, 0, 0, 2, 0};
        result = MathUtils.byteArrayToIntArray(input);
        assertEquals(3, result.length);
        assertEquals(514, result[0]);
        assertEquals(66050, result[1]);
        assertEquals(512, result[2]);
    }

     public void testByteArrayToIntArray_inputArrayTooShort() {
         try {
             MathUtils.byteArrayToIntArray(new byte[] {1,3});
             fail("IllegalArgumentException expected");
         } catch (IllegalArgumentException expected) {
         }
     }
}
