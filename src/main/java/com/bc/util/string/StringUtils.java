/*
 * $Id: StringUtils.java,v 1.3 2008-01-30 14:07:48 kashif Exp $
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class StringUtils {

    public static final String NEW_LINE = "\n";

    /**
     * Tests whether a given string is possibly a java identifier or not.
     *
     * @param s
     * @return whether java identifier or not
     */
    public static boolean isJavaIdentifier(String s) {
        if (isEmpty(s)) {
            return false;
        }
        if (Character.isJavaIdentifierStart(s.charAt(0))) {
            for (int i = 1; i < s.length(); i++) {
                if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotEmpty(String value) {
        return value != null && value.length() > 0;
    }

    public static boolean isEmpty(String value) {
        return (value == null) || (value.length() == 0);
    }

    public static int compareTo(String s1, String s2) {
        if (s1 != null && s2 != null) {
            return s1.compareTo(s2);
        }
        return 0;
    }

    public static String fileSizeString(long bytes) {
        ensureFormat();
        final StringBuffer result = new StringBuffer(64);
        double value;

        if (bytes >= ONE_GB) {
            value = scaleToThreeDecimals(bytes, ONE_GB);
            result.append(format.format(value));
            result.append(" GB");
        } else if (bytes >= ONE_MB) {
            value = scaleToThreeDecimals(bytes, ONE_MB);
            result.append(format.format(value));
            result.append(" MB");
        } else {
            value = scaleToThreeDecimals(bytes, ONE_KB);
            result.append(format.format(value));
            result.append(" KB");
        }

        return result.toString();
    }

    /**
     * Splits the given text into a list of tokens by using the supplied separators. Empty tokens are created for
     * successive separators, or if the the supplied text starts with or ends with a separator. If the given text string
     * is empty, and empty list is returned, but never <code>null</code>. The tokens added to list will never contain
     * separators.
     *
     * @param text       the text to be splitted into tokens
     * @param separators the characters used to separate the tokens
     * @param trimTokens if true, white space characters are removed from both ends of each token
     * @param tokens     can be null. If not null, all tokens are added to this list and the method it, otherwise a new
     *                   list is created.
     * @return a list of tokens extracted from the given text, never <code>null</code>
     * @throws IllegalArgumentException if one of the arguments was null
     * @see java.util.StringTokenizer
     */
 public static List<String> split(String text, char[] separators, boolean trimTokens, List<String> tokens) {
        if (separators == null || separators.length == 0) {
            throw new IllegalArgumentException("Separators must not be null or empty.");
        }

        if (tokens == null) {
            tokens = new Vector<String>();
        }

        String sepsStr = new String(separators);
        StringTokenizer st = new StringTokenizer(text, sepsStr, true);
        String token;
        String lastToken = null;
        while (st.hasMoreTokens()) {
            try {
                token = st.nextToken();
            } catch (Exception e) {
                break;
            }
            if (isSeparatorToken(token, sepsStr)) {
                // If text starts with a separator or two succesive separators
                // have been seen, add empty string
                if (lastToken == null || isSeparatorToken(lastToken, sepsStr)) {
                    tokens.add("");
                }
            } else {
                if (trimTokens) {
                    token = token.trim();
                }
                tokens.add(token);
            }
            lastToken = token;
        }
        // If text ends with a separator, add empty string
        if (lastToken != null && isSeparatorToken(lastToken, sepsStr)) {
            tokens.add("");
        }

        return tokens;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private static final double ONE_KB = 1024.0;
    private static final double ONE_MB = 1024.0 * 1024.0;
    private static final double ONE_GB = 1024.0 * 1024.0 * 1024.0;
    private static DecimalFormat format;

    private static double scaleToThreeDecimals(double value, double scale) {
        double upScaled = (1000.0 * value / scale);
        int truncated = (int) (upScaled + 0.5);
        return truncated * 0.001;
    }

    private static void ensureFormat() {
        if (format == null) {
            format = new DecimalFormat("0.0");
            format.setMaximumFractionDigits(3);
            format.setDecimalSeparatorAlwaysShown(true);
            final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            format.setDecimalFormatSymbols(symbols);
        }
    }

    public static boolean equalStrings(final String s1, final String s2) {
        if (s1 != null) {
            return s1.equals(s2);
        }
        if (s2 != null) {
            return s2.equals(s1);
        }
        return true;
    }

    public static boolean equalIgnoreCase(final String s1, final String s2) {
        final String su1 = s1 == null ? s1 : s1.toUpperCase();
        final String su2 = s2 == null ? s2 : s2.toUpperCase();
        return equalStrings(su1, su2);
    }
    public static  String truncate(final String target, final int maxSize) {
        return (target.length() > maxSize ? target.substring(0, maxSize) : target);
    }

    private static boolean isSeparatorToken(String token, String separators) {
        return token.length() == 1 && separators.indexOf(token) >= 0;
    }

}
