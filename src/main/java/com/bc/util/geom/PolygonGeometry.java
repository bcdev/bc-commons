/*
 * $Id: PolygonGeometry.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class PolygonGeometry extends ShapeGeometry {

    public PolygonGeometry(Shape shape) {
        super(shape);
    }

    /**
     * Creates a new polygon geometry from an array of points.
     *
     * @param points an array of points, must not be null and the length must be &gt;= 6 and a multiple of 2, x is on
     *               even positions, y on odd
     *
     * @return the new polygon geometry
     */
    public static PolygonGeometry create(float[] points) {
        if (points == null) {
            throw new IllegalArgumentException("points is null");
        }
        if (points.length < 6 || points.length % 2 != 0) {
            throw new IllegalArgumentException("illegal number of points");
        }
        GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, points.length / 2);
        for (int i = 0; i < points.length; i += 2) {
            float x = points[i];
            float y = points[i + 1];
            if (i == 0) {
                generalPath.moveTo(x, y);
            } else {
                generalPath.lineTo(x, y);
            }
        }
        generalPath.closePath();
        return new PolygonGeometry(generalPath);
    }

    /**
     * Creates a new polygon geometry from an array of points.
     *
     * @param points an array of points, must not be null and the length must be &gt;= 6 and a multiple of 2, x is on
     *               even positions, y on odd
     *
     * @return the new polygon geometry
     */
    public static PolygonGeometry create(double[] points) {
        if (points == null) {
            throw new IllegalArgumentException("points is null");
        }
        if (points.length < 6 || points.length % 2 != 0) {
            throw new IllegalArgumentException("illegal number of points");
        }
        GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, points.length / 2);
        for (int i = 0; i < points.length; i += 2) {
            float x = (float) points[i];
            float y = (float) points[i + 1];
            if (i == 0) {
                generalPath.moveTo(x, y);
            } else {
                generalPath.lineTo(x, y);
            }
        }
        generalPath.closePath();
        return new PolygonGeometry(generalPath);
    }

    public int getDimension() {
        return 2;
    }

    public String getGeometryType() {
        return POLYGON;
    }

    public String getAsText() {
        return new GeometryFormatter().format(this);
    }

    public boolean equals(Object obj) {
        if (obj instanceof PolygonGeometry) {
            return getEquals((PolygonGeometry) obj) == TRUE;
        }
        return false;
    }
}
