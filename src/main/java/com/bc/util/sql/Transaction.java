/*
 * $Id: Transaction.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A database transaction.
 */
public interface Transaction {

    boolean isUpdate();

    /**
     * The execute method implements the actual transaction behaviour.
     *
     * @param connection the connection to be used for the transaction
     * @throws SQLException if a database error occurs
     */
    void execute(Connection connection) throws SQLException;
}

