/*
 * $Id: PropertyParser.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
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
package com.bc.util.prop;

import com.bc.util.geom.Geometry;
import com.bc.util.geom.GeometryParser;

import java.awt.Color;
import java.io.File;
import java.text.ParseException;
import java.util.Map;
import java.util.StringTokenizer;

public class PropertyParser {

    private static final char EOS = (char) -1;

    private static final int NAME_EXPECTED = 0;
    private static final int PARENT_CREATED = 1;
    private static final int INDEX_EXPECTED = 2;
    private static final int EOS_SEEN = 4;
    private static final int ARRAY_CREATED = 3;

    private PropertyParser() {
    }

    public static Property parseProperty(Class parentType, String fullName) throws ParseException,
                                                                                   PropertyNotFoundException {
        int state = NAME_EXPECTED;
        StringBuffer tokenBuf = new StringBuffer();
        Property parent = null;
        int pos = 0;

        while (true) {
            char c;

            // skip whitespace characters
            while (true) {
                c = getChar(fullName, pos);
                if (Character.isWhitespace(c)) {
                    pos++;
                } else {
                    break;
                }
            }

            if (state == NAME_EXPECTED) { // name expected
                if (Character.isJavaIdentifierStart(c)) {
                    tokenBuf.append(c);
                    pos++;
                    while (true) {
                        c = getChar(fullName, pos);
                        if (Character.isJavaIdentifierPart(c)) {
                            tokenBuf.append(c);
                            pos++;
                        } else {
                            break;
                        }
                    }
                    final String propertyName = tokenBuf.toString();
                    tokenBuf.setLength(0);

                    if (parent != null) {
                        parent = PropertyFactory.createNestedProperty(parent, propertyName);
                    } else {
                        parent = PropertyFactory.createChildProperty(parentType, propertyName);
                    }
                    if (parent == null) {
                        throw new PropertyNotFoundException("'" + propertyName + "' not found");
                    }
                    state = PARENT_CREATED; // parent created, '.' or '[' or EOS expected
                } else {
                    throw new ParseException("name expected, got " + fullName, pos);
                }
            } else if (state == PARENT_CREATED) { // parent created, '.' or '[' or EOS expected
                if (c == '.') {
                    pos++;
                    state = NAME_EXPECTED; // name expected
                } else if (c == '[') {
                    pos++;
                    state = INDEX_EXPECTED; // index or key expected
                } else if (c == EOS) {
                    state = EOS_SEEN; // EOS
                    break;
                } else {
                    throw new ParseException("'.' or '[' or EOS expected", pos);
                }
            } else if (state == INDEX_EXPECTED) { // index or key expected
                if (Character.isDigit(c)) {
                    tokenBuf.append(c);
                    pos++;
                    while (true) {
                        c = getChar(fullName, pos);
                        if (Character.isDigit(c)) {
                            tokenBuf.append(c);
                            pos++;
                        } else {
                            break;
                        }
                    }
                    final String indexString = tokenBuf.toString();
                    tokenBuf.setLength(0);
                    final int index = Integer.parseInt(indexString);
                    final Property arrayBase;
                    if (parent instanceof NestedProperty) {
                        NestedProperty np = (NestedProperty) parent;
                        arrayBase = np.getChild();
                    } else {
                        arrayBase = parent;
                    }
                    if (arrayBase.getType() != Property.UNKNOWN_TYPE && !arrayBase.getType().isArray()) {
                        throw new ParseException("'" + parent.getName() + "' is not a Java array", pos);
                    }
                    final ArrayProperty ap = new ArrayProperty(arrayBase, index);
                    if (parent instanceof NestedProperty) {
                        NestedProperty np = (NestedProperty) parent;
                        np.setChild(ap);
                    } else {
                        parent = ap;
                    }
                    state = ARRAY_CREATED; // array parent created, ']' expected
                } else if (c == '"') {
                    pos++;
                    while (true) {
                        c = getChar(fullName, pos);
                        // todo 3 nf/** - recognize escape characters
                        if (c == '"') {
                            pos++;
                            break;
                        } else if (c == EOS) {
                            throw new ParseException("string delimitter expected", pos);
                        }
                        tokenBuf.append(c);
                        pos++;
                    }
                    final String key = tokenBuf.toString();
                    tokenBuf.setLength(0);
                    final Property mapBase;
                    if (parent instanceof NestedProperty) {
                        NestedProperty np = (NestedProperty) parent;
                        mapBase = np.getChild();
                    } else {
                        mapBase = parent;
                    }
                    if (mapBase.getType() != Property.UNKNOWN_TYPE && !Map.class.isAssignableFrom(mapBase.getType())) {
                        throw new ParseException("'" + parent.getName() + "' is not a java.util.Map", pos);
                    }
                    final MapProperty mp = new MapProperty(key);
                    if (parent instanceof NestedProperty) {
                        parent = new NestedProperty(parent, mp);
                    } else {
                        parent = new NestedProperty(mapBase, mp);
                    }
                    //System.out.println("parent.getName() = " + parent.getName());
                    state = ARRAY_CREATED; // array parent created, ']' expected
                } else {
                    throw new ParseException("index or key expected", pos);
                }
            } else if (state == ARRAY_CREATED) { // array parent created, ']' expected
                if (c == ']') {
                    pos++;
                    state = PARENT_CREATED; // parent created, '.' or '[' or EOS expected
                } else {
                    throw new ParseException("']' expected", pos);
                }
            } else {
                throw new IllegalStateException();
            }
        }

        if (state != EOS_SEEN) { // EOS
            throw new IllegalStateException();
        }
        if (parent == null) {
            throw new IllegalStateException();
        }

        return parent;
    }

    public static Object parseValue(Class type, final String name, final String value) throws ParseException {
        final Object obj;
        try {
            if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
                obj = Boolean.valueOf(value);
            } else if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
                obj = Byte.valueOf(value);
            } else if (type.equals(Character.TYPE) || type.equals(Character.class)) {
                if (value.length() != 1) {
                    throw new ParseException(
                            "character value expected for property '" + name + "', value was '" + value + "'",
                            0);
                }
                obj = new Character(value.charAt(0));
            } else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
                obj = Short.valueOf(value);
            } else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
                obj = Integer.valueOf(value);
            } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
                obj = Long.valueOf(value);
            } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
                obj = Float.valueOf(value);
            } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
                obj = Double.valueOf(value);
            } else if (type.equals(String.class)) {
                obj = value;
            } else if (type.equals(File.class)) {
                obj = new File(value);
            } else if (type.equals(Color.class)) {
                obj = parseColor(value);
                if (obj == null) {
                    throw new ParseException(
                            "color value expected for property '" + name + "', value was '" + value + "'", 0);
                }
            } else if (Geometry.class.isAssignableFrom(type)) {
                obj = new GeometryParser().parseWKT(value);
            } else {
                throw new ParseException(
                        "don't know how to convert value for property '" + name + "', value was '" + value + "'", 0);
            }
        } catch (NumberFormatException e) {
            throw new ParseException("number value expected for property '" + name + "', value was '" + value + "'", 0);
        }
        return obj;
    }

    public static Object parseColor(String value) {
        final StringTokenizer st = new StringTokenizer(value, ",", false);
        final int count = st.countTokens();
        try {
            if (count == 1) {
                return Color.decode(value);
            } else if (count == 3 || count == EOS_SEEN) {
                final int r = Integer.parseInt(st.nextToken());
                final int g = Integer.parseInt(st.nextToken());
                final int b = Integer.parseInt(st.nextToken());
                final int a = count == EOS_SEEN ? Integer.parseInt(st.nextToken()) : 255;
                try {
                    return new Color(r, g, b, a);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static char getChar(String fullName, int pos) {
        char c;
        if (pos < fullName.length()) {
            c = fullName.charAt(pos);
        } else {
            c = EOS;
        }
        return c;
    }

}
