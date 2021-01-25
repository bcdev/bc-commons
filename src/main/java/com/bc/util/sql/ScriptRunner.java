/*
 * $Id: ScriptRunner.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class ScriptRunner {

    private Connection connection;
    private LineNumberReader reader;
    private StringBuffer buffer;
    private ErrorHandler errorHandler;
    private Logger logger;
    private int lineNumber;
    private String sql;

    public ScriptRunner() {
        errorHandler = null;
        lineNumber = 0;
        sql = "";
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getSql() {
        return sql;
    }

    public void logError(SQLException e) {
        if (logger != null) {
            logger.severe("SQL script error, before or at line " + lineNumber + ": " + e.getMessage() + "\n" +
                          "  sql=\"" + this.sql + "\"");
        }
    }

    public void runScriptFromText(final Connection connection, final String scriptText) throws SQLException {
        try {
            runScript(connection, new StringReader(scriptText));
        } catch (IOException e) {
            throw new IllegalStateException("unexpected I/O error");
        }
    }

    public void runScriptFromResourcePath(final Connection connection, final String scriptResourcePath) throws SQLException {
        if (logger != null) {
            logger.info("executing SQL script '" + scriptResourcePath + "'...");
        }
        final InputStream resourceAsStream = getClass().getResourceAsStream(scriptResourcePath);
        //final InputStream resourceAsStream = ClassLoader.getSystemResourceAsStream(scriptResourcePath);
        final Reader reader = new InputStreamReader(resourceAsStream);
        try {
            runScript(connection, reader);
            if (logger != null) {
                logger.info("SQL script '" + scriptResourcePath + "' successfully executed");
            }
        } catch (IOException e) {
            throw new RuntimeException("unexpected I/O error", e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

    public void runScript(final Connection connection, final Reader reader) throws IOException,
                                                                                   SQLException {
        initScriptExecution(connection, reader);
        try {
            runScriptImpl();
        } finally {
            endScriptExecution();
        }
    }

    private void runScriptImpl() throws IOException,
                                        SQLException {
        String line;
        while (true) {
            line = this.reader.readLine();
            lineNumber = reader.getLineNumber();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (!line.startsWith("--")) {
                buffer.append(line);
                if (line.endsWith(";")) {
                    consumeSql();
                } else {
                    buffer.append(' ');
                }
            }
        }
        consumeSql();
    }

    private void initScriptExecution(Connection connection, Reader reader) {
        this.connection = connection;
        this.reader = new LineNumberReader(reader);
        this.buffer = new StringBuffer();
        this.sql = "";
        this.lineNumber = 0;
    }

    private void endScriptExecution() {
        this.connection = null;
        this.reader = null;
        this.buffer = null;
    }

    private void consumeSql() throws SQLException {
        String sql = buffer.toString().trim();
        if (sql.length() > 0) {
            try {
                executeSql(sql);
            } catch (SQLException e) {

                System.out.println("Last SQL statement causing the error: " + sql);

                logError(e);
                if (errorHandler != null) {
                    errorHandler.handleError(this, e);
                } else {
                    logError(e);
                    throw e;
                }
            }
        }
        buffer.setLength(0);
    }

    private void executeSql(final String sql) throws SQLException {
        this.sql = sql;
        final Statement stmt = connection.createStatement();
        stmt.executeUpdate(this.sql);
        stmt.close();
    }

    public static interface ErrorHandler {

        void handleError(ScriptRunner scriptRunner, SQLException e) throws SQLException;
    }

}
