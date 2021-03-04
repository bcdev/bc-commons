/*
 * $Id: CompositeTransaction.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * A transaction which is composed of other transactions.
 */
public class CompositeTransaction implements Transaction {

   public CompositeTransaction() {
        this.transactions = new LinkedList<Transaction>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    public boolean isUpdate() {

        for (int i = 0; i < transactions.size(); i++) {
            final Transaction t = transactions.get(i);
            if (t.isUpdate()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Executes this transactions by delegating the call to all contained transactions.
     * Order of execution is order of adding delegates to this transaction.
     * @param connection
     * @param connection the connection to be used for the transaction
     *
     * @throws SQLException if a database error occurs
     */
    public void execute(Connection connection) throws SQLException {
        for (int i = 0; i < transactions.size(); i++) {
            final Transaction t = transactions.get(i);
            if (t instanceof UpdateTransaction) {
                final String sql = ((UpdateTransaction) t).getTemplate().getSql();
                System.out.println("Trying to execute: " + sql);
                System.out.println(((UpdateTransaction) t).getParameterObject());
            }
            System.out.println(t.getClass().getName());
            t.execute(connection);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private List<Transaction> transactions;
}
