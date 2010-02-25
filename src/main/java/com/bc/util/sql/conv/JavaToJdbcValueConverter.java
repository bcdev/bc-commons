/*
 * Created at 17.04.2004 21:08:13
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.sql.conv;

import com.bc.util.prop.Property;

public interface JavaToJdbcValueConverter {

    Object convertJavaToJdbcValue(final Property property, final Object javaValue);
}
