/*
 * $Id: TimestampIdGeneratorTest.java,v 1.1 2007-02-27 12:45:31 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import junit.framework.TestCase;

public class TimestampIdGeneratorTest extends TestCase {

    public TimestampIdGeneratorTest(String s) {
        super(s);
    }

    public void testMultipleIdGeneration() {
        assertFalse(
                com.bc.util.sql.TimestampIdGenerator.getNextLong() == com.bc.util.sql.TimestampIdGenerator.getNextLong());
        assertFalse(
                com.bc.util.sql.TimestampIdGenerator.getNextLong() == com.bc.util.sql.TimestampIdGenerator.getNextLong());
        assertFalse(
                com.bc.util.sql.TimestampIdGenerator.getNextLong() == com.bc.util.sql.TimestampIdGenerator.getNextLong());
        assertFalse(
                com.bc.util.sql.TimestampIdGenerator.getNextLong() == com.bc.util.sql.TimestampIdGenerator.getNextLong());
        assertFalse(
                com.bc.util.sql.TimestampIdGenerator.getNextLong() == com.bc.util.sql.TimestampIdGenerator.getNextLong());
        assertFalse(
                com.bc.util.sql.TimestampIdGenerator.getNextLong() == com.bc.util.sql.TimestampIdGenerator.getNextLong());
        assertFalse(
                com.bc.util.sql.TimestampIdGenerator.getNextLong() == com.bc.util.sql.TimestampIdGenerator.getNextLong());
    }
}

