/*
 * $Id: GeometryParserTest.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import junit.framework.TestCase;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.text.ParseException;

public class GeometryParserTest extends TestCase {

    private GeometryParser parser;

    public GeometryParserTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        parser = new GeometryParser();
    }

    protected void tearDown() throws Exception {
        parser = null;
    }

    public void testParseExceptionCorrectlyThrown() {
        assertParseExceptionThrown("1,2", "geometry type name expected");
        assertParseExceptionThrown("Point 1 2", "'(' expected");
        assertParseExceptionThrown("Point(1 2", "')' expected");
        assertParseExceptionThrown("Point(1 2]", "')' expected");
        assertParseExceptionThrown("Point(,1 2)", "x-value expected");
        assertParseExceptionThrown("Point(1, 2)", "y-value expected");
        assertParseExceptionThrown("Polyfon((1 2,2 3,5 3))", "geometry type name expected");
        assertParseExceptionThrown("Polygon((1 2,2 3,5 3),1 2,2 3,5 3))", "'(' expected");
        assertParseExceptionThrown("Polygon((1 2 2 3 5 3))", "',' or ')' expected");
        assertParseExceptionThrown("Polygon((1 2,2 3,5 3)(1 2,2 3,5 3))", "',' or ')' expected");
    }

    public void testPointParsing() throws ParseException {
        Geometry g;
        PointGeometry p;

        g = parser.parseWKT("Point(1 2)");
        assertNotNull(g);
        assertEquals(PointGeometry.class, g.getClass());
        assertEquals("POINT(1 2)", g.toString());
        p = (PointGeometry) g;
        assertEquals(1.0, p.getX(), 1e-10);
        assertEquals(2.0, p.getY(), 1e-10);

        g = parser.parseWKT("POINT (0.1   -0.2 )");
        assertNotNull(g);
        assertEquals(PointGeometry.class, g.getClass());
        assertEquals("POINT(0.1 -0.2)", g.toString());
        p = (PointGeometry) g;
        assertEquals(0.1, p.getX(), 1e-10);
        assertEquals(-0.2, p.getY(), 1e-10);
    }

    public void testLineStringParsing() throws ParseException {
        Geometry g;
        Shape s;
        PathIterator i;
        double c[] = new double[6];

        g = parser.parseWKT("LineString(1 2,3 4.3,  -0.6 5)");
        assertNotNull(g);
        assertEquals(LineStringGeometry.class, g.getClass());
        assertEquals("LINESTRING(1 2,3 4.3,-0.6 5)", g.toString());
        s = g.getAsShape();
        assertNotNull(s);
        i = s.getPathIterator(null);
        assertMoveTo(i, c, 1.0, 2.0);
        assertLineTo(i, c, 3.0, 4.3);
        assertLineTo(i, c, -.6, 5.0);
        assertDone(i);
    }

    public void testParseImplicitlyAutoclosedPolygon() throws ParseException {
        Geometry g;
        Shape s;
        PathIterator i;
        double c[] = new double[6];

        g = parser.parseWKT("Polygon ((1 2,3 4,  5  6))");
        assertNotNull(g);
        assertEquals(PolygonGeometry.class, g.getClass());
        assertEquals("POLYGON((1 2,3 4,5 6,1 2))", g.toString());
        s = g.getAsShape();
        assertNotNull(s);
        i = s.getPathIterator(null);
        assertMoveTo(i, c, 1.0, 2.0);
        assertLineTo(i, c, 3.0, 4.0);
        assertLineTo(i, c, 5.0, 6.0);
        assertClose(i, c);
        assertDone(i);
     }

    public void testParseExplicitlyClosedPolygon() throws ParseException {
       Geometry g;
       Shape s;
       PathIterator i;
       double c[] = new double[6];

       g = parser.parseWKT("Polygon ((1 2,3 4,  5 6, 1 2))");
       assertNotNull(g);
       assertEquals(PolygonGeometry.class, g.getClass());
       assertEquals("POLYGON((1 2,3 4,5 6,1 2))", g.toString());
       s = g.getAsShape();
       assertNotNull(s);
       i = s.getPathIterator(null);
       assertMoveTo(i, c, 1.0, 2.0);
       assertLineTo(i, c, 3.0, 4.0);
       assertLineTo(i, c, 5.0, 6.0);
       assertClose(i, c);
       assertDone(i);
    }

    public void testParseExplicitlyClosedPolygonUsingDecimalPowerNotationOnY() throws ParseException {
       Geometry g;
       Shape s;
       PathIterator i;
       double c[] = new double[6];

       g = parser.parseWKT("Polygon ((1 2,3 4,    5 6e1,   1 2))");
       assertNotNull(g);
       assertEquals(PolygonGeometry.class, g.getClass());
       assertEquals("POLYGON((1 2,3 4,5 60,1 2))", g.toString());
       s = g.getAsShape();
       assertNotNull(s);
       i = s.getPathIterator(null);
       assertMoveTo(i, c, 1.0, 2.0);
       assertLineTo(i, c, 3.0, 4.0);
       assertLineTo(i, c, 5.0, 60.0);
       assertClose(i, c);
       assertDone(i);
    }

    public void testParseExplicitlyClosedPolygonUsingDecimalPowerNotationOnX() throws ParseException {
       Geometry g;
       Shape s;
       PathIterator i;
       double c[] = new double[6];

       g = parser.parseWKT("Polygon ((-1e-1 2,3 4,  5e1    6, -1e-1 2))");
       assertNotNull(g);
       assertEquals(PolygonGeometry.class, g.getClass());
       assertEquals("POLYGON((-0.1 2,3 4,50 6,-0.1 2))", g.toString());
       s = g.getAsShape();
       assertNotNull(s);
       i = s.getPathIterator(null);
       assertMoveTo(i, c, -0.1, 2.0);
       assertLineTo(i, c, 3.0, 4.0);
       assertLineTo(i, c, 50.0, 6.0);
       assertClose(i, c);
       assertDone(i);
    }

    public void testParseMultiplePointlistPolygon() throws ParseException {
        Geometry g;
        Shape s;
        PathIterator i;
        double c[] = new double[6];

        g = parser.parseWKT("POLYGON( (1 2,3 4, 5 6,1 2) , (7 8,9 10,11 12, 7 8) )");
        assertNotNull(g);
        assertEquals(PolygonGeometry.class, g.getClass());
        assertEquals("POLYGON((1 2,3 4,5 6,1 2),(7 8,9 10,11 12,7 8))", g.toString());
        s = g.getAsShape();
        assertNotNull(s);
        i = s.getPathIterator(null);
        assertMoveTo(i, c, 1.0, 2.0);
        assertLineTo(i, c, 3.0, 4.0);
        assertLineTo(i, c, 5.0, 6.0);
        assertClose(i, c);
        assertMoveTo(i, c, 7.0, 8.0);
        assertLineTo(i, c, 9.0, 10.0);
        assertLineTo(i, c, 11.0, 12.0);
        assertClose(i, c);
        assertDone(i);
    }

    public void testMultiPolygonParsing() throws ParseException {
        String multiPolyTest = "MULTIPOLYGON(((-180 -90,-180 0,0 0,0 -90,-180 -90)),((-180 -90,-180 0,360 0,360 -90,-180 -90)))";
        Geometry g;

        g = parser.parseWKT(multiPolyTest);
        assertNotNull(g);
        assertEquals(MultiPolygonGeometry.class, g.getClass());
        assertEquals(multiPolyTest, g.toString());
    }

    private void assertMoveTo(PathIterator i, double[] c, final double x, final double y) {
        assertSegPoint(i, c, PathIterator.SEG_MOVETO, x, y);
    }

    private void assertLineTo(PathIterator i, double[] c, final double x, final double y) {
        assertSegPoint(i, c, PathIterator.SEG_LINETO, x, y);
    }

    private void assertClose(PathIterator i, double[] c) {
        assertEquals(false, i.isDone());
        assertEquals(PathIterator.SEG_CLOSE, i.currentSegment(c));
        i.next();
    }

    private void assertSegPoint(PathIterator i, double[] c, final int segType, final double x, final double y) {
        assertEquals(false, i.isDone());
        assertEquals(segType, i.currentSegment(c));
        assertEquals(x, c[0], 1e-6);
        assertEquals(y, c[1], 1e-6);
        i.next();
    }

    private void assertDone(PathIterator i) {
        assertEquals(true, i.isDone());
    }

    private void assertParseExceptionThrown(final String invalidWkt, String expectedMsg) {
        try {
            parser.parseWKT(invalidWkt);
            fail(expectedMsg);
        } catch (ParseException expectedError) {
            assertEquals(expectedMsg, expectedError.getMessage());
        }
    }

}

