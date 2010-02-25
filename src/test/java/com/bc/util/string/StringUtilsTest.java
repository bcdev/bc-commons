/*
 * $Id: StringUtilsTest.java,v 1.2 2008-03-20 14:11:22 tom Exp $
 *
 * Copyright (C) 2002,2003  by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package com.bc.util.string;


import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

    public void testIsJavaIdentifierBehavesOkOnNullAndEmptyStrings() {
        assertFalse(StringUtils.isJavaIdentifier(""));
        assertFalse(StringUtils.isJavaIdentifier(null));
    }

    public void testIsJavaIdentifier() {
        assertTrue(StringUtils.isJavaIdentifier("int"));
        assertTrue(StringUtils.isJavaIdentifier("String"));
        assertTrue(StringUtils.isJavaIdentifier("Tom"));
        assertTrue(StringUtils.isJavaIdentifier("VeryLongButStillLegalIdentifier"));

        assertFalse(StringUtils.isJavaIdentifier("no identifier"));
        assertFalse(StringUtils.isJavaIdentifier("t�z"));
        assertFalse(StringUtils.isJavaIdentifier("what?"));
        assertFalse(StringUtils.isJavaIdentifier("klhu&/89"));
    }

    public void testIsNotEmpty() {
        assertTrue(StringUtils.isNotEmpty("hallo"));
        assertTrue(StringUtils.isNotEmpty("entwickler"));
        assertTrue(StringUtils.isNotEmpty("  auch was drin"));

        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(""));
    }

    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty(null));

        assertFalse(StringUtils.isEmpty("hups"));
        assertFalse(StringUtils.isEmpty("h#asd+g"));
    }

    public void testConstants() {
        assertEquals("\n", StringUtils.NEW_LINE);
    }

    public void testFormatBytesToFileSizeString() {
        assertEquals("1.0 KB", StringUtils.fileSizeString(1024));
        assertEquals("3.22 KB", StringUtils.fileSizeString((int)(3.22 * 1024.0)));

        assertEquals("1.0 MB", StringUtils.fileSizeString(1024 * 1024));
        assertEquals("2.0 MB", StringUtils.fileSizeString(2 * 1024 * 1024));
        assertEquals("48.25 MB", StringUtils.fileSizeString((int)(48.25 * 1024 * 1024)));

        assertEquals("2.0 GB", StringUtils.fileSizeString(2L * 1024L * 1024L * 1024L));
        assertEquals("12.0 GB", StringUtils.fileSizeString(12L * 1024L * 1024L * 1024L));
    }

    public void testEquals() {
        assertTrue(StringUtils.equalStrings(null, null));
        assertTrue(StringUtils.equalStrings("", ""));
        assertTrue(StringUtils.equalStrings("a", "a"));

        assertFalse(StringUtils.equalStrings(" ", ""));
        assertFalse(StringUtils.equalStrings("", " "));
        assertFalse(StringUtils.equalStrings("", null));
        assertFalse(StringUtils.equalStrings(null, ""));
        assertFalse(StringUtils.equalStrings("a", "A"));
    }

    public void testEqualsIgnoreCase() {
        assertTrue(StringUtils.equalIgnoreCase(null, null));
        assertTrue(StringUtils.equalIgnoreCase("", ""));
        assertTrue(StringUtils.equalIgnoreCase("a", "a"));
        assertTrue(StringUtils.equalIgnoreCase("a", "A"));

        assertFalse(StringUtils.equalIgnoreCase(" ", ""));
        assertFalse(StringUtils.equalIgnoreCase("", " "));
        assertFalse(StringUtils.equalIgnoreCase("", null));
        assertFalse(StringUtils.equalIgnoreCase(null, ""));
    }

    public void testCompareTo() {
        assertEquals(-2, StringUtils.compareTo("blaber", "blaberis"));
        assertEquals(2, StringUtils.compareTo("blaberis", "blaber"));
        assertEquals(0, StringUtils.compareTo("blaber", "blaber"));
        assertEquals(-6, StringUtils.compareTo("", "blaber"));
        assertEquals(6, StringUtils.compareTo("blaber", ""));

        assertEquals(0, StringUtils.compareTo("huhu", null));
        assertEquals(0, StringUtils.compareTo(null, "huhu"));
        assertEquals(0, StringUtils.compareTo("", ""));
        assertEquals(0, StringUtils.compareTo(null, ""));
        assertEquals(0, StringUtils.compareTo("", null));
        assertEquals(0, StringUtils.compareTo(null, null));
    }
}
