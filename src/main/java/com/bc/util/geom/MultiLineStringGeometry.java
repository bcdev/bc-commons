/*
 * $Id: MultiLineStringGeometry.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import com.bc.util.NotImplementedException;

import java.awt.Shape;

public class MultiLineStringGeometry extends AbstractGeometry {

    private GeometryCollection lineStrings;

    public MultiLineStringGeometry() {
        lineStrings = new GeometryCollection();
    }

    public PointGeometry getCenterPoint() {
        return lineStrings.getCenterPoint();
    }

    public Shape getAsShape() {
        return lineStrings.getAsShape();
    }

    public void addLineString(LineStringGeometry p) {
        lineStrings.addGeometry(p);
    }

    public void removeLineString(LineStringGeometry p) {
        lineStrings.removeGeometry(p);
    }

    public LineStringGeometry getLineString(int i) {
        return (LineStringGeometry) lineStrings.getGeometry(i);
    }

    public int getLineStringCount() {
        return lineStrings.getGeometryCount();
    }

    public int getDimension() {
        return 1;
    }

    public String getGeometryType() {
        return MULTILINESTRING;
    }

    public String getAsText() {
        return new GeometryFormatter().format(this);
    }

    public int getEquals(Geometry g) {
        if (g == this) {
            return TRUE;
        } else if (g == null) {
            return UNKNOWN;
        } else if (g instanceof MultiLineStringGeometry) {
            MultiLineStringGeometry mls = (MultiLineStringGeometry) g;
            if (mls.lineStrings.getEquals(lineStrings) != TRUE) {
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
