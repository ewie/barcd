/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd;

import de.tu_chemnitz.mi.barcd.geometry.AxisAlignedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.ConvexPolygon;
import de.tu_chemnitz.mi.barcd.geometry.OrientedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.Point;

/**
 * A region is a set of connected points, generating the region, and is
 * described by the convex polygon containing all generating points. A region's
 * coverage property specifies the percentage the convex polygon's area is
 * actually covered by generating points.
 *
 * Based on the region's convex polygon, an oriented minimum area enclosing
 * rectangle as well as an axis-aligned enclosing rectangle can be computed.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Region {
    private final ConvexPolygon convexPolygon;

    private OrientedRectangle orientedRectangle;

    private AxisAlignedRectangle axisAlignedRectangle;

    private Barcode barcode;

    private final double coverage;

    /**
     * @param convexPolygon the convex polygon describing the region
     * @param coverage the region's coverage
     */
    public Region(ConvexPolygon convexPolygon, double coverage) {
        this.convexPolygon = convexPolygon;
        this.coverage = Math.max(0, Math.min(coverage, 1));
    }

    /**
     * Get the region's coverage.
     *
     * @return the region's coverage
     */
    public double getCoverage() {
        return coverage;
    }

    /**
     * @return the barcode covered by this region or {@code null} if the region
     *   covers no barcode
     */
    public Barcode getBarcode() {
        return barcode;
    }

    /**
     * Set the barcode covered by this region.
     *
     * @param barcode the barcode covered by this region
     */
    public void setBarcode(Barcode barcode) {
        this.barcode = barcode;
    }

    /**
     * @return the convex polygon containing the points spanning this region
     */
    public ConvexPolygon getConvexPolygon() {
        return convexPolygon;
    }

    /**
     * @return the minimum area rectangle enclosing the points spanning this
     *         region
     */
    public OrientedRectangle getOrientedRectangle() {
        if (orientedRectangle == null) {
            orientedRectangle = OrientedRectangle.createFromPolygon(convexPolygon);
        }
        return orientedRectangle;
    }

    /**
     * @return the minimum area axis aligned rectangle enclosing the points
     *         spanning this region
     */
    public AxisAlignedRectangle getAxisAlignedRectangle() {
        if (axisAlignedRectangle == null) {
            axisAlignedRectangle = AxisAlignedRectangle.createFromPolygon(convexPolygon);
        }
        return axisAlignedRectangle;
    }

    /**
     * Test if the region contains a certain point.
     *
     * @param p the point to test
     *
     * @return true if the region contains the point
     */
    public boolean contains(Point p) {
        AxisAlignedRectangle ar = getAxisAlignedRectangle();
        return ar.contains(p) && convexPolygon.contains(p);
    }
}
