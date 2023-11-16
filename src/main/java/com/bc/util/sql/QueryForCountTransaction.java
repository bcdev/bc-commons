/*
 * $Id: QueryForCountTransaction.java,v 1.2 2007-06-18 09:35:16 tom Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryForCountTransaction implements Transaction {

    public QueryForCountTransaction(String tableName) {
        this.tableName = tableName;
        this.count = -1;
    }

    public int fetchCount() {
        final int count = this.count;
        this.count = -1;
        return count;
    }

    public boolean isUpdate() {
        return false;
    }

    public void execute(Connection connection) throws SQLException {
        final String sql = "SELECT COUNT(*) FROM " + tableName;
        final PreparedStatement stmt = connection.prepareStatement(sql);
        final ResultSet rs;
        try {
            rs = stmt.executeQuery();
            try {
                rs.next();
                count = rs.getInt(1);
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////////

    private final String tableName;
    private int count;
}
