/*
 * $Id: LineStringGeometryTest.java,v 1.1 2007-02-27 12:45:31 eugen Exp $
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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.geom.GeneralPath;
import java.awt.geom.RectangularShape;

public class LineStringGeometryTest extends TestCase {

    public void testGetAsShapeReturnsAClone_ByUsingRectangleInput() {
        final Rectangle rectangle = new Rectangle(12, 134);
        final ShapeGeometry lineStringGeometry = new LineStringGeometry(rectangle);

        final Shape shape = lineStringGeometry.getAsShape();

        assertNotSame(rectangle, shape);
        assertEquals(true, shape instanceof RectangularShape);
    }

    public void testGetAsShapeReturnsAClone_ByUsingGeneralPathInput() {
        final GeneralPath gp = new GeneralPath();
        gp.moveTo(3, 4);
        gp.moveTo(5, 2);
        gp.moveTo(4, 5);
        gp.closePath();
        final ShapeGeometry lineStringGeometry = new LineStringGeometry(gp);

        final Shape shape = lineStringGeometry.getAsShape();

        assertNotSame(gp, shape);
        assertEquals(true, shape instanceof GeneralPath);
    }

    public void testGetAsShapeReturnsAClone_ByUsingPolygonInput() {
        final Polygon polygon = new Polygon();
        polygon.addPoint(3, 4);
        polygon.addPoint(5, 2);
        polygon.addPoint(4, 5);
        final ShapeGeometry lineStringGeometry = new LineStringGeometry(polygon);

        final Shape shape = lineStringGeometry.getAsShape();

        assertSame(polygon, shape);
    }
}
