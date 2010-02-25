/*
 * $Id: PointGeometry.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class PointGeometry extends AbstractGeometry {

    private Point2D point;
    private static final double R = 1.0e-5;
    public static final double EPS = 1.0e-5;

    public PointGeometry(double x, double y) {
        this(new Point2D.Double(x, y));
    }

    public PointGeometry(Point2D p) {
        point = p;
    }

    public PointGeometry getCenterPoint() {
        return new PointGeometry(getX(), getY());
    }

    public Point2D getPoint() {
        return point;
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    public int getDimension() {
        return 0;
    }

    public String getGeometryType() {
        return POINT;
    }

    public Shape getAsShape() {
        return new Ellipse2D.Double(getX() - R, getY() - R, 2 * R, 2 * R);
    }

    public String getAsText() {
        return new GeometryFormatter().format(this);
    }

    public int getEquals(Geometry g) {
        if (g == this) {
            return TRUE;
        } else if (g == null) {
            return UNKNOWN;
        } else if (g instanceof PointGeometry) {
            PointGeometry p = (PointGeometry) g;
            final double dx = getX() - p.getX();
            final double dy = getY() - p.getY();
            if (dx * dx + dy * dy > (EPS * EPS)) {
                return FALSE;
            }
            return TRUE;
        }
        return FALSE;
    }

    public int getDisjoint(Geometry g) {
        if (g == null) {
            return UNKNOWN;
        }
        if (g instanceof PointGeometry) {
            final int status = getEquals(g);
            if (status == TRUE) {
                return FALSE;
            } else if (status == FALSE) {
                return TRUE;
            }
            return status;
        } else {
            return g.getAsShape().contains(getX(), getY()) ? FALSE : TRUE;
        }
    }

    public int getContains(Geometry g) {
        // A point can not contain any other point
        return FALSE;
    }

    public boolean equals(Object obj) {
        if (obj instanceof PointGeometry) {
            return getEquals((PointGeometry) obj) == TRUE;
        }
        return false;
    }
}
