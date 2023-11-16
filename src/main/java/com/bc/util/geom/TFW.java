/*
 * $Id: TFW.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
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

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.text.ParseException;

/**
 * <p>TIFF World File (TFW) format is supported by ArcView, ArcInfo, MapInfo, and many other programs--such as SMS. TIFF
 * World files are normal TIFF files with an accompanying .twf metadata file. Despite the fact that TWF files are
 * relatively common, the definition of the .twf file has remained obscure.
 * <p/>
 * <p>The TFW file contains information about the origin (insertion point) of the image and individual cell size. There
 * is no information on image dimensions (number of rows or columns), units, projection or datum. This additional
 * information must be obtained from the supplied supplementary information, image description or image supplier, in
 * order to create an accompanying .IF file to correctly georeference the image. Typically, a MapInfo or ESRI image file
 * will be stored in a projection such as NAD27 and California State Plane Zone 6.
 * <p/>
 * <p>A TFW file normally contains the following information:
 * <p/>
 * <p>Line Number Sample Value Meaning <br>Line 1 +6.00 Cell size in the X direction <br>Line 2 -0.00 Rotation in the X
 * direction <br>Line 3 -0.00 Rotation in the Y direction <br>Line 4 -6.00 Cell size in the Y direction <br>Line 5
 * 1709053.00 Easting value of insertion point X <br>Line 6 807714.00 Northing value of insertion point Y
 * <p/>
 * <p>Note that sign of the cell size in Y (line 4) defines if the image insertion point is the upper left or lower left
 * corner. A positive means the "Y" values are increasing upwards and therefore, the registration must be starting at
 * the bottom or lower left corner. A negative sign means that the insertion point is the upper left. The insertion
 * point coordinates relate to the map corner of the image in its defined projection itself.
 * <p/>
 * <p>A sample TWF file is shown below:
 * <p/>
 * <p>2.5 pixel x size (meters) <br>-0.0 rotation factor <br>-0.0 rotation factor <br>-2.5 pixel y size (meters)
 * <br>75000 image min x (meters) (NW <br>-200000 image max y (meters) Corner) <br>Normally an image will be supplied
 * with a .tif and .twf file pair (e.g., RIVER.TIF and RIVER.TFW).
 * <p/>
 * <p>Note that SMS will perform a coordinate transformation of the image. Select Edit | Coordinate Conversions. Note
 * that if only the TIFF image is loaded, then you will not be able to "find" the image. So, it is best that you draw a
 * box around the image to see it.
 * <p/>
 * <p>Also, both the DEM and TFW files will need to be in the same coordinate system (or individually converted) for
 * them to match up in SMS.
 */
public class TFW {

    private final AffineTransform _transform;

    public TFW(double scaleX,
               double shearY,
               double shearX,
               double scaleY,
               double translateX,
               double translateY) {
        this(new double[]{scaleX, shearY, shearX, scaleY, translateX, translateY});
    }

    public TFW(double[] flatmatrix) {
        _transform = new AffineTransform(flatmatrix);
    }

    public double getScaleX() {
        return _transform.getScaleX();
    }

    public double getScaleY() {
        return _transform.getScaleY();
    }

    public double getShearX() {
        return _transform.getShearX();
    }

    public double getShearY() {
        return _transform.getShearY();
    }

    public double getTranslateX() {
        return _transform.getTranslateX();
    }

    public double getTranslateY() {
        return _transform.getTranslateY();
    }

    public static TFW load(final File file) throws ParseException,
                                                   IOException {
        final FileReader reader = new FileReader(file);
        try {
            return load(reader);
        } finally {
            reader.close();
        }
    }

    public static TFW load(Reader reader) throws IOException,
                                                 ParseException {
        final StreamTokenizer st = new StreamTokenizer(reader);
        final double[] flatmatrix = new double[6];
        for (int i = 0; i < flatmatrix.length; i++) {
            st.nextToken();
            if (st.ttype == StreamTokenizer.TT_NUMBER) {
                flatmatrix[i] = st.nval;
            } else {
                throw new ParseException("number expected", st.lineno());
            }
        }
        st.nextToken();
        if (st.ttype != StreamTokenizer.TT_EOF) {
            throw new ParseException("no more tokens expected", st.lineno());
        }
        return new TFW(flatmatrix);
    }
}
