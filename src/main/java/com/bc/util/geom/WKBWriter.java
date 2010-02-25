/*
 * $Id: WKBWriter.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
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

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;


/**
 * Writes a {@link Geometry} into Well-Known Binary format.
 * <p/>
 * The WKB format is specified in the OGC Simple Features for SQL specification.
 * <p/>
 * Empty Points cannot be represented in WKB; an
 * {@link IllegalArgumentException} will be thrown if one is written.
 * <p/>
 * This class is designed to support reuse of a single instance to read multiple
 * geometries. This class is not thread-safe; each thread should create its own
 * instance.
 *
 * @see WKBReader
 */
public class WKBWriter {

    private ByteArrayOutputStream byteArrayOS;

    // holds output data values
    private ByteBuffer bBuffer;

    /**
     * Creates a writer that writes {@link Geometry}s in BIG_ENDIAN byte order
     */
    public WKBWriter() {
        this(ByteOrder.BIG_ENDIAN);
    }

    /**
     * Creates a writer that writes {@link Geometry}s with the given byte order
     *
     * @param byteOrder the byte ordering to use
     */
    public WKBWriter(ByteOrder byteOrder) {
        bBuffer = ByteBuffer.allocate(2 * 8);
        bBuffer.order(byteOrder);
        byteArrayOS = new ByteArrayOutputStream();
    }

    /**
     * Writes a {@link Geometry} into a byte array.
     *
     * @param geom the geometry to write
     *
     * @return the byte array containing the WKB
     */
    public byte[] write(Geometry geom) {
        try {
            byteArrayOS.reset();
            write(geom, byteArrayOS);
        } catch (IOException ex) {
            throw new RuntimeException("Unexpected IO exception: "
                                       + ex.getMessage());
        }
        return byteArrayOS.toByteArray();
    }

    /**
     * Writes a {@link Geometry} to an {@link OutputStream}.
     *
     * @param geom the geometry to write
     * @param os   the out stream to write to
     *
     * @throws IOException if an I/O error occurs
     */
    public void write(Geometry geom, OutputStream os) throws IOException {
        if (geom instanceof PointGeometry) {
            writePoint((PointGeometry) geom, os);
        } else if (geom instanceof LineStringGeometry) {
            writeLineString((LineStringGeometry) geom, os);
        } else if (geom instanceof PolygonGeometry) {
            writePolygon((PolygonGeometry) geom, os);
        } else if (geom instanceof MultiPointGeometry) {
            writeMultiPoint((MultiPointGeometry) geom, os);
        } else if (geom instanceof MultiLineStringGeometry) {
            writeMultiLineString((MultiLineStringGeometry) geom, os);
        } else if (geom instanceof MultiPolygonGeometry) {
            writeMultiPolygon((MultiPolygonGeometry) geom, os);
        }
        // else if (geom instanceof GeometryCollection)
        // writeGeometryCollection(WKBConstants.GEOMETRYCOLLECTION,
        // (GeometryCollection) geom, os);
        else {
            // Assert.shouldNeverReachHere("Unknown Geometry type");
        }
    }

    private void writePoint(PointGeometry pt, OutputStream os) throws IOException {
        writeByteOrder(os);
        writeGeometryType(WKBConstants.POINT, os);
        writePoint2D(pt.getPoint(), os);
    }

    private void writeLineString(LineStringGeometry line, OutputStream os)
            throws IOException {
        writeByteOrder(os);
        writeGeometryType(WKBConstants.LINESTRING, os);
        List<List<Point2D>> pointListList = extractPoints(line.getAsShape());
        if (pointListList.size() > 1) {
            throw new IllegalStateException(
                    "LineString must only contain one startingpoint");
        }
        List<Point2D> pointList = pointListList.get(0);
        writeInt(pointList.size(), os);
        writePointList(pointList, os);
    }

    private void writePolygon(PolygonGeometry poly, OutputStream os)
            throws IOException {
        writeByteOrder(os);
        writeGeometryType(WKBConstants.POLYGON, os);
        List<List<Point2D>> pointListList = extractPoints(poly.getAsShape());
        writeInt(pointListList.size(), os);
        for (List<Point2D> pointList : pointListList) {
            writeInt(pointList.size(), os);
            writePointList(pointList, os);
        }
    }

