/*
 * $Id: WKBReader.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
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

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.bc.util.NotImplementedException;

/**
 * Reads a {@link Geometry}from a byte stream in Well-Known Binary format.
 * <p>
 * This class is designed to support reuse of a single instance to read multiple
 * geometries. This class is not thread-safe; each thread should create its own
 * instance.
 * 
 * @see WKBWriter
 */
public class WKBReader {

	private static final String INVALID_GEOM_TYPE_MSG = "Invalid geometry type encountered in ";

	private InputStream inputStream;

	private ByteBuffer bBuffer;

	public WKBReader() {
		bBuffer = ByteBuffer.allocate(2*8);
	}

	/**
	 * Reads a single {@link Geometry} from a byte array.
	 * 
	 * @param bytes
	 *            the byte array to read from
	 * @return the geometry read
	 * @throws IOException
	 *             if an input exception occurs
	 */
	public Geometry read(byte[] bytes) throws IOException {
		inputStream = new ByteArrayInputStream(bytes);
		return read(inputStream);
	}

	/**
	 * Reads a {@link Geometry} from an {@link InputStream).
	 * 
	 * @param is
	 *            the stream to read from
	 * @return the Geometry read
	 * @throws IOException
	 */
	public Geometry read(InputStream is) throws IOException {
		this.inputStream = is;
		return readGeometry();
	}

	private Geometry readGeometry() throws IOException {
		// determine byte order
		int byteOrder = inputStream.read();
		// default is big endian
		if (byteOrder == WKBConstants.NDR) {
			bBuffer.order(ByteOrder.LITTLE_ENDIAN);
		}

		int typeInt = readInt();
		int geometryType = typeInt & 0xff;

		switch (geometryType) {
		case WKBConstants.POINT:
			return readPoint();
		case WKBConstants.LINESTRING:
			return readLineString();
		case WKBConstants.POLYGON:
			return readPolygon();
		case WKBConstants.MULTIPOINT:
			return readMultiPoint();
		case WKBConstants.MULTILINESTRING:
			return readMultiLineString();
		case WKBConstants.MULTIPOLYGON:
			return readMultiPolygon();
		case WKBConstants.GEOMETRYCOLLECTION:
			return readGeometryCollection();
		}
		throw new InvalidClassException("Unknown WKB type " + geometryType);
	}

	private PointGeometry readPoint() throws IOException {
		Point2D point = readPoint2D();
		return new PointGeometry(point);
	}

	private LineStringGeometry readLineString() throws IOException {
		return new LineStringGeometry(readLinearRing());
	}

	private GeneralPath readLinearRing() throws IOException {
		final int size = readInt();
		GeneralPath linearRing = new GeneralPath();
		for (int i = 0; i < size; i++) {
			Point2D point = readPoint2D();
			if (i == 0) {
				linearRing.moveTo((float) point.getX(), (float) point.getY());
			} else {
				linearRing.lineTo((float) point.getX(), (float) point.getY());
			}
		}
		return linearRing;
	}

	private PolygonGeometry readPolygon() throws IOException {
		int numRings = readInt();
		GeneralPath polygon = new GeneralPath();
		for (int i = 0; i < numRings; i++) {
			GeneralPath ring = readLinearRing();
			polygon.append(ring, false);
		}
		return new PolygonGeometry(polygon);
	}

	private MultiPointGeometry readMultiPoint() throws IOException {
		final int numGeom = readInt();
		MultiPointGeometry multiPoint = new MultiPointGeometry();
		for (int i = 0; i < numGeom; i++) {
			Geometry geometry = readGeometry();
			if (geometry instanceof PointGeometry) {
				multiPoint.addPoint((PointGeometry) geometry);
			} else {
				throw new InvalidClassException(INVALID_GEOM_TYPE_MSG + "MultiPoint");
			}
		}
		return multiPoint;
	}

	private MultiLineStringGeometry readMultiLineString() throws IOException {
		final int numGeom = readInt();
		MultiLineStringGeometry multiLineString = new MultiLineStringGeometry();
		for (int i = 0; i < numGeom; i++) {
			Geometry geometry = readGeometry();
			if (geometry instanceof LineStringGeometry) {
				multiLineString.addLineString((LineStringGeometry) geometry);
			} else {
				throw new InvalidClassException(INVALID_GEOM_TYPE_MSG
						+ "MultiLineString");
			}
		}
		return multiLineString;
	}

	private MultiPolygonGeometry readMultiPolygon() throws IOException {
		final int numGeom = readInt();
		MultiPolygonGeometry multiPolygon = new MultiPolygonGeometry();
		for (int i = 0; i < numGeom; i++) {
			Geometry geometry = readGeometry();
			if (geometry instanceof PolygonGeometry) {
				multiPolygon.addPolygon((PolygonGeometry) geometry);
			} else {
				throw new InvalidClassException(INVALID_GEOM_TYPE_MSG
						+ "MultiPolygon");
			}
		}
		return multiPolygon;
	}

	private GeometryCollection readGeometryCollection() throws IOException {
		throw new NotImplementedException();
	}

	private Point2D readPoint2D() throws IOException {
		bBuffer.rewind();
		inputStream.read(bBuffer.array(), 0, 2 * 8);
		double x = bBuffer.getDouble();
		double y = bBuffer.getDouble();
		return new Point2D.Double(x, y);
	}

	private int readInt() throws IOException {
		bBuffer.rewind();
		inputStream.read(bBuffer.array(), 0, 4);
		return bBuffer.getInt();
	}

}