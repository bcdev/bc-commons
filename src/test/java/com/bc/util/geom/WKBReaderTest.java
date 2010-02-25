/*
 * $Id: WKBReaderTest.java,v 1.1 2007-02-27 12:45:31 eugen Exp $
 *
 * Copyright (C) 2006 by Brockmann Consult (info@brockmann-consult.de)
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

import java.awt.geom.GeneralPath;

/**
 * Created by marcoz.
 *
 * @author marcoz
 * @version $Revision: 1.1 $ $Date: 2007-02-27 12:45:31 $
 */
public class WKBReaderTest extends WKBTestCase {

	private PointGeometry point;
	private WKBReader reader;

	protected void setUp() throws Exception {
		reader = new WKBReader();
		point = new PointGeometry(1, 1);
	}

	public void testReadLittleEndianPoint() throws Exception {
		byte[] wkb = hexToBytes("0101000000000000000000F03F000000000000F03F");
		Geometry geometry = reader.read(wkb);
		assertEquals(point, geometry);
	}

	public void testReadBigEndianPoint() throws Exception {
		byte[] wkb = hexToBytes("00000000013FF00000000000003FF0000000000000");
		Geometry geometry = reader.read(wkb);
		assertEquals(point, geometry);
	}

	public void testReadLineString() throws Exception {
		GeneralPath gp = new GeneralPath();
		gp.moveTo(1, 2);
		gp.lineTo(3, 4);
		gp.lineTo(5, 6);
		LineStringGeometry lineStringGeometry = new LineStringGeometry(gp);

		byte[] wkb = hexToBytes("0000000002000000033FF000000000000040000000000000004008000000000000401000000000000040140000000000004018000000000000");
		Geometry geometry = reader.read(wkb);
		assertEquals(lineStringGeometry, geometry);
	}

	public void testReadPolygon() throws Exception {
		GeneralPath gp = new GeneralPath();
		gp.moveTo(1, 2);
		gp.lineTo(3, 4);
		gp.lineTo(5, 6);
		gp.closePath();
		PolygonGeometry polygonGeometry = new PolygonGeometry(gp);
		byte[] wkb = hexToBytes("000000000300000001000000043FF0000000000000400000000000000040080000000000004010000000000000401400000000000040180000000000003FF00000000000004000000000000000");
		Geometry geometry = reader.read(wkb);
		assertEquals(polygonGeometry, geometry);

		gp = new GeneralPath();
		gp.moveTo(1f, 2f);
		gp.lineTo(3f, 4f);
		gp.lineTo(5f, 6f);
		gp.lineTo(1f, 2f);
		gp.closePath();
		polygonGeometry = new PolygonGeometry(gp);
		wkb = hexToBytes("000000000300000001000000043FF0000000000000400000000000000040080000000000004010000000000000401400000000000040180000000000003FF00000000000004000000000000000");
		geometry = reader.read(wkb);
		assertEquals(polygonGeometry, geometry);

		gp = new GeneralPath();
		gp.moveTo(1f, 2f);
		gp.lineTo(3f, 4f);
		gp.lineTo(5f, 6f);
		gp.closePath();
		gp.moveTo(7f, 8f);
		gp.lineTo(9f, 10f);
		gp.lineTo(11f, -12.21f);
		gp.closePath();
		polygonGeometry = new PolygonGeometry(gp);
		wkb = hexToBytes("000000000300000002000000043FF0000000000000400000000000000040080000000000004010000000000000401400000000000040180000000000003FF0000000000000400000000000000000000004401C0000000000004020000000000000402200000000000040240000000000004026000000000000C0286B8520000000401C0000000000004020000000000000");
		geometry = reader.read(wkb);
		assertEquals(polygonGeometry, geometry);
	}

	public void testReadMultiPoint() throws Exception {
		final MultiPointGeometry mpg = new MultiPointGeometry();
		mpg.addPoint(new PointGeometry(2, 4));
		mpg.addPoint(new PointGeometry(6, 8));

		byte[] wkb = hexToBytes("000000000400000002000000000140000000000000004010000000000000000000000140180000000000004020000000000000");
		Geometry geometry = reader.read(wkb);
		assertEquals(mpg, geometry);
	}

	public void testReadMultiLineString() throws Exception {
		final MultiLineStringGeometry mlsg = new MultiLineStringGeometry();
		GeneralPath gp = new GeneralPath();
		gp.moveTo(3, 4);
		gp.lineTo(5, 8);
		gp.lineTo(6, 7);
		mlsg.addLineString(new LineStringGeometry(gp));

		gp = new GeneralPath();
		gp.moveTo(1, 2);
		gp.lineTo(3, 4);
		gp.lineTo(5, 6);
		mlsg.addLineString(new LineStringGeometry(gp));

		byte[] wkb = hexToBytes("00000000050000000200000000020000000340080000000000004010000000000000401400000000000040200000000000004018000000000000401C0000000000000000000002000000033FF000000000000040000000000000004008000000000000401000000000000040140000000000004018000000000000");
		Geometry geometry = reader.read(wkb);
		assertEquals(mlsg, geometry);
	}

	public void testReadMultiPolygon() throws Exception {
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

		byte[] wkb = hexToBytes("0000000006000000020000000003000000010000000540080000000000004010000000000000401400000000000040100000000000004014000000000000401C0000000000004008000000000000401C00000000000040080000000000004010000000000000000000000300000001000000053FF000000000000040000000000000004008000000000000401000000000000040140000000000004018000000000000401C00000000000040200000000000003FF00000000000004000000000000000");
		Geometry geometry = reader.read(wkb);
		assertEquals(mpgg, geometry);
	}
}
