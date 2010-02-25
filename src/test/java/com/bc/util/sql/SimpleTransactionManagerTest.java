/*
 * $Id: SimpleTransactionManagerTest.java,v 1.1 2007-02-27 12:45:31 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import junit.framework.TestCase;

public class SimpleTransactionManagerTest extends TestCase {

//    public SimpleTransactionManagerTest(String s) {
//        super(s);
//    }
//
//    public void setUp() {
//         MockDataSource mockDatasource = new com.mockobjects.
//         MockConnection2 mockConnection = new MockConnection2();
//         mockDataSource.expectGetConnection(mockConnection);
//         mockConnection.expectClose();
//         SimpleTransactionManager transactionManager = new SimpleTransactionManager(mockDatasource);
//    }
//
    public void testConstructorArguments() {
        try {
            SimpleTransactionManager.create(null);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }
//
//    class DummyTransaction extends Assert implements Transaction {
//        Connection expectedConnection;
//        boolean executed = false;
//        boolean disposed = false;
//
//        public DummyTransaction(Connection expectedConnection) {
//            this.expectedConnection = expectedConnection;
//        }
//
//        public void execute(Connection connection) throws SQLException {
//            assertSame(expectedConnection, connection);
//            executed = true;
//        }
//
//        public void dispose() {
//            disposed = true;
//        }
//
//        public void verify() {
//            assertTrue(executed);
//            assertTrue(disposed);
//        }
//    }
//
//    public void noTest() throws SQLException{
//        SimpleTransactionManager transactionManager = new SimpleTransactionManager(null);
//        Transaction dummyTransaction = new DummyTransaction(mockConnection);
//        transactionManager.execute(dummyTransaction);
//        dummyTransaction.verify();
//        mockConnection.verify();
//        mockDataSource.verify();
//    }
}

