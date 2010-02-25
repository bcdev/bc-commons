package com.bc.util.string;

public class PatternUtils {

    public static boolean matchesIntRange(String pattern) {
        if (pattern == null) {
            return true;
        }
        pattern = pattern.trim();
        if (pattern.length() == 0) {
            return true;
        }
        return pattern.matches(INT_RANGE_REGEXP);
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private static final String INT_RANGE_REGEXP = "[\\d][\\d\\s;-]*[\\d]";
}