    private void writeMultiPoint(MultiPointGeometry multiPointGeometry,
                                 OutputStream os) throws IOException {
        writeByteOrder(os);
        writeGeometryType(WKBConstants.MULTIPOINT, os);
        writeInt(multiPointGeometry.getPointCount(), os);
        for (int i = 0; i < multiPointGeometry.getPointCount(); i++) {
            writePoint(multiPointGeometry.getPoint(i), os);
        }
    }

    private void writeMultiLineString(
            MultiLineStringGeometry multiLineStringGeometry, OutputStream os)
            throws IOException {
        writeByteOrder(os);
        writeGeometryType(WKBConstants.MULTILINESTRING, os);
        writeInt(multiLineStringGeometry.getLineStringCount(), os);
        for (int i = 0; i < multiLineStringGeometry.getLineStringCount(); i++) {
            writeLineString(multiLineStringGeometry.getLineString(i), os);
        }
    }

    private void writeMultiPolygon(MultiPolygonGeometry multiPolygonGeometry,
                                   OutputStream os) throws IOException {
        writeByteOrder(os);
        writeGeometryType(WKBConstants.MULTIPOLYGON, os);
        writeInt(multiPolygonGeometry.getPolygonCount(), os);
        for (int i = 0; i < multiPolygonGeometry.getPolygonCount(); i++) {
            writePolygon(multiPolygonGeometry.getPolygon(i), os);
        }
    }

//	private void writeGeometryCollection(int geometryType,
//			GeometryCollection gc, OutStream os) throws IOException {
//		writeByteOrder(os);
//		writeGeometryType(geometryType, os);
//		writeInt(gc.getGeometryCount(), os);
//		for (int i = 0; i < gc.getGeometryCount(); i++) {
//			write(gc.getGeometry(i), os);
//		}
//	}

    private void writeByteOrder(OutputStream os) throws IOException {
        int byteOderValue;
        if (bBuffer.order() == ByteOrder.LITTLE_ENDIAN) {
            byteOderValue = WKBConstants.NDR;
        } else {
            byteOderValue = WKBConstants.XDR;
        }
        os.write(byteOderValue);
    }

    private void writeGeometryType(int geometryType, OutputStream os)
            throws IOException {
        writeInt(geometryType, os);
    }

    private void writeInt(int intValue, OutputStream os) throws IOException {
        bBuffer.rewind();
        bBuffer.putInt(intValue);
        os.write(bBuffer.array(), 0, 4);
    }

    private List<List<Point2D>> extractPoints(Shape shape) {
        float[] coords1 = new float[6];
        float x0 = 0, y0 = 0;
        float xi = 0, yi = 0;
        List<Point2D> listOfPoints = new ArrayList<Point2D>();
        List<List<Point2D>> listOfListOfPoints = new ArrayList<List<Point2D>>();
        final PathIterator pathIterator = shape.getPathIterator(null);
        int openCount = 0;
        while (!pathIterator.isDone()) {
            final int type = pathIterator.currentSegment(coords1);
            if (type == PathIterator.SEG_MOVETO) {
                x0 = coords1[0];
                y0 = coords1[1];
                if (openCount > 0) {
                    listOfListOfPoints.add(listOfPoints);
                    listOfPoints = new ArrayList<Point2D>();
                    openCount--;
                }
                listOfPoints.add(new Point2D.Float(x0, y0));
                openCount++;
            } else if (type == PathIterator.SEG_LINETO) {
                xi = coords1[0];
                yi = coords1[1];
                listOfPoints.add(new Point2D.Float(xi, yi));
            } else if (type == PathIterator.SEG_CLOSE) {
                openCount--;
                if (x0 != xi || y0 != yi) {
                    listOfPoints.add(new Point2D.Float(x0, y0));
                }
                listOfListOfPoints.add(listOfPoints);
                listOfPoints = new ArrayList<Point2D>();
            } else {
                throw new IllegalStateException("unexpected segment type: "
                                                + type);
            }
            pathIterator.next();
        }
        if (openCount > 0 && listOfPoints.size() != 0) {
            listOfListOfPoints.add(listOfPoints);
            openCount--;
        }
        if (openCount != 0) {
            throw new IllegalStateException("strange path");
        }
        return listOfListOfPoints;
    }

    private void writePointList(List<Point2D> pointlist, OutputStream os)
            throws IOException {
        for (Point2D point2D : pointlist) {
            writePoint2D(point2D, os);
        }
    }

    private void writePoint2D(Point2D point, OutputStream os) throws IOException {
        bBuffer.rewind();
        bBuffer.putDouble(point.getX());
        bBuffer.putDouble(point.getY());
        os.write(bBuffer.array(), 0, 16);
    }
}