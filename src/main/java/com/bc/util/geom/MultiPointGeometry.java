/*
 * $Id: MultiPointGeometry.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import com.bc.util.NotImplementedException;

import java.awt.Shape;


public class MultiPointGeometry extends AbstractGeometry {

    private final GeometryCollection points;

    public MultiPointGeometry() {
        points = new GeometryCollection();
    }

    public PointGeometry getCenterPoint() {
        return points.getCenterPoint();
    }

    public Shape getAsShape() {
        return points.getAsShape();
    }

    public void addPoint(PointGeometry p) {
        points.addGeometry(p);
    }

    public void removePoint(PointGeometry p) {
        points.removeGeometry(p);
    }

    public PointGeometry getPoint(int i) {
        return (PointGeometry) points.getGeometry(i);
    }

    public int getPointCount() {
        return points.getGeometryCount();
    }

    public int getDimension() {
        return 1;
    }

    public String getGeometryType() {
        return MULTIPOINT;
    }

    public String getAsText() {
        return new GeometryFormatter().format(this);
    }

    public int getEquals(Geometry g) {
        if (g == this) {
            return TRUE;
        } else if (g == null) {
            return UNKNOWN;
        } else if (g instanceof MultiPointGeometry) {
            MultiPointGeometry mp = (MultiPointGeometry) g;
            if (mp.points.getEquals(points) != TRUE) {
                return FALSE;
            }
            return TRUE;
        }
        return FALSE;
    }

    public int getDisjoint(Geometry g) {
        throw new NotImplementedException();
    }

    public int getContains(Geometry g) {
        throw new NotImplementedException();
    }
}
