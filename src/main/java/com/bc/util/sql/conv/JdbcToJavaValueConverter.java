/*
 * Created at 17.04.2004 21:07:53
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.sql.conv;

import com.bc.util.prop.Property;

public interface JdbcToJavaValueConverter {

    Object convertJdbcToJavaValue(final Property property, final Object jdbcValue);
}
