package com.bc.util.math;

public class MathUtils {

    public static double clipToThreeDigits(double input) {
        return ((long) (input * 1000)) * 0.001;
    }

    /**
     * Converts the integer value to a byte array. Size of the result is always 4.
     * inverse operation to byteArrayToInt()
     *
     * @param value the integer value to be converted
     * @return the byte array
     */
    public static byte[] intToByteArray(int value) {
        final byte[] result = new byte[4];

        for (int i = 0; i < 4; ++i) {
            int shift = i << 3;
            result[3 - i] = (byte) ((value & (0xff << shift)) >>> shift);
        }

        return result;
    }

    /**
     * Converts a byte[] of legth 4 to an integer value. If the byte array contains more data,
     * only the first 4 bytes are used.
     * Inverse operation to intToByteArray()
     *
     * @param bytes the bytes to be converted
     * @return the result
     * @throws IllegalArgumentException when the input array is shorter than 4 bytes
     */
    public static int byteArrayToInt(byte[] bytes) {
        if (bytes.length < 4) {
            throw new IllegalArgumentException("input array too short");
        }

        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result |= (bytes[3 - i] & 0xff) << (i << 3);
        }

        return result;
    }

    public static int[] byteArrayToIntArray(byte[] bytes) {
        if (bytes.length < 4) {
            throw new IllegalArgumentException("input array too short");
        }
        
        final int resultSize = bytes.length / 4;
        final int[] result = new int[resultSize];

        for (int i = 0; i < result.length; i++) {
            result[i] = 0;
            final int offset = 4 * i;
            for (int k = 0; k < 4; ++k) {
                result[i] |= (bytes[offset + (3 - k)] & 0xff) << (k << 3);
            }
        }
        
        return result;
    }
}
