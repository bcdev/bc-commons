/*
 * $Id: PolygonGeometryTest.java,v 1.1 2007-02-27 12:45:31 eugen Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.bc.util.geom;

import junit.framework.TestCase;

import java.text.ParseException;

public class PolygonGeometryTest extends TestCase {

    private GeometryParser parser;

    protected void setUp() throws Exception {
        parser = new GeometryParser();
    }

    protected void tearDown() throws Exception {
        parser = null;
    }

    public void testDisjointAndIntersects() throws ParseException {
        final Geometry poly = parser.parseWKT("POLYGON((1 1,5 1,5 3,3 3,3 5,1 5,1 1))");
        final Geometry polyIntersects = parser.parseWKT("POLYGON((4 1,6 1,6 3,4 3,4 1))");
        final Geometry polyDisjoint = parser.parseWKT("POLYGON((4 4,6 4,6 6,4 6,4 4))");

        assertEquals(Geometry.UNKNOWN, poly.getDisjoint(null));
        assertEquals(Geometry.TRUE, poly.getDisjoint(polyDisjoint));
        assertEquals(Geometry.FALSE, poly.getDisjoint(polyIntersects));

        assertEquals(Geometry.UNKNOWN, poly.getIntersects(null));
        assertEquals(Geometry.TRUE, poly.getIntersects(polyIntersects));
        assertEquals(Geometry.FALSE, poly.getIntersects(polyDisjoint));
    }

    public void testContainsAndWithin() throws ParseException {
        final String p1Wkt = "POLYGON((" +
                             "1 1," +
                             "5 1," +
                             "5 3," +
                             "3 3," +
                             "3 5," +
                             "1 5," +
                             "1 1))";
        final String p2Wkt = "POLYGON((" +
                             "1.1 1.1," +
                             "4.9 1.1," +
                             "4.9 2.9," +
                             "2.9 2.9," +
                             "2.9 4.9," +
                             "1.1 4.9," +
                             "1.1 1.1))";
        final String p3Wkt = "POLYGON((" +
                             "1.1 1.1," +
                             "4.9 1.1," +
                             "5.1 2.9," +
                             "2.9 2.9," +
                             "2.9 4.9," +
                             "1.1 4.9," +
                             "1.1 1.1))";
        final Geometry poly = parser.parseWKT(p1Wkt);
        final Geometry polyContained = parser.parseWKT(p2Wkt);
        final Geometry polyNotContained = parser.parseWKT(p3Wkt);

        assertEquals(Geometry.UNKNOWN, polyContained.getWithin(null));
        assertEquals(Geometry.TRUE, polyContained.getWithin(poly));
        assertEquals(Geometry.FALSE, polyNotContained.getWithin(poly));

        assertEquals(Geometry.UNKNOWN, poly.getContains(null));
        assertEquals(Geometry.TRUE, poly.getContains(polyContained));
        assertEquals(Geometry.FALSE, poly.getContains(polyNotContained));
    }

    public void testEqual() throws ParseException {
        final Geometry g__ = parser.parseWKT("POLYGON((1 1,5 1,5 3,3 3,3 5,1 5,1 1))");
        final Geometry gEQ = parser.parseWKT("POLYGON((5 1,5 3,3 3,3 5,1 5,1 1,5 1))");
        final Geometry gNE = parser.parseWKT("POLYGON((1 1,5 1,5 3,3 4,3 5,1 5,1 1))");

        assertEquals(Geometry.UNKNOWN, g__.getEquals(null));
        assertEquals(Geometry.TRUE, g__.getEquals(gEQ));
        assertEquals(Geometry.FALSE, g__.getEquals(gNE));
        assertEquals(Geometry.FALSE, g__.getEquals(new PointGeometry(23, 65)));
    }

    public void testGetCenterPoint() throws ParseException {
        final Geometry g = parser.parseWKT("POLYGON((1 1,6 1,6 3,4 5,1 5,1 1))");
//         _ _ _ _ _
//        |         |
//        |         |  = g
//        |        /
//        |_ _ _ /

        final PointGeometry centerPoint = g.getCenterPoint();
        assertNotNull(centerPoint);
        assertEquals(3.5, centerPoint.getX());
        assertEquals(3.0, centerPoint.getY());
    }
}
