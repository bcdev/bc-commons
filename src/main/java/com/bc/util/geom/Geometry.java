/*
 * $Id: Geometry.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import java.awt.Shape;
import java.io.Serializable;

// discuss with Olaf the consequences of this. I added the serializable stuff because
// tomcat throws exception on geometry objects when saving the session
// ANSWER: see http://jroller.com/page/JonathanLehr?anchor=tomcat_weirdness_sheesh, short: You need
// to configure tomcat to not persist sessions, a task that is best described as unintuitive...

public interface Geometry extends Serializable {

    String POINT = "POINT";
    String LINESTRING = "LINESTRING";
    String POLYGON = "POLYGON";
    String MULTIPOINT = "MULTIPOINT";
    String MULTILINESTRING = "MULTILINESTRING";
    String MULTIPOLYGON = "MULTIPOLYGON";
    String GEOMETRYCOLLECTION = "GEOMETRYCOLLECTION";

    int TRUE = 1;
    int FALSE = 0;
    int UNKNOWN = -1;

    PointGeometry getCenterPoint();

    String getGeometryType();

    int getDimension();

    Shape getAsShape();

    String getAsText();

    /**
     * returns Geometry.TRUE if this geometry is 'spatially equal' to given Geometry
     * @param g the geometry to be tested
     * @return Geometry.TRUE or Geometry.FALSE or Geometry.UNKNOWN
     */
    int getEquals(Geometry g);

    /**
     * returns Geometry.TRUE if this geometry is 'spatially disjoint' to the given Geometry
     * @param g the geometry to be tested
     * @return Geometry.TRUE or Geometry.FALSE or Geometry.UNKNOWN
     */
    int getDisjoint(Geometry g);

    /**
     * returns Geometry.TRUE if this geometry is 'spatially within' the given Geometry
     * @param g the geometry to be tested
     * @return Geometry.TRUE or Geometry.FALSE or Geometry.UNKNOWN
     */
    int getWithin(Geometry g);

    /**
     * Returns whether the geometry passed in intersects with this one.
     * @param g the geometry to be tested
     * @return Geometry.TRUE or Geometry.FALSE or Geometry.UNKNOWN
     */
    int getIntersects(Geometry g);

    /**
     * returns Geometry.TRUE if this geometry 'spatially contains' given Geometry
     * @param g the geometry to be tested
     * @return Geometry.TRUE or Geometry.FALSE or Geometry.UNKNOWN
     */
    int getContains(Geometry g);
}
