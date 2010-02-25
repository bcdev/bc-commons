/*
 * $Id: LineStringGeometry.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import com.bc.util.NotImplementedException;

import java.awt.Shape;

public class LineStringGeometry extends ShapeGeometry {

    public LineStringGeometry(Shape shape) {
        super(shape);
    }

    public int getDimension() {
        return 1;
    }

    public String getGeometryType() {
        return LINESTRING;
    }

    public String getAsText() {
        return new GeometryFormatter().format(this);
    }

    public int getDisjoint(Geometry g) {
        throw new NotImplementedException();
    }

    public int getContains(Geometry g) {
        throw new NotImplementedException();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LineStringGeometry) {
            return getEquals((LineStringGeometry) obj) == TRUE;
        }
        return false;
    }
}
