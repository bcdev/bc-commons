package com.bc.util.geom;

import junit.framework.TestCase;

import java.awt.*;
import java.text.ParseException;

@SuppressWarnings({"MagicNumber", "UnusedAssignment"})
public class MultiPolygonGeometryTest extends TestCase {

    public void testGetDimension() {
        assertEquals(2, multi.getDimension());
    }

    public void testGeometryType() {
        assertEquals(Geometry.MULTIPOLYGON, multi.getGeometryType());
    }

    public void testAddPolygon() throws ParseException {
        assertEquals(0, multi.getPolygonCount());

        PolygonGeometry polygon = (PolygonGeometry) parser.parseWKT("POLYGON((0 0,1 0,1 1,0 1,0 0))");
        multi.addPolygon(polygon);
        assertEquals(1, multi.getPolygonCount());

        polygon = (PolygonGeometry) parser.parseWKT("POLYGON((1 1,2 1,2 2,1 2,1 1))");
        multi.addPolygon(polygon);
        assertEquals(2, multi.getPolygonCount());
    }

    public void testRemovePolygon() throws ParseException {
        final PolygonGeometry polygon_1 = (PolygonGeometry) parser.parseWKT("POLYGON((0 0,1 0,1 1,0 1,0 0))");
        multi.addPolygon(polygon_1);
        final PolygonGeometry polygon_2 = (PolygonGeometry) parser.parseWKT("POLYGON((1 1,2 1,2 2,1 2,1 1))");
        multi.addPolygon(polygon_2);

        assertEquals(2, multi.getPolygonCount());

        multi.removePolygon(polygon_1);
        assertEquals(1, multi.getPolygonCount());

        multi.removePolygon(polygon_1);
        assertEquals(1, multi.getPolygonCount());

        multi.removePolygon(polygon_2);
        assertEquals(0, multi.getPolygonCount());
    }

    public void testGetPolygon() throws ParseException {
        final PolygonGeometry polygon_1 = (PolygonGeometry) parser.parseWKT("POLYGON((0 0,1 0,1 1,0 1,0 0))");
        multi.addPolygon(polygon_1);
        final PolygonGeometry polygon_2 = (PolygonGeometry) parser.parseWKT("POLYGON((1 1,2 1,2 2,1 2,1 1))");
        multi.addPolygon(polygon_2);

        PolygonGeometry polygon = multi.getPolygon(0);
        assertEquals(polygon_1.getAsText(), polygon.getAsText());

        polygon = multi.getPolygon(1);
        assertEquals(polygon_2.getAsText(), polygon.getAsText());

        try {
            polygon = multi.getPolygon(4);
            fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException expected) {
        }

        try {
            polygon = multi.getPolygon(-2);
            fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    public void testGetAsShape() throws ParseException {
        prepareMultiPolygon();

        final Shape shape = multi.getAsShape();
        assertNotNull(shape);
        final Rectangle bounds = shape.getBounds();
        assertEquals(2.0, bounds.getHeight(), 1e-6);
        assertEquals(2.0, bounds.getWidth(), 1e-6);
        assertTrue(shape.contains(0.4, 0.5));
        assertFalse(shape.contains(1.4, 0.5));
    }

    public void testGetAsText() throws ParseException {
        prepareMultiPolygon();
        final String result = "MULTIPOLYGON(((0 0,1 0,1 1,0 1,0 0)),((1 1,2 1,2 2,1 2,1 1)))";

        assertEquals(result, multi.getAsText());
    }

    public void testGetEquals_ReturnsTrueForComparisonWithSelf() throws ParseException {
        prepareMultiPolygon();

        assertEquals(Geometry.TRUE, multi.getEquals(multi));
    }

    public void testGetEquals_ReturnsUnknownForComparisonWithNull() throws ParseException {
        prepareMultiPolygon();

        assertEquals(Geometry.UNKNOWN, multi.getEquals(null));
    }

    public void testGetEquals_ReturnsFalseForComparisonWithOtherGeometries() throws ParseException {
        prepareMultiPolygon();

        final PointGeometry pointGeometry = new PointGeometry(23, 67);
        assertEquals(Geometry.FALSE, multi.getEquals(pointGeometry));

        final PolygonGeometry polygonGeometry = (PolygonGeometry) parser.parseWKT("POLYGON((1 1,2 1,2 2,1 2,1 1))");
        assertEquals(Geometry.FALSE, multi.getEquals(polygonGeometry));
    }

    public void testGetEquals_ReturnsTrueForCorrectComparision() throws ParseException {
        prepareMultiPolygon();

        final MultiPolygonGeometry toCompare = new MultiPolygonGeometry();
        final PolygonGeometry polygon_1 = (PolygonGeometry) parser.parseWKT("POLYGON((0 0,1 0,1 1,0 1,0 0))");
        toCompare.addPolygon(polygon_1);
        final PolygonGeometry polygon_2 = (PolygonGeometry) parser.parseWKT("POLYGON((1 1,2 1,2 2,1 2,1 1))");
        toCompare.addPolygon(polygon_2);

        assertEquals(Geometry.TRUE, multi.getEquals(toCompare));
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private GeometryParser parser;
    private MultiPolygonGeometry multi;

    protected void setUp() throws Exception {
        parser = new GeometryParser();
        multi = new MultiPolygonGeometry();
    }

    protected void tearDown() throws Exception {
        parser = null;
    }

    private void prepareMultiPolygon() throws ParseException {
        PolygonGeometry polygon_1 = (PolygonGeometry) parser.parseWKT("POLYGON((0 0,1 0,1 1,0 1,0 0))");
        multi.addPolygon(polygon_1);
        PolygonGeometry polygon_2 = (PolygonGeometry) parser.parseWKT("POLYGON((1 1,2 1,2 2,1 2,1 1))");
        multi.addPolygon(polygon_2);
    }
}
