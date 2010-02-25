/*
 * $Id: GeometryFormatter.java,v 1.2 2008-11-10 08:36:37 sabine Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import com.bc.util.NotImplementedException;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public class GeometryFormatter {

    public GeometryFormatter() {
    }

    public String format(Point2D p) {
        return format(p.getX(), p.getY());
    }

    public String format(PointGeometry g) {
        return format(g.getX(), g.getY());
    }

    public String format(LineStringGeometry g) {
        return formatLineString(g.getAsShape());
    }

    public String format(PolygonGeometry g) {
        return formatPolygon(g.getAsShape());
    }

    public String format(MultiPointGeometry g) {
        StringBuffer sb = new StringBuffer(BUFFER_SIZE);
        append(sb, Geometry.MULTIPOINT);
        append(sb, "(");
        for (int i = 0; i < g.getPointCount(); i++) {
            final PointGeometry point = g.getPoint(i);
            appendPointBody(sb, point.getPoint().getX(), point.getPoint().getY());
            if (i != g.getPointCount() - 1) {
                append(sb, ",");
            }
        }
        append(sb, ")");
        return sb.toString();
    }

    public String format(MultiLineStringGeometry g) {
        StringBuffer sb = new StringBuffer(BIG_BUFFER_SIZE);
        append(sb, Geometry.MULTILINESTRING);
        append(sb, "(");
        for (int i = 0; i < g.getLineStringCount(); i++) {
            final LineStringGeometry lineString = g.getLineString(i);
            appendLineStringPath(sb, lineString.getAsShape(), true);
            if (i != g.getLineStringCount() - 1) {
                append(sb, ",");
            }
        }
        append(sb, ")");
        return sb.toString();
    }

    public String format(MultiPolygonGeometry g) {
        StringBuffer sb = new StringBuffer(BIG_BUFFER_SIZE);
        append(sb, Geometry.MULTIPOLYGON);
        append(sb, "(");
        for (int i = 0; i < g.getPolygonCount(); i++) {
            final PolygonGeometry pg = g.getPolygon(i);
            appendPolygonBody(sb, pg.getAsShape());
            if (i != g.getPolygonCount() - 1) {
                append(sb, ",");
            }
        }
        append(sb, ")");
        return sb.toString();
    }

    public String format(GeometryCollection g) {
        throw new NotImplementedException();
    }

    public String format(double x, double y) {
        //noinspection MagicNumber
        StringBuffer sb = new StringBuffer(32);
        append(sb, Geometry.POINT);
        appendPointBody(sb, x, y);
        return sb.toString();
    }

    public String formatLineString(Shape s) {
        StringBuffer sb = new StringBuffer(BUFFER_SIZE);
        append(sb, Geometry.LINESTRING);
        appendLineStringPath(sb, s, true);
        return sb.toString();
    }

    public String formatPolygon(Shape s) {
        StringBuffer sb = new StringBuffer(BUFFER_SIZE);
        append(sb, Geometry.POLYGON);
        appendPolygonBody(sb, s);
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private static final int BUFFER_SIZE = 1024;
    private static final int BIG_BUFFER_SIZE = 2048;

    private void appendLineStringPath(StringBuffer sb, Shape s, boolean single) {
        float[] coords1 = new float[6];
        float x0 = 0, y0 = 0;
        float xi = 0, yi = 0;
        final PathIterator pathIterator = s.getPathIterator(null);
        int openCountTotal = 0;
        int openCount = 0;
        while (!pathIterator.isDone()) {
            final int type = pathIterator.currentSegment(coords1);
            if (type == PathIterator.SEG_MOVETO) {
                x0 = coords1[0];
                y0 = coords1[1];
                if (openCount > 0) {
                    append(sb, ")");
                    openCount--;
                }
                openCount++;
                openCountTotal++;
                if (openCountTotal > 1) {
                    if (single) {
                        throw new IllegalStateException("unexpected segment type: " + type);
                    }
                    append(sb, ",");
                }
                append(sb, "(");
                append(sb, x0, y0);
            } else if (type == PathIterator.SEG_LINETO) {
                xi = coords1[0];
                yi = coords1[1];
                append(sb, ",");
                append(sb, xi, yi);
            } else if (type == PathIterator.SEG_CLOSE) {
                openCount--;
                if (x0 != xi || y0 != yi) {
                    append(sb, ",");
                    append(sb, x0, y0);
                }
                append(sb, ")");
            } else {
                throw new IllegalStateException("unexpected segment type: " + type);
            }
            pathIterator.next();
        }
        if (openCount > 0) {
            append(sb, ")");
            openCount--;
        }
        if (openCount != 0) {
            throw new IllegalStateException("strange path");
        }
    }

    private void append(StringBuffer sb, float x, float y) {
        append(sb, x);
        append(sb, " ");
        append(sb, y);
    }

    private void append(StringBuffer sb, double x, double y) {
        append(sb, x);
        append(sb, " ");
        append(sb, y);
    }

    private void append(StringBuffer sb, String s) {
        sb.append(s);
    }

    private void append(StringBuffer sb, double d) {
        if (Math.floor(d) == d) {
            sb.append(Math.round(d));
        } else {
            sb.append(d);
        }
    }

    private void append(StringBuffer sb, float f) {
        if (Math.floor(f) == f) {
            sb.append(Math.round(f));
        } else {
            sb.append(f);
        }
    }

    private void appendPointBody(StringBuffer sb, double x, double y) {
        append(sb, "(");
        append(sb, x, y);
        append(sb, ")");
    }

    private void appendPolygonBody(StringBuffer sb, Shape s) {
        append(sb, "(");
        appendLineStringPath(sb, s, false);
        append(sb, ")");
    }

}
