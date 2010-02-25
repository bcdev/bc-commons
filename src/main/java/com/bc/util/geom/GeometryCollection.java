/*
 * $Id: GeometryCollection.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import com.bc.util.NotImplementedException;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class GeometryCollection extends AbstractGeometry {

    public GeometryCollection() {
        geometries = new ArrayList<Geometry>();
    }

    public PointGeometry getCenterPoint() {
        final Rectangle2D bounds2D = getAsShape().getBounds2D();
        return new PointGeometry(bounds2D.getCenterX(), bounds2D.getCenterY());
    }

    public void addGeometry(Geometry p) {
        geometries.add(p);
    }

    public void removeGeometry(Geometry p) {
        geometries.remove(p);
    }

    public Geometry getGeometry(int i) {
        return geometries.get(i);
    }

    public int getGeometryCount() {
        return geometries.size();
    }

    public int getDimension() {
        int dim = 0;
        for (int i = 0; i < getGeometryCount(); i++) {
            final Geometry geometry = getGeometry(i);
            if (i == 0) {
                dim = geometry.getDimension();
            } else {
                dim = Math.max(dim, geometry.getDimension());
            }
        }
        return dim;
    }

    public String getGeometryType() {
        return GEOMETRYCOLLECTION;
    }

    public Shape getAsShape() {
        Area a = null;
        for (int i = 0; i < getGeometryCount(); i++) {
            final Geometry geometry = getGeometry(i);
            final Shape shape = geometry.getAsShape();
            if (shape != null) {
                if (a == null) {
                    a = new Area();
                }
                a.add(new Area(shape));
            }
        }
        return a;
    }

    public String getAsText() {
        return new GeometryFormatter().format(this);
    }

    public int getEquals(Geometry g) {
        if (g == this) {
            return TRUE;
        } else if (g == null) {
            return UNKNOWN;
        } else if (g instanceof GeometryCollection) {
            final GeometryCollection gc = (GeometryCollection) g;
            if (gc.getGeometryCount() != getGeometryCount()) {
                return FALSE;
            }
            for (int i = 0; i < gc.getGeometryCount(); i++) {
                if (gc.getGeometry(i).getEquals(getGeometry(i)) != TRUE) {
                    return FALSE;
                }
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

    ////////////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////////

    private List<Geometry> geometries;
}
