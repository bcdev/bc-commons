/*
 * $Id: PointGeometryTest.java,v 1.1 2007-02-27 12:45:31 eugen Exp $
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

import java.awt.Point;
import java.awt.Rectangle;

public class PointGeometryTest extends TestCase {

    public void testDoublesConstructor() {
        final PointGeometry pg = new PointGeometry(3, 4);
        assertEquals(3.0, pg.getX(), 1e-10);
        assertEquals(4.0, pg.getY(), 1e-10);
    }

    public void testPointConstructor() {
        final PointGeometry pg = new PointGeometry(new Point(3, 4));
        assertEquals(3.0, pg.getX(), 1e-10);
        assertEquals(4.0, pg.getY(), 1e-10);
    }

    public void testDisjointAndIntersects() {
        final PointGeometry p = new PointGeometry(5.0, 6);
        final PointGeometry pDisjoint = new PointGeometry(5.00002, 6);
        final PointGeometry pIntersects = new PointGeometry(5.000005, 6);

        assertEquals(Geometry.UNKNOWN, p.getDisjoint(null));
        assertEquals(Geometry.TRUE, p.getDisjoint(pDisjoint));
        assertEquals(Geometry.FALSE, p.getDisjoint(pIntersects));

        assertEquals(Geometry.UNKNOWN, p.getIntersects(null));
        assertEquals(Geometry.TRUE, p.getIntersects(pIntersects));
        assertEquals(Geometry.FALSE, p.getIntersects(pDisjoint));
    }

    public void testEqual() {
        final PointGeometry p = new PointGeometry(4, 5);

        assertEquals(Geometry.UNKNOWN, p.getEquals(null));
        assertEquals(Geometry.TRUE, p.getEquals(new PointGeometry(4, 5)));
        assertEquals(Geometry.FALSE, p.getEquals(new PointGeometry(4.1, 5)));
        assertEquals(Geometry.TRUE, p.getEquals(new PointGeometry(4.0000001, 5)));
        assertEquals(Geometry.FALSE, p.getEquals(new PolygonGeometry(new Rectangle(243, 654))));

    }

    public void testGetCenterPoint() {
        final PointGeometry p = new PointGeometry(6.2, 13.4);

        final PointGeometry centerPoint = p.getCenterPoint();
        assertNotSame(p, centerPoint);
        assertEquals(p.getX(), centerPoint.getX());
        assertEquals(p.getY(), centerPoint.getY());
    }
}
