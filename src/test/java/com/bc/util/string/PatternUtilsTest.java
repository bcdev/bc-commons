package com.bc.util.string;

import junit.framework.TestCase;

public class PatternUtilsTest extends TestCase {

    public void testCheckIntRangePattern() {
        assertFalse(PatternUtils.matchesIntRange(";887"));
        assertFalse(PatternUtils.matchesIntRange("77;"));
        assertFalse(PatternUtils.matchesIntRange("uuzt"));
        assertFalse(PatternUtils.matchesIntRange("u56uzt"));
        assertFalse(PatternUtils.matchesIntRange("8z9"));
        assertFalse(PatternUtils.matchesIntRange("8z9"));
        assertFalse(PatternUtils.matchesIntRange("-7"));
        assertFalse(PatternUtils.matchesIntRange("123-"));
        assertFalse(PatternUtils.matchesIntRange("123,89"));

        assertTrue(PatternUtils.matchesIntRange(""));
        assertTrue(PatternUtils.matchesIntRange(" "));
        assertTrue(PatternUtils.matchesIntRange(null));
        assertTrue(PatternUtils.matchesIntRange("6677"));
        assertTrue(PatternUtils.matchesIntRange(" 6677"));
        assertTrue(PatternUtils.matchesIntRange("6677 "));
        assertTrue(PatternUtils.matchesIntRange("6677;998"));
        assertTrue(PatternUtils.matchesIntRange("5-19"));
        assertTrue(PatternUtils.matchesIntRange("5-19;87"));
        assertTrue(PatternUtils.matchesIntRange("5-19;45-98"));
        assertTrue(PatternUtils.matchesIntRange("6677-6698;998; 78; 4-9"));
    }
}
