/*
 * $Id: QueryForListTransaction.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class QueryForListTransaction extends TemplateTransaction {

    private List resultList;

    public QueryForListTransaction(String templateSql, Class resultType, Object parameterObject) {
        super(templateSql, resultType, parameterObject);
    }

    public boolean isUpdate() {
        return false;
    }

    public List fetchResultList() {
        final List resultList = this.resultList;
        this.resultList = null;
        return resultList;
    }

    public void execute(Connection connection) throws SQLException {
        resultList = getTemplate().executeQueryForList(connection, getParameterObject());
    }
}
