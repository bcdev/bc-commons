/*
 * $Id$
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;

public class QueryForSetTransaction<T> extends TemplateTransaction {

    private TreeSet<T> result;

    public QueryForSetTransaction(String templateSql, Class<T> resultType, Object parameterObject) {
        super(templateSql, resultType, parameterObject);
    }

    public boolean isUpdate() {
        return false;
    }

    public Collection<T> fetchResult() {
        final Collection<T> result = this.result;
        this.result = null;
        return result;
    }

	public void execute(Connection connection) throws SQLException {
        this.result = new TreeSet<T>();
        getTemplate().executeQueryForCollection(connection, getParameterObject(), result);
    }
}
