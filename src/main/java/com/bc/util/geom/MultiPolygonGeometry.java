/*
 * $Id: MultiPolygonGeometry.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import java.awt.Shape;

public class MultiPolygonGeometry extends AbstractGeometry {

    private GeometryCollection polygons;

    public MultiPolygonGeometry() {
        polygons = new GeometryCollection();
    }

    public PointGeometry getCenterPoint() {
        return polygons.getCenterPoint();
    }

    public void addPolygon(PolygonGeometry p) {
        polygons.addGeometry(p);
    }

    public void removePolygon(PolygonGeometry p) {
        polygons.removeGeometry(p);
    }

    public PolygonGeometry getPolygon(int i) {
        return (PolygonGeometry) polygons.getGeometry(i);
    }

    public int getPolygonCount() {
        return polygons.getGeometryCount();
    }

    public int getDimension() {
        return 2;
    }

    public String getGeometryType() {
        return MULTIPOLYGON;
    }

    public Shape getAsShape() {
        return polygons.getAsShape();
    }

    public String getAsText() {
        return new GeometryFormatter().format(this);
    }

    @SuppressWarnings({"InstanceofInterfaces"})
    public int getEquals(Geometry g) {
        if (g == this) {
            return TRUE;
        } else if (g == null) {
            return UNKNOWN;
        } else if (g instanceof MultiPolygonGeometry) {
            MultiPolygonGeometry mp = (MultiPolygonGeometry) g;
            if (mp.polygons.getEquals(polygons) != TRUE) {
                return FALSE;
            }
            return TRUE;
        }
        return FALSE;
    }

    // @todo - 2 nf/nf write test for this
    public int getDisjoint(Geometry g) {
        PolygonGeometry polygon;
        int status;
        for (int i = 0; i < getPolygonCount(); i++) {
            polygon = getPolygon(i);
            status = polygon.getDisjoint(g);
            if (status == FALSE) {
                return FALSE;
            } else if (status == UNKNOWN) {
                return UNKNOWN;
            }
        }
        return TRUE;
    }

    // @todo - 2 nf/nf write test for this
    public int getContains(Geometry g) {
        PolygonGeometry polygon;
        int status;
        for (int i = 0; i < getPolygonCount(); i++) {
            polygon = getPolygon(i);
            status = polygon.getContains(g);
            if (status == TRUE) {
                return TRUE;
            } else if (status == UNKNOWN) {
                return UNKNOWN;
            }
        }
        return FALSE;
    }
}
