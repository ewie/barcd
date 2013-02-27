package de.tu_chemnitz.mi.barcd;

import java.util.Arrays;
import java.util.List;

import de.tu_chemnitz.mi.barcd.geometry.AxisAlignedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.ConvexPolygon;
import de.tu_chemnitz.mi.barcd.geometry.GenericConvexPolygon;
import de.tu_chemnitz.mi.barcd.geometry.OrientedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.Point;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Region {
    public enum BoundType {
        AXIS_ALIGNED_RECTANGLE,
        CONVEX_POLYGON,
        ORIENTED_RECTANGLE
    };
    
    private ConvexPolygon polygon;

    private OrientedRectangle orientedRectangle;

    private AxisAlignedRectangle axisAlignedRectangle;
    
    private Barcode barcode;
    
    private double coverage;
    
    public static Region createFromPoints(Point[] points) {
        return createFromPoints(Arrays.asList(points));
    }
    
    public static Region createFromPoints(List<Point> points) {
        return createFromPoints(points, points.size());
    }
    
    public static Region createFromPoints(List<Point> points, int generatingPointCount) {
        GenericConvexPolygon polygon = GenericConvexPolygon.createFromConvexHull(points);
        double coverage = generatingPointCount / polygon.computeArea();
        return new Region(polygon, coverage);
    }
    
    public Region(ConvexPolygon polygon, double coverage) {
        this.polygon = polygon;
        this.coverage = coverage;
    }
    
    public double getCoverage() {
        return coverage;
    }
    
    public Barcode getBarcode() {
        return barcode;
    }
    
    public void setBarcode(Barcode barcode) {
        this.barcode = barcode;
    }
    
    public double getCoverage(BoundType type) {
        double cov;
        switch (type) {
        case AXIS_ALIGNED_RECTANGLE:
            cov = (coverage * polygon.computeArea()) / getAxisAlignedRectangle().computeArea();
            break;
        case CONVEX_POLYGON:
            cov = coverage;
            break;
        case ORIENTED_RECTANGLE:
            cov = (coverage * polygon.computeArea()) / getOrientedRectangle().computeArea();
            break;
        default:
            throw new IllegalArgumentException("unknown region bound type");
        }
        return Math.max(0, Math.min(cov, 1));
    }
    
    /**
     * @return the convex polygon containing the points spanning this region
     */
    public ConvexPolygon getConvexPolygon() {
        return this.polygon;
    }
    
    /**
     * @return the minimum area rectangle enclosing the points spanning this
     *         region
     */
    public OrientedRectangle getOrientedRectangle() {
        if (this.orientedRectangle == null) {
            this.orientedRectangle = OrientedRectangle.createFromPolygon(this.polygon);
        }
        return this.orientedRectangle;
    }
    
    /**
     * @return the minimum area axis aligned rectangle enclosing the points
     *         spanning this region
     */
    public AxisAlignedRectangle getAxisAlignedRectangle() {
        if (this.axisAlignedRectangle == null) {
            this.axisAlignedRectangle = AxisAlignedRectangle.createFromPolygon(this.polygon);
        }
        return this.axisAlignedRectangle;
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
        return ar.contains(p) && polygon.contains(p);
    }
    
    /**
     * Get the ratio of the convex polygon's area and the oriented rectangle's
     * area.
     * 
     * @return the discrepancy in the range (0, 1]
     */
    public double getDiscrepancy() {
        return polygon.computeArea() / getOrientedRectangle().computeArea();
    }
}
