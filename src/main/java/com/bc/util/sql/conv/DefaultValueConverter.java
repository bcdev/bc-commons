/*
 * Created at 17.04.2004 21:12:21
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.sql.conv;

import com.bc.util.geom.Geometry;
import com.bc.util.geom.GeometryParser;
import com.bc.util.prop.Property;

import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;

public class DefaultValueConverter implements ValueConverter {

    public Object convertJavaToJdbcValue(Property property, Object javaValue) {
        if (javaValue == null) {
            return null;
        }
        Object jdbcValue = javaValue;
        if (javaValue instanceof java.util.Date) {
            // todo nf/** 3 - if this default conversion causes problems use a special JavaToJdbcValueConverter
            jdbcValue = new java.sql.Timestamp(((java.util.Date) javaValue).getTime());
        } else if (javaValue instanceof Boolean) {
            jdbcValue = new Integer(((Boolean) javaValue).booleanValue() ? 1 : 0);
        } else if (javaValue instanceof Geometry) {
            final Geometry geometry = (Geometry) javaValue;
            jdbcValue = geometry.getAsText();
        } else {
            // add more here if required
        }
        return jdbcValue;
    }

    public Object convertJdbcToJavaValue(Property property, Object jdbcValue) {
        Object javaValue = jdbcValue;
        if (jdbcValue == null) {
            if (boolean.class.equals(property.getType())) {
                javaValue = Boolean.FALSE;
            } else if (char.class.equals(property.getType())) {
                javaValue = new Character('\000');
            } else if (byte.class.equals(property.getType())) {
                javaValue = new Byte((byte) 0);
            } else if (short.class.equals(property.getType())) {
                javaValue = new Short((short) 0);
            } else if (int.class.equals(property.getType())) {
                javaValue = new Integer(0);
            } else if (long.class.equals(property.getType())) {
                javaValue = new Long(0L);
            } else if (float.class.equals(property.getType())) {
                javaValue = new Float(0.0f);
            } else if (double.class.equals(property.getType())) {
                javaValue = new Double(0.0);
            }
        } else if (jdbcValue instanceof Number) {
            final Number jdbcNumber = (Number) jdbcValue;
            if (Boolean.class.equals(property.getType()) || boolean.class.equals(property.getType())) {
                javaValue = Boolean.valueOf(jdbcNumber.intValue() != 0);
            } else if (Character.class.equals(property.getType()) || char.class.equals(property.getType())) {
                javaValue = new Character((char) (jdbcNumber.intValue() & 0xffff));
            } else if (Byte.class.equals(property.getType()) || byte.class.equals(property.getType())) {
                javaValue = new Byte(jdbcNumber.byteValue());
            } else if (Short.class.equals(property.getType()) || short.class.equals(property.getType())) {
                javaValue = new Short(jdbcNumber.shortValue());
            } else if (Integer.class.equals(property.getType()) || int.class.equals(property.getType())) {
                javaValue = new Integer(jdbcNumber.intValue());
            } else if (Long.class.equals(property.getType()) || long.class.equals(property.getType())) {
                javaValue = new Long(jdbcNumber.longValue());
            } else if (Float.class.equals(property.getType()) || float.class.equals(property.getType())) {
                javaValue = new Float(jdbcNumber.floatValue());
            } else if (Double.class.equals(property.getType()) || double.class.equals(property.getType())) {
                javaValue = new Double(jdbcNumber.doubleValue());
            } else if (String.class.equals(property.getType())) {
                javaValue = jdbcNumber.toString();
            }
        } else if (jdbcValue instanceof String) {
            if (Geometry.class.isAssignableFrom(property.getType())) {
                try {
                    javaValue = new GeometryParser().parseWKT((String) jdbcValue);
                } catch (ParseException e) {
                    // todo - 3 tb/** eventually define new exception class here
                    throw new IllegalArgumentException(e.getMessage());
                }
            }

        } else if ((jdbcValue instanceof byte[])) {
            if (Geometry.class.isAssignableFrom(property.getType())) {
                try {
                    javaValue = new GeometryParser().parseWKT(new String((byte[]) jdbcValue));
                } catch (ParseException e) {
                    // todo - 3 tb/** eventually define new exception class here
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        } else if (jdbcValue instanceof java.sql.Timestamp) {
            if (java.util.Date.class.equals(property.getType())) {
                javaValue = new java.util.Date(((java.sql.Timestamp) jdbcValue).getTime());
            }
        } else if (jdbcValue instanceof java.sql.Date) {
            if (java.util.Date.class.equals(property.getType())) {
                javaValue = new java.util.Date(((java.sql.Date) jdbcValue).getTime());
            }
        } else if (jdbcValue instanceof Blob && property.getType().equals(byte[].class)) {
            Blob blob = (Blob) jdbcValue;
            try {
                long llength = (int) blob.length();
                // completely artificial limit of 42 megabytes...
                if (llength > 42 * 1024 * 1024) {
                    throw new IllegalStateException("blob too large to assign to byte[] (limit is 42M)");
                }
                int length = (int) llength;
                javaValue = blob.getBytes(1, length);
            } catch (SQLException e) {
                throw new IllegalArgumentException(e.getMessage());
            }

        } else {
            // add more here if required
        }
        return javaValue;
    }
}
