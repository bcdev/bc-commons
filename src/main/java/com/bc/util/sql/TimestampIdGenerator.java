/*
 * $Id: TimestampIdGenerator.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

/**
 * Provides a thread-safe generator for unique identifiers which can be used as primary keys.
 */
public class TimestampIdGenerator {

    public static String getPrefix() {
        if (prefix == null) {
            prefix = ("" + Math.random() + "0000000000000").substring(2, 12);
        }
        return prefix;
    }

    public static long getNextLong() {
        synchronized (TimestampIdGenerator.class) {
            long result = System.currentTimeMillis();
            // he/** - preserve a real unique Id!!! An Id lastId +1 can be already in use.
            // comment ok: with TimestampIdGenerator the only case would be two processes writing
            // to the database at the same time.
            if (result <= lastId) {
                result = lastId + 1;
            }
            lastId = result;
            return result;
        }
    }

    public static String getNextString() {
        return getPrefix() + Long.toHexString(getNextLong());
    }

    ////////////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////////

    private static long lastId = System.currentTimeMillis();
    private static String prefix = null;

}
