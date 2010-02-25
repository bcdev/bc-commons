/*
 * $Id: GeometryParser.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.geom;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.text.ParseException;

public class GeometryParser {

    private StreamTokenizer tokenizer;

    public GeometryParser() {
    }

    /**
     * Creates geometry from the given well-known-text (WKT) representation. For details about the WKT, refer to the
     * OpenGIS SimpleFeatures for SQL Specification document.
     *
     * @param wkt the geometry well-known-text representation
     *
     * @return the geometry
     *
     * @throws ParseException if a parse fail occurs
     */
    public Geometry parseWKT(String wkt) throws ParseException {
        if (wkt == null) {
            throw new IllegalArgumentException("wkt is null");
        }

        initTokenizer(wkt);

        Geometry geometry = null;
        try {
            geometry = parseGeometryTaggedText();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        } finally {
            disposeTokenizer();
        }

        return geometry;
    }

    private Geometry parseGeometryTaggedText() throws IOException,
                                                      ParseException {
        Geometry geometry = parsePointTaggedText();
        if (geometry == null) {
            geometry = parseLineStringTaggedText();
        }
        if (geometry == null) {
            geometry = parsePolygonTaggedText();
        }
        if (geometry == null) {
            geometry = parseMultiPointTaggedText();
        }
        if (geometry == null) {
            geometry = parseMultiLineStringTaggedText();
        }
        if (geometry == null) {
            geometry = parseMultiPolygonTaggedText();
        }
        if (geometry == null) {
            geometry = parseGeometryCollectionTaggedText();
        }
        if (geometry == null) {
            fail("geometry type name expected");
        }
        return geometry;
    }


    private PointGeometry parsePointTaggedText() throws IOException,
                                                        ParseException {
        int tt = tokenizer.nextToken();
        if (tt == StreamTokenizer.TT_WORD && tokenizer.sval.equalsIgnoreCase(Geometry.POINT)) {
            return parsePointBody();
        }
        tokenizer.pushBack();
        return null;
    }

    private Geometry parseLineStringTaggedText() throws IOException,
                                                        ParseException {
        int tt = tokenizer.nextToken();
        if (tt == StreamTokenizer.TT_WORD && tokenizer.sval.equalsIgnoreCase(Geometry.LINESTRING)) {
            return parseLineStringBody();
        }
        tokenizer.pushBack();
        return null;
    }

    private Geometry parsePolygonTaggedText() throws IOException,
                                                     ParseException {
        int tt = tokenizer.nextToken();
        if (tt == StreamTokenizer.TT_WORD && tokenizer.sval.equalsIgnoreCase(Geometry.POLYGON)) {
            return parsePolygonBody();
        }
        tokenizer.pushBack();
        return null;
    }

    private Geometry parseMultiPointTaggedText() throws IOException,
                                                        ParseException {
        int tt = tokenizer.nextToken();
        if (tt == StreamTokenizer.TT_WORD && tokenizer.sval.equalsIgnoreCase(Geometry.MULTIPOINT)) {
            return parseMultiPointBody();
        }
        tokenizer.pushBack();
        return null;
    }

    private Geometry parseMultiLineStringTaggedText() throws IOException,
                                                             ParseException {
        int tt = tokenizer.nextToken();
        if (tt == StreamTokenizer.TT_WORD && tokenizer.sval.equalsIgnoreCase(Geometry.MULTILINESTRING)) {
            return parseMultiLineStringBody();
        }
        tokenizer.pushBack();
        return null;
    }

    private Geometry parseMultiPolygonTaggedText() throws IOException,
                                                          ParseException {
        int tt = tokenizer.nextToken();
        if (tt == StreamTokenizer.TT_WORD && tokenizer.sval.equalsIgnoreCase(Geometry.MULTIPOLYGON)) {
            return parseMultiPolygonBody();
        }
        tokenizer.pushBack();
        return null;
    }

    private Geometry parseGeometryCollectionTaggedText() throws IOException,
                                                                ParseException {
        int tt = tokenizer.nextToken();
        if (tt == StreamTokenizer.TT_WORD && tokenizer.sval.equalsIgnoreCase(Geometry.GEOMETRYCOLLECTION)) {
            return parseGeometryCollectionBody();
        }
        tokenizer.pushBack();
        return null;
    }

    private PointGeometry parsePointBody() throws IOException,
                                                  ParseException {
        final Point2D point = new Point2D.Double();
        int tt = tokenizer.nextToken();
        if (tt != StreamTokenizer.TT_EOF) {
            parseListStart(tt);
            parsePoint(point);
            tt = tokenizer.nextToken();
            if (tt != ')') {
                tokenizer.pushBack();
                fail("')' expected");
            }
        }
        return new PointGeometry(point.getX(), point.getY());
    }

    private LineStringGeometry parseLineStringBody() throws IOException,
                                                            ParseException {
        return parseLineStringBody(false);
    }

    private LineStringGeometry parseLineStringBody(boolean autoClose) throws IOException,
                                                                             ParseException {
        final GeneralPath gp = new GeneralPath();
        int tt = tokenizer.nextToken();
        if (tt != StreamTokenizer.TT_EOF) {
            parseListStart(tt);
            Point2D.Double point = new Point2D.Double();
            int numElems = 0;
            float x0 = 0, y0 = 0;
            float xi = 0, yi = 0;
            boolean mustLineTo = false;
            do {
                if (mustLineTo) {
                    gp.lineTo(xi, yi);
                }
                parsePoint(point);
                if (numElems == 0) {
                    x0 = (float) point.x;
                    y0 = (float) point.y;
                    gp.moveTo(x0, y0);
                } else {
                    xi = (float) point.x;
                    yi = (float) point.y;
                    mustLineTo = true;
                }
                numElems++;
            } while (!parseListEnd());
            if (autoClose) {
                // lineTo(xi, yi) only if it is not first point,
                // because  path.closePath() does the job for us
                if (mustLineTo && (x0 != xi || y0 != yi)) {
                    gp.lineTo(xi, yi);
                }
                gp.closePath();
            } else if (mustLineTo) {
                gp.lineTo(xi, yi);
            }
        }
        return new LineStringGeometry(gp);
    }

    private PolygonGeometry parsePolygonBody() throws IOException,
                                                      ParseException {
        GeneralPath path = new GeneralPath();
        ShapeGeometry elem = null;
        int numElems = 0;
        int tt = tokenizer.nextToken();
        if (tt != StreamTokenizer.TT_EOF) {
            parseListStart(tt);
            do {
                elem = parseLineStringBody(true);
                path.append(elem.getAsShape(), false);
                numElems++;
            } while (!parseListEnd());
        }

        return new PolygonGeometry(numElems == 1 ? elem.getAsShape() : path);
    }

    private MultiPointGeometry parseMultiPointBody() throws IOException,
                                                            ParseException {
        MultiPointGeometry mp = new MultiPointGeometry();
        PointGeometry p;
        int tt = tokenizer.nextToken();
        if (tt != StreamTokenizer.TT_EOF) {
            parseListStart(tt);
            do {
                p = parsePointBody();
                mp.addPoint(p);
            } while (!parseListEnd());
        }
        return mp;
    }

    private MultiLineStringGeometry parseMultiLineStringBody() throws IOException,
                                                                      ParseException {
        MultiLineStringGeometry ml = new MultiLineStringGeometry();
        LineStringGeometry l = null;
        int tt = tokenizer.nextToken();
        if (tt != StreamTokenizer.TT_EOF) {
            parseListStart(tt);
            do {
                l = parseLineStringBody();
                ml.addLineString(l);
            } while (!parseListEnd());
        }
        return ml;
    }

    private MultiPolygonGeometry parseMultiPolygonBody() throws IOException,
                                                                ParseException {
        MultiPolygonGeometry mp = new MultiPolygonGeometry();
        PolygonGeometry p = null;
        int tt = tokenizer.nextToken();
        if (tt != StreamTokenizer.TT_EOF) {
            parseListStart(tt);
            do {
                p = parsePolygonBody();
                mp.addPolygon(p);
            } while (!parseListEnd());
        }
        return mp;
    }

    private GeometryCollection parseGeometryCollectionBody() throws IOException,
                                                                    ParseException {
        GeometryCollection gc = new GeometryCollection();
        Geometry g;
        int tt = tokenizer.nextToken();
        if (tt != StreamTokenizer.TT_EOF) {
            parseListStart(tt);
            do {
                g = parseGeometryTaggedText();
                gc.addGeometry(g);
            } while (parseListEnd());
        }
        return gc;
    }

    private void parsePoint(Point2D point) throws IOException,
                                                  ParseException {
        final double x = parseDouble(tokenizer, "x-value");
        final double y = parseDouble(tokenizer, "y-value");
        point.setLocation(x, y);
    }

    /**
     * Private helper method to enable parsing of floating point numbers in "e" notation. This contains the most basic
     * implementation that seems to be suitable for this kind of operation but will fail on certain unexpected values,
     * e.g. 1e3b4 where 1 will be the mantissa and 3b4 will be the exponent (which would fail...)
     *
     * @param tokenizer
     * @param valueName
     *
     * @return
     *
     * @throws ParseException
     * @throws IOException
     */
    private double parseDouble(StreamTokenizer tokenizer, String valueName) throws ParseException,
                                                                                   IOException {
        int tt = tokenizer.nextToken();
        if (tt != StreamTokenizer.TT_NUMBER) {
            tokenizer.pushBack();
            fail(valueName + " expected");
        }
        double result = tokenizer.nval;
        tt = tokenizer.nextToken();
        if (tt == StreamTokenizer.TT_WORD &&
            (tokenizer.sval.startsWith("e") || tokenizer.sval.startsWith("E"))) {
            result = new Double(result + tokenizer.sval).doubleValue();
        } else {
            tokenizer.pushBack();
        }
        return result;
    }

    private void parseListStart(int tt) throws ParseException {
        if (tt != '(') {
            tokenizer.pushBack();
            fail("'(' expected");
        }
    }

    private boolean parseListEnd() throws IOException,
                                          ParseException {
        int tt = tokenizer.nextToken();
        if (tt == ')') {
            return true;
        } else if (tt != ',') {
            tokenizer.pushBack();
            fail("',' or ')' expected");
        }
        return false;
    }

    private void initTokenizer(String wkt) {
        StringReader r = new StringReader(wkt);
        tokenizer = new StreamTokenizer(r);
        tokenizer.resetSyntax();
        tokenizer.parseNumbers();
        tokenizer.whitespaceChars(0, 32);
        tokenizer.eolIsSignificant(false);
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('_', '_');
        tokenizer.quoteChar('"');
    }

    private void disposeTokenizer() {
        tokenizer = null;
    }

    private void fail(String message) throws ParseException {
        throw new ParseException(message, tokenizer.lineno());
    }
}
