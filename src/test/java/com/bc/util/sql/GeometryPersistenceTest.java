package com.bc.util.sql;

import com.bc.util.geom.GeometryParser;
import com.bc.util.geom.PointGeometry;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo 1 ok/tb Eliminate database access in tests (as discussed on the phone)
 * This test depends on a special hsql-version that understands the datatype GEOMETRY
 * All tests have been deactivated... (olafk)
 * $Id: GeometryPersistenceTest.java,v 1.3 2007-11-20 16:20:50 tom Exp $
 * Copyright by Brockmann Consult, 2004
 */

public class GeometryPersistenceTest extends TestCase {

    private Connection connection;
    private GeometryParser wktParser;

    protected void setUp() throws Exception {
        wktParser = new GeometryParser();
//        Class.forName("org.hsqldb.jdbcDriver");
//        connection = DriverManager.getConnection("jdbc:hsqldb:.", "sa", "");
    }

    protected void tearDown() throws Exception {
//        connection.close();
//        connection = null;
    }

    public void testDummy() {

    }

    public final void dontTestThatGeometryIsInsertedCorrectly() throws SQLException,
            ParseException {
        final String tableName = "lwedkjfl";
        PointGeometry p;
        UpdateTransaction ut;
        QueryForObjectTransaction qt;
        Map m = new HashMap();

        ut = new UpdateTransaction("CREATE TABLE " + tableName + " (\n" +
                "    Name VARCHAR,\n" +
                "    ROI GEOMETRY\n" +
                ");");
        ut.execute(connection);

        p = new PointGeometry(23, 54);
        m.put("name", "bimbo");
        m.put("roi", p);
        ut = new UpdateTransaction("INSERT INTO " + tableName + " VALUES(" +
                "  ${name}, " +
                "  GeomFromText(${roi})" +
                ")", m);
        ut.execute(connection);

        qt = new QueryForObjectTransaction("SELECT " +
                "name as name, " +
                "roi as roi " +
                "FROM " + tableName + "",
                Map.class, null);
        qt.execute(connection);
        Map rm = (Map) qt.fetchResultObject();

        assertEquals("bimbo", rm.get("name"));
        assertEquals("POINT(23 54)", rm.get("roi"));
        assertEquals(new PointGeometry(23, 54), wktParser.parseWKT((String) rm.get("roi")));
    }

    public void dontTestContainsGeometry() throws SQLException,
            ParseException {
        final String tableName = "iuztiewrq";
        UpdateTransaction ut;
        Map m = new HashMap();

        ut = new UpdateTransaction("CREATE TABLE " + tableName + " (\n" +
                "    Name VARCHAR,\n" +
                "    ROI GEOMETRY\n" +
                ");");
        ut.execute(connection);

        final String templateSql = "INSERT INTO " + tableName + " VALUES(" +
                "  ${name}, " +
                "  GeomFromText(${roi})" +
                ")";
        m.put("name", "bimbo");
        m.put("roi", new PointGeometry(3, 4));
        ut = new UpdateTransaction(templateSql, m);
        ut.execute(connection);

        m.put("name", "bimbo2");
        m.put("roi", wktParser.parseWKT("POLYGON((1 1,5 1,5 5,1 5,1 1))"));
        ut = new UpdateTransaction(templateSql, m);
        ut.execute(connection);

        final QueryForListTransaction ql = new QueryForListTransaction("SELECT " +
                "name as name, " +
                "ASTEXT(roi) as roi " +
                "FROM " + tableName + " " +
                "WHERE CONTAINS(roi, GeomFromText('POINT(2 4)')) = 1",
                Map.class, null);
        ql.execute(connection);
        final List list = ql.fetchResultList();
        assertNotNull(list);
        assertEquals(1, list.size());
        Map map = (Map) list.get(0);
        assertNotNull(map);
        assertEquals("bimbo2", map.get("name"));
        assertEquals("POLYGON((1 1,5 1,5 5,1 5,1 1))", map.get("roi"));
    }
}
