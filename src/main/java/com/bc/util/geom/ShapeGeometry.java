/*
 * $Id: ShapeGeometry.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RectangularShape;

public abstract class ShapeGeometry extends AbstractGeometry {

    private final transient Shape shape;
    public static final double EPS = 1e-5;

    protected ShapeGeometry(Shape shape) {
        this.shape = shape;
    }

    public PointGeometry getCenterPoint() {
        final Rectangle2D bounds2D = shape.getBounds2D();
        return new PointGeometry(bounds2D.getCenterX(), bounds2D.getCenterY());
    }

    public Shape getAsShape() {
        if (shape instanceof RectangularShape) {
            return (Shape) ((RectangularShape) shape).clone();
        } else if (shape instanceof GeneralPath) {
            return (Shape) ((GeneralPath) shape).clone();
        }
        return shape;
    }

    public int getEquals(Geometry g) {
        if (g == this) {
            return TRUE;
        } else if (g == null) {
            return UNKNOWN;
        } else if (getDimension() != g.getDimension()) {
            return FALSE;
        } else if (g instanceof ShapeGeometry) {
            final ShapeGeometry sg = (ShapeGeometry) g;
            if (shape.equals(sg.shape)) {
                return TRUE;
            }
            //
            // test if path iterators are equal
            //
            final PathIterator p1 = shape.getPathIterator(null);
            final PathIterator p2 = sg.shape.getPathIterator(null);
            final float[] c1 = new float[6];
            final float[] c2 = new float[6];
            int s1, s2;
            double dx, dy;
            boolean done = false;
            while (!done) {
                if (p1.isDone() != p2.isDone()) {
                    break;
                }
                if (p1.isDone()) {
                    return TRUE;
                }
                s1 = p1.currentSegment(c1);
                s2 = p2.currentSegment(c2);
                if (s1 != s2) {
                    break;
                }
                for (int i = 0; i < 3; i += 2) {
                    dx = c1[i] - c2[i];
                    dy = c1[i + 1] - c2[i + 1];
                    if (dx * dx + dy * dy > (EPS * EPS)) {
                        done = true;
                        break;
                    }
                }
                if (done) {
                    break;
                }
                p1.next();
                p2.next();
            }
            //
            // if we reach this point, the path iterators are not equal
            // and we compare the areas
            //
            if (getDimension() == 2) {
                final Area a1 = new Area(shape);
                final Area a2 = new Area(sg.shape);
                return a1.equals(a2) ? TRUE : FALSE;
            }
            return FALSE;
        }
        return FALSE;
    }

    public int getDisjoint(Geometry g) {
        if (g == null) {
            return UNKNOWN;
        }
        final Shape s1 = getAsShape();
        if (s1 == null) {
            return UNKNOWN;
        }
        if (g instanceof PointGeometry) {
            PointGeometry pg = (PointGeometry) g;
            return s1.contains(pg.getX(), pg.getY()) ? FALSE : TRUE;
        } else {
            final Shape s2 = g.getAsShape();
            if (s2 == null) {
                return UNKNOWN;
            }
            final Area a1 = new Area(s1);
            final Area a2 = s2 instanceof Area ? (Area) s2 : new Area(s2);
            a1.intersect(a2);
            return a1.isEmpty() ? TRUE : FALSE;
        }
    }

    public int getContains(Geometry g) {
        if (g == null) {
            return UNKNOWN;
        }
        if (g instanceof PointGeometry) {
            final PointGeometry pg = (PointGeometry) g;
            if (getAsShape().contains(pg.getPoint())) {
                return TRUE;
            } else {
                return FALSE;
            }
        } else {
            final Area a1 = new Area(getAsShape());
            final Area a2 = new Area(g.getAsShape());
            a1.intersect(a2);
            return a1.equals(a2) ? TRUE : FALSE;
        }
    }
}
