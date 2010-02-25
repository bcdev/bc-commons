/*
 * Created at 06.04.2004 23:55:00
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.sql;

import com.bc.util.prop.Property;
import com.bc.util.prop.PropertyNotFoundException;
import com.bc.util.sql.conv.DefaultValueConverter;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


public class TemplateTest extends TestCase {

    private Connection connection;

    protected void setUp() throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        connection = DriverManager.getConnection("jdbc:hsqldb:.", "sa", "");
        execute("CREATE TABLE T1 (\n" +
                "    CI   INTEGER,\n" +
                "    CF   FLOAT,\n" +
                "    CS   VARCHAR,\n" +
                "    CD   DATE\n" +
                ")");
        execute("INSERT INTO T1 VALUES(1, 1.1, 'ABC', '2002-12-10')");
        execute("INSERT INTO T1 VALUES(2, 1.2, 'BCD', '2002-12-11')");
        execute("INSERT INTO T1 VALUES(3, 1.3, 'CDE', '2002-12-12')");
        execute("INSERT INTO T1 VALUES(4, 1.4, 'DEF', '2002-12-13')");
        execute("INSERT INTO T1 VALUES(5, 1.5, '0xA', '2002-12-14')");
    }

    protected void tearDown() throws Exception {
        execute("DROP TABLE T1");
        connection.close();
        connection = null;
    }

    public void testTemplateSqlParsing() throws SQLException,
                                                ParseException,
                                                PropertyNotFoundException {
        String sql =
                "SELECT " +
                "  T1.CI AS pi," +
                "  T1.CF AS pf," +
                "  T1.CS AS ps," +
                "  T1.CD AS pd " +
                "FROM T1 " +
                "WHERE T1.CI = ${qi} OR T1.CF < ${qf} OR T1.CS = ${qs} OR T1.CD >= ${qd} ";

        final Template t = new Template(sql, Q.class, P.class);
        StringBuffer sb = new StringBuffer(sql);
        sb.replace(sb.indexOf("${qi}"), sb.indexOf("${qi}") + 5, "?");
        sb.replace(sb.indexOf("${qf}"), sb.indexOf("${qf}") + 5, "?");
        sb.replace(sb.indexOf("${qs}"), sb.indexOf("${qs}") + 5, "?");
        sb.replace(sb.indexOf("${qd}"), sb.indexOf("${qd}") + 5, "?");
        assertEquals(sb.toString(), t.getSql());

        assertEquals(Q.class, t.getParameterType());
        assertEquals(P.class, t.getResultType());

        final Property[] inputProperties = t.getParameterProperties();
        assertEquals(4, inputProperties.length);
        assertEquals("qi", inputProperties[0].getName());
        assertEquals("qf", inputProperties[1].getName());
        assertEquals("qs", inputProperties[2].getName());
        assertEquals("qd", inputProperties[3].getName());

        final Property[] outputProperties = t.getResultProperties();
        assertEquals(4, outputProperties.length);
        assertEquals("pi", outputProperties[0].getName());
        assertEquals("pf", outputProperties[1].getName());
        assertEquals("ps", outputProperties[2].getName());
        assertEquals("pd", outputProperties[3].getName());
    }

    public void testQueryObjectWithIntOutputType() throws SQLException,
                                                          ParseException,
                                                          PropertyNotFoundException {
        String sql = "SELECT COUNT (*) FROM T1";
        final Template t = new Template(sql, null, Integer.class);
        final Object o = t.executeQueryForObject(connection, null);
        assertType(Integer.class, o);
        assertEquals(new Integer(5), o);
    }

    public void testQueryObjectWithFloatOutputType() throws SQLException,
                                                            ParseException,
                                                            PropertyNotFoundException {
        String sql =
                "SELECT " +
                "  T1.CF AS value " +
                "FROM T1 " +
                "WHERE T1.CI = 3";
        final Template t = new Template(sql, null, Float.class);
        final Object o = t.executeQueryForObject(connection, null);
        assertType(Float.class, o);
        assertEquals(new Float(1.3f), o);
    }

    public void testQueryListWithSingleInputValue() throws SQLException,
                                                           ParseException,
                                                           PropertyNotFoundException {
        String sql =
                "SELECT " +
                "  T1.CI AS pi," +
                "  T1.CF AS pf," +
                "  T1.CS AS ps," +
                "  T1.CD AS pd " +
                "FROM T1 " +
                "WHERE T1.CF <= ${value}";

        final Template t = new Template(sql, Double.class, P.class);
        final List list = t.executeQueryForList(connection, new Double(1.3));
        assertEquals(3, list.size());
        assertType(P.class, list.get(0));
        assertType(P.class, list.get(1));
        assertType(P.class, list.get(2));
    }

    public void testAddAndRemoveForJdbcToJavaValueConverter() throws SQLException {
        String sql =
                "SELECT " +
                "  T1.CS AS value " +
                "FROM T1 " +
                "WHERE T1.CS = '0xA'";

        Object result;
        final Template t = new Template(sql, null, Integer.class);

        result = t.executeQueryForObject(connection, null);
        assertType(String.class, result);
        assertEquals("0xA", result);

        t.addJdbcToJavaValueConverter("value", new HexValueConverter());
        result = t.executeQueryForObject(connection, null);
        assertType(Integer.class, result);
        assertEquals(new Integer(10), result);

        t.removeJdbcToJavaValueConverter("value");
        result = t.executeQueryForObject(connection, null);
        assertType(String.class, result);
        assertEquals("0xA", result);
    }

    public void testJavaToJdbcValueValueConverterAddRemove() throws SQLException {
        String sql =
                "SELECT " +
                "  T1.CS AS value " +
                "FROM T1 " +
                "WHERE T1.CS = ${value}";

        Object result;
        final Template t = new Template(sql, Integer.class, String.class);

        try {
            result = t.executeQueryForObject(connection, new Integer(10));
            // T1.CS is of type VARCHAR, input is an int
            assertNull(result);
        } catch (SQLException expected) {
        }

        t.addJavaToJdbcValueConverter("value", new HexValueConverter());
        result = t.executeQueryForObject(connection, new Integer(10));
        assertType(String.class, result);
        assertEquals("0xA", result);

        t.removeJavaToJdbcValueConverter("value");
        try {
            result = t.executeQueryForObject(connection, new Integer(10));
            // T1.CS is of type VARCHAR, input is an int
            assertNull(result);
        } catch (SQLException expected) {
        }
    }

    private void execute(final String sql) throws SQLException {
        final Statement stmt = connection.createStatement();
        stmt.execute(sql);
        stmt.close();
    }

    private void assertType(Class type, Object value) {
        assertNotNull(value);
        assertEquals(type, value.getClass());
    }

    public static class P {

        private int pi;
        private double pf;
        private String ps;
        private Date pd;

        public P() {
        }

        public int getPi() {
            return pi;
        }

        public void setPi(int pi) {
            this.pi = pi;
        }

        public double getPf() {
            return pf;
        }

        public void setPf(double pf) {
            this.pf = pf;
        }

        public String getPs() {
            return ps;
        }

        public void setPs(String ps) {
            this.ps = ps;
        }

        public Date getPd() {
            return pd;
        }

        public void setPd(Date pd) {
            this.pd = pd;
        }
    }

    public static class Q {

        private int qi;
        private double qf;
        private String qs;
        private Date qd;

        public Q() {
        }

        public int getQi() {
            return qi;
        }

        public void setQi(int qi) {
            this.qi = qi;
        }

        public double getQf() {
            return qf;
        }

        public void setQf(double qf) {
            this.qf = qf;
        }

        public String getQs() {
            return qs;
        }

        public void setQs(String qs) {
            this.qs = qs;
        }

        public Date getQd() {
            return qd;
        }

        public void setQd(Date qd) {
            this.qd = qd;
        }
    }

    public static class HexValueConverter extends DefaultValueConverter {

        public Object convertJavaToJdbcValue(Property property, Object javaValue) {
            if (Integer.class.isAssignableFrom(property.getType())) {
                return "0x" + Integer.toHexString(((Integer) javaValue).intValue()).toUpperCase();
            }
            return super.convertJavaToJdbcValue(property, javaValue);
        }

        public Object convertJdbcToJavaValue(Property property, Object jdbcValue) {
            if (jdbcValue instanceof String) {
                final String s = jdbcValue.toString();
                if (Integer.class.isAssignableFrom(property.getType())) {
                    if (s.startsWith("0x") || s.startsWith("0X")) {
                        return Integer.valueOf(s.substring(2), 16);
                    }
                }
            }
            return super.convertJdbcToJavaValue(property, jdbcValue);
        }
    }
}
