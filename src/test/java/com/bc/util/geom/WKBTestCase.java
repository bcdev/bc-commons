/*
 * $Id: WKBTestCase.java,v 1.1 2007-02-27 12:45:31 eugen Exp $
 *
 * Copyright (C) 2006 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.bc.util.geom;

import junit.framework.TestCase;

/**
 * Created by marcoz.
 * 
 * @author marcoz
 * @version $Revision: 1.1 $ $Date: 2007-02-27 12:45:31 $
 */
public abstract class WKBTestCase extends TestCase {

	protected static String bytesToHex(byte[] bytes) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			buf.append(toHexDigit((b >> 4) & 0x0F));
			buf.append(toHexDigit(b & 0x0F));
		}
		return buf.toString();
	}

	private static char toHexDigit(int n) {
		if (n < 0 || n > 15)
			throw new IllegalArgumentException("Nibble value out of range: "
					+ n);
		if (n <= 9)
			return (char) ('0' + n);
		return (char) ('A' + (n - 10));
	}

	/**
	 * Converts a hexadecimal string to a byte array.
	 * 
	 * @param hex
	 *            a string containing hex digits
	 */
	protected static byte[] hexToBytes(String hex) {
		int byteLen = hex.length() / 2;
		byte[] bytes = new byte[byteLen];

		for (int i = 0; i < hex.length() / 2; i++) {
			int i2 = 2 * i;
			if (i2 + 1 > hex.length())
				throw new IllegalArgumentException("Hex string has odd length");

			int nib1 = hexToInt(hex.charAt(i2));
			int nib0 = hexToInt(hex.charAt(i2 + 1));
			byte b = (byte) ((nib1 << 4) + (byte) nib0);
			bytes[i] = b;
		}
		return bytes;
	}

	private static int hexToInt(char hex) {
		int nib = Character.digit(hex, 16);
		if (nib < 0)
			throw new IllegalArgumentException("Invalid hex digit");
		return nib;
	}

	/**
	 * Asserts that two byte arrays are equal. If they are not, an
	 * AssertionError is thrown with the given message.
	 */
	protected static void assertEquals(String message, byte[] expecteds,
			byte[] actuals) {
		if (expecteds == actuals)
			return;
		String header = message == null ? "" : message + ": ";
		if (expecteds == null)
			fail(header + "expected array was null");
		if (actuals == null)
			fail(header + "actual array was null");
		if (actuals.length != expecteds.length)
			fail(header + "array lengths differed, expected.length="
					+ expecteds.length + " actual.length=" + actuals.length);

		for (int i = 0; i < expecteds.length; i++) {
			byte o1 = expecteds[i];
			byte o2 = actuals[i];
			assertEquals(header + "arrays differ " + "(expected:<"
					+ bytesToHex(expecteds) + "> but was:<"
					+ bytesToHex(actuals) + ">) "
					+ "first differed at element [" + i + "];", o1, o2);
		}
	}

	/**
	 * Asserts that two byte arrays are equal. If they are not, an
	 * AssertionError is thrown.
	 */
	protected static void assertEquals(byte[] expecteds, byte[] actuals) {
		assertEquals(null, expecteds, actuals);
	}

}
