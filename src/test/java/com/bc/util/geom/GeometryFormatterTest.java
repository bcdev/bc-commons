/*
 * $Id: GeometryFormatterTest.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import junit.framework.TestCase;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.text.ParseException;

public class GeometryFormatterTest extends TestCase {

    private GeometryFormatter formatter;

    public GeometryFormatterTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        formatter = new GeometryFormatter();
    }

    protected void tearDown() throws Exception {
        formatter = null;
    }

    public void testPointFormatting() throws ParseException {
        assertEquals("POINT(4.3 5.8)", formatter.format(4.3, 5.8));

        PointGeometry pg = new PointGeometry(0.1, -0.2);
        assertEquals("POINT(0.1 -0.2)", formatter.format(pg));
        assertEquals("POINT(0.1 -0.2)", pg.toString());

        pg = new PointGeometry(-3.0, -0.132);
        assertEquals("POINT(-3 -0.132)", formatter.format(pg));
        assertEquals("POINT(-3 -0.132)", pg.toString());

        assertEquals("POINT(2.5 13.5)", formatter.format(new Point2D.Float(2.5f, 13.5f)));
    }

    public void testLineStringFormatting() throws ParseException {
        LineStringGeometry g;
        GeneralPath gp;

        gp = new GeneralPath();
        gp.moveTo(1f, 2f);
        gp.lineTo(3f, 4.3f);
        gp.lineTo(-.6f, 5f);
        g = new LineStringGeometry(gp);
        assertEquals("LINESTRING(1 2,3 4.3,-0.6 5)", formatter.format(g));
        assertEquals("LINESTRING(1 2,3 4.3,-0.6 5)", g.toString());
    }

    public void testPolygonFormatting() throws ParseException {
        PolygonGeometry g;
        GeneralPath gp;

        gp = new GeneralPath();
        gp.moveTo(1f, 2f);
        gp.lineTo(3f, 4f);
        gp.lineTo(5f, 6f);
        gp.closePath();
        g = new PolygonGeometry(gp);
        assertEquals("POLYGON((1 2,3 4,5 6,1 2))", formatter.format(g));
        assertEquals("POLYGON((1 2,3 4,5 6,1 2))", g.toString());

        gp = new GeneralPath();
        gp.moveTo(1f, 2f);
        gp.lineTo(3f, 4f);
        gp.lineTo(5f, 6f);
        gp.lineTo(1f, 2f);
        gp.closePath();
        g = new PolygonGeometry(gp);
        assertEquals("POLYGON((1 2,3 4,5 6,1 2))", formatter.format(g));
        assertEquals("POLYGON((1 2,3 4,5 6,1 2))", g.toString());

        gp = new GeneralPath();
        gp.moveTo(1f, 2f);
        gp.lineTo(3f, 4f);
        gp.lineTo(5f, 6f);
        gp.closePath();
        gp.moveTo(7f, 8f);
        gp.lineTo(9f, 10f);
        gp.lineTo(11f, -12.21f);
        gp.closePath();
        g = new PolygonGeometry(gp);
        assertEquals("POLYGON((1 2,3 4,5 6,1 2),(7 8,9 10,11 -12.21,7 8))", formatter.format(g));
        assertEquals("POLYGON((1 2,3 4,5 6,1 2),(7 8,9 10,11 -12.21,7 8))", g.toString());
    }

    public void testMultiLineStringGeometryFormatting() {
        final MultiLineStringGeometry mlsg = new MultiLineStringGeometry();
        GeneralPath gp = new GeneralPath();
        gp.moveTo(3.2f, 4.7f);
        gp.lineTo(5, 8);
        gp.lineTo(6, 7);
        mlsg.addLineString(new LineStringGeometry(gp));

        gp = new GeneralPath();
        gp.moveTo(1.f, 2f);
        gp.lineTo(3, 4);
        gp.lineTo(5, 6);
        mlsg.addLineString(new LineStringGeometry(gp));

        assertEquals("MULTILINESTRING((3.2 4.7,5 8,6 7),(1 2,3 4,5 6))", formatter.format(mlsg));
        assertEquals("MULTILINESTRING((3.2 4.7,5 8,6 7),(1 2,3 4,5 6))", mlsg.toString());
    }

    public void testMultiPointGeometryFormatting() {
        final MultiPointGeometry mpg = new MultiPointGeometry();
        mpg.addPoint(new PointGeometry(2.3, 4.5));
        mpg.addPoint(new PointGeometry(-3.0, 1.9));

        assertEquals("MULTIPOINT((2.3 4.5),(-3 1.9))", formatter.format(mpg));
        assertEquals("MULTIPOINT((2.3 4.5),(-3 1.9))", mpg.toString());
    }

    public void testMultiPolygonGeometryFormatting() {
        final MultiPolygonGeometry mpgg = new MultiPolygonGeometry();
        GeneralPath shape = new GeneralPath();
        shape.moveTo(3, 4);
        shape.lineTo(5, 4);
        shape.lineTo(5, 7);
        shape.lineTo(3, 7);
        shape.closePath();
        mpgg.addPolygon(new PolygonGeometry(shape));

        shape = new GeneralPath();
        shape.moveTo(1, 2);
        shape.lineTo(3, 4);
        shape.lineTo(5, 6);
        shape.lineTo(7, 8);
        shape.closePath();
        mpgg.addPolygon(new PolygonGeometry(shape));

        assertEquals("MULTIPOLYGON(((3 4,5 4,5 7,3 7,3 4)),((1 2,3 4,5 6,7 8,1 2)))", formatter.format(mpgg));
        assertEquals("MULTIPOLYGON(((3 4,5 4,5 7,3 7,3 4)),((1 2,3 4,5 6,7 8,1 2)))", mpgg.toString());
    }

    public void testGeometryCollectionFormatting() {
        final GeometryCollection g = new GeometryCollection();
        try {
            formatter.format(g);
            fail("IllegalStateException expected because this is not Implemented");
        } catch (IllegalStateException expected) {
        }
        try {
            g.toString();
            fail("IllegalStateException expected because this is not Implemented");
        } catch (IllegalStateException expected) {
        }
    }

    public void testShapeGeometryFormatting() {
        final GeneralPath gp = new GeneralPath();
        gp.moveTo(4, 5);
        gp.lineTo(6, 7);
        gp.lineTo(8, 3);
        gp.lineTo(2, 1);
        gp.closePath();
        assertEquals("POLYGON((4 5,6 7,8 3,2 1,4 5))", formatter.formatPolygon(gp));
    }
}

