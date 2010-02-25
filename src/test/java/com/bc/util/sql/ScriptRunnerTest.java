/*
 * Created at 06.04.2004 23:55:00
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.sql;

import com.bc.util.prop.PropertyNotFoundException;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;


public class ScriptRunnerTest extends TestCase {

    private Connection connection;

    protected void setUp() throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        connection = DriverManager.getConnection("jdbc:hsqldb:.", "sa", "");
    }

    protected void tearDown() throws Exception {
        connection.close();
        connection = null;
    }

    public void testInitialStateOfScriptRunner() {
        final ScriptRunner scriptRunner = new ScriptRunner();
        assertEquals(null, scriptRunner.getErrorHandler());
        assertEquals(0, scriptRunner.getLineNumber());
        assertEquals("", scriptRunner.getSql());
    }

    public void testRunScript() throws SQLException,
                                       ParseException,
                                       PropertyNotFoundException {
        String sqlScript =
                "\n" +
                "-- create the test table with name '_T1'\n" +
                "\n" +
                "CREATE TABLE _T1 (\n" +
                "    CI   INTEGER,\n" +
                "    CF   FLOAT,\n" +
                "    CS   VARCHAR,\n" +
                "    CD   DATE\n" +
                ");\n" +
                "\n" +
                "-- now inserting data\n" +
                "\n" +
                "INSERT INTO _T1 VALUES(1, 1.1, 'ABC', '2002-12-10');\n" +
                "INSERT INTO _T1 VALUES(2, 1.2, 'BCD', '2002-12-11');\n" +
                "INSERT INTO _T1 VALUES(3, 1.3, 'CDE', '2002-12-12');\n" +
                "INSERT INTO _T1 VALUES(4, 1.4, 'DEF', '2002-12-13');\n" +
                " INSERT INTO _T1 VALUES(5, 1.5, '0xA', '2002-12-14'); \n";

        final ScriptRunner scriptRunner = new ScriptRunner();
        scriptRunner.runScriptFromText(connection, sqlScript);
        assertEquals("INSERT INTO _T1 VALUES(5, 1.5, '0xA', '2002-12-14');", scriptRunner.getSql());

        Statement stmt = connection.createStatement();
        final ResultSet rs = stmt.executeQuery("SELECT COUNT (*) FROM _T1");
        rs.next();
        assertEquals(5, rs.getInt(1));
        rs.close();
        stmt.close();

        stmt = connection.createStatement();
        stmt.execute("DROP TABLE _T1");
        stmt.close();
    }
}
