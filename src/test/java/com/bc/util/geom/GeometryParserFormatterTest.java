/*
 * $Id: GeometryParserFormatterTest.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
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

import java.awt.geom.GeneralPath;
import java.text.ParseException;

public class GeometryParserFormatterTest extends TestCase {

    private GeometryFormatter formatter;
    private GeometryParser parser;

    protected void setUp() throws Exception {
        formatter = new GeometryFormatter();
        parser = new GeometryParser();
    }

    protected void tearDown() throws Exception {
        formatter = null;
        parser = null;
    }

    public void testPointGeometry() throws ParseException {
        final PointGeometry point = new PointGeometry(23.5, 3.896);

        assertEquals("POINT(23.5 3.896)", formatter.format(point));
        assertEquals("POINT(23.5 3.896)", point.getAsText());

        final Geometry parsed = parser.parseWKT(point.getAsText());

        assertEquals("POINT(23.5 3.896)", parsed.getAsText());
        assertEquals("POINT(23.5 3.896)", formatter.format((PointGeometry) parsed));
    }

    public void testLineStringGeometry() throws ParseException {
        final GeneralPath gp = new GeneralPath();
        gp.moveTo(1, 1);
        gp.lineTo(5, 1);
        gp.lineTo(5, 5);
        gp.lineTo(1, 5);
        final LineStringGeometry line = new LineStringGeometry(gp);

        assertEquals("LINESTRING(1 1,5 1,5 5,1 5)", formatter.format(line));
        assertEquals("LINESTRING(1 1,5 1,5 5,1 5)", line.getAsText());

        final Geometry parsed = parser.parseWKT(line.getAsText());

        assertEquals("LINESTRING(1 1,5 1,5 5,1 5)", parsed.getAsText());
        assertEquals("LINESTRING(1 1,5 1,5 5,1 5)", formatter.format((LineStringGeometry) parsed));
    }

    public void testPolygonGeometry() throws ParseException {
        final GeneralPath gp = new GeneralPath();
        gp.moveTo(1, 1);
        gp.lineTo(5, 1);
        gp.lineTo(5, 5);
        gp.lineTo(1, 5);
        gp.closePath();
        final PolygonGeometry polygon = new PolygonGeometry(gp);

        assertEquals("POLYGON((1 1,5 1,5 5,1 5,1 1))", formatter.format(polygon));
        assertEquals("POLYGON((1 1,5 1,5 5,1 5,1 1))", polygon.getAsText());

        final Geometry parsed = parser.parseWKT(polygon.getAsText());

        assertEquals("POLYGON((1 1,5 1,5 5,1 5,1 1))", parsed.getAsText());
        assertEquals("POLYGON((1 1,5 1,5 5,1 5,1 1))", formatter.format((PolygonGeometry) parsed));
    }
}
