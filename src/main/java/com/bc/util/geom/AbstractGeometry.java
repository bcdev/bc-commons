/*
 * $Id: AbstractGeometry.java,v 1.2 2008-10-09 13:51:13 tom Exp $
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

public abstract class AbstractGeometry implements Geometry {

    public String toString() {
        return getAsText();
    }

    @SuppressWarnings({"SimplifiableIfStatement"})
    public boolean equals(Object obj) {
        if (obj instanceof Geometry) {
            return getEquals((Geometry) obj) == TRUE;
        }
        return false;
    }

    public int getIntersects(Geometry g) {
        if (g == null) {
            return UNKNOWN;
        }
        final int status = g.getDisjoint(this);
        if (status == TRUE) {
            return FALSE;
        } else if (status == FALSE) {
            return TRUE;
        } else {
            return UNKNOWN;
        }
    }

    public int getWithin(Geometry g) {
        if (g == null) {
            return UNKNOWN;
        }
        return g.getContains(this);
    }
}
