/*
 * $Id: QueryForObjectTransaction.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryForObjectTransaction extends TemplateTransaction {

    private Object resultObject;

    public QueryForObjectTransaction(String templateSql, Class resultType, Object parameterObject) {
        super(templateSql, resultType, parameterObject);
    }

    public Object fetchResultObject() {
        final Object resultObject = this.resultObject;
        this.resultObject = null;
        return resultObject;
    }

    public boolean isUpdate() {
        return false;
    }

    public void execute(Connection connection) throws SQLException {
        resultObject = getTemplate().executeQueryForObject(connection, getParameterObject());
    }
}
