package com.bc.util.string;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {
    private MessageDigest md5Digester;
    private static final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public MD5Encoder() throws NoSuchAlgorithmException {
        md5Digester = MessageDigest.getInstance("MD5");
    }

    /**
     * Encode cleartext password to md5 hash value
     *
     * @param cleartext
     * @return 32 character string, hexadecimal representation
     */

    public String encode(String cleartext) {
        // convert cleartext to md5 digest
        byte[] hashed = md5Digester.digest(cleartext.getBytes());
        return new String(checkAndConvertToString(hashed));
    }

    public void update(byte[] cleartext) {
        md5Digester.update(cleartext);
    }

    public String digest() {
        byte[] hashed = md5Digester.digest();
        return new String(checkAndConvertToString(hashed));
    }

    ////////////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////////

    private char[] checkAndConvertToString(byte[] hashed) {
        if (hashed.length != 16) {
            throw new RuntimeException("MD5 digesting failed");
        }
        // convert binary digest to hex string
        char[] buffer = new char[32];
        for (int i = 0; i < 16; i++) {
            int low = hashed[i] & 0x0f;
            int high = (hashed[i] & 0xf0) >> 4;
            buffer[i * 2] = hex[high];
            buffer[i * 2 + 1] = hex[low];
        }
        return buffer;
    }
}
