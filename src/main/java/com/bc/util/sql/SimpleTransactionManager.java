/*
 * $Id: SimpleTransactionManager.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SimpleTransactionManager implements TransactionManager {

    private final DataSource dataSource;

    private SimpleTransactionManager(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("datasource is null");
        }
        this.dataSource = dataSource;
    }

    public static TransactionManager create(DataSource dataSource) {
        return new SimpleTransactionManager(dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void execute(Transaction transaction) throws SQLException {
        final Connection connection = dataSource.getConnection();
        boolean mustResetAutoCommit = false;
        try {
            if (transaction.isUpdate()) {
                if (connection.getAutoCommit()) {
                    connection.setAutoCommit(false);
                    mustResetAutoCommit = true;
                }
                try {
//                    System.out.println(transaction.getClass().getName());
//                    if (transaction.getClass().getName().equals("com.bc.util.sql.UpdateTransaction")) {
//                        System.out.println("Transaction: " + ((UpdateTransaction) transaction).getTemplate().getSql());
//                    }
                    transaction.execute(connection);
                } catch (SQLException e) {

                    if (transaction instanceof TemplateTransaction ||
                            transaction instanceof UpdateTransaction) {
                        final TemplateTransaction trans = (TemplateTransaction) transaction;
                        final String sql = trans.getTemplate().getSql();
                        System.out.println("Last SQL statement causing the error: " + sql);
                    }

                    else if (transaction instanceof CompositeTransaction){
                        final CompositeTransaction trans = (CompositeTransaction) transaction;
//                        final String sql = trans.getTemplate().getSql();
//                        System.out.println("Last SQL statement causing the error: " + sql);
                     }

                    connection.rollback();
                    throw e;
                }
                connection.commit();
            } else {
                transaction.execute(connection);
            }
        } finally {
            if (mustResetAutoCommit) {
                connection.setAutoCommit(true);
            }
            connection.close();
        }
    }
}
