/*
 * $Id: UpdateTransaction.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import java.sql.Connection;
import java.sql.SQLException;

public class UpdateTransaction extends TemplateTransaction {

    private int resultValue;

    public UpdateTransaction(String sql) {
        super(sql);
    }

    public UpdateTransaction(String templateSql, Object parameterObject) {
        super(templateSql, null, parameterObject);
    }

    public int fetchResultValue() {
        final int result = this.resultValue;
        this.resultValue = -1;
        return result;
    }

    public boolean isUpdate() {
        return true;
    }

    public void execute(Connection connection) throws SQLException {
        resultValue = getTemplate().executeUpdate(connection, getParameterObject());
    }
}
