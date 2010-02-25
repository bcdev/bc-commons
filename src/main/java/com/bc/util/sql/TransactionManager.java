/*
 * $Id: TransactionManager.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface TransactionManager {

    DataSource getDataSource();

    void execute(Transaction transaction) throws SQLException;

}
