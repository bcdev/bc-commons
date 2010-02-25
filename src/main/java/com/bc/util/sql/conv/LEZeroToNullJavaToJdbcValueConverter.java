package com.bc.util.sql.conv;

import com.bc.util.prop.Property;

/**
 * By convention some number fields in tables may signal no value not only by containing NULL, but also 0 or negative
 * values (e.g. ids). This converter honors this convention. <p>The other way round is solved by the {@link
 * DefaultValueConverter} which transforms the NULL value in a number field to zero of the corresponding Java number
 * type.
 */
public class LEZeroToNullJavaToJdbcValueConverter implements JavaToJdbcValueConverter {

    public Object convertJavaToJdbcValue(Property property, Object javaValue) {
        if (javaValue == null || ((Number) javaValue).doubleValue() <= 0.0) {
            return null;
        }
        return javaValue;
    }
}
