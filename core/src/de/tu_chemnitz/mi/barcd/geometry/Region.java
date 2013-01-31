package de.tu_chemnitz.mi.barcd.geometry;

import java.util.Arrays;
import java.util.List;

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
    
    private Rectangle orientedRectangle;
    
    private Rectangle axisAlignedRectangle;
    
    /**
     * The number of points used to create this region.
     */
    private int generatingPointCount;
    
    /**
     * Construct a region from an array of points (expects no duplicate points).
     * 
     * @param points the points that should make up a region
     */
    public Region(Point[] points) {
        this(Arrays.asList(points));
    }
    
    /**
     * Construct a region from a list of points (expects no duplicate points).
     * 
     * @param points the points that should make up a region
     */
    public Region(List<Point> points) {
        this.polygon = ConvexPolygon.createFromConvexHull(points);
        this.generatingPointCount = points.size();
    }
    
    public double getCoverage(BoundType type) {
        double cov;
        switch (type) {
        case AXIS_ALIGNED_RECTANGLE:
            cov = this.generatingPointCount / (double) getAxisAlignedRectangle().computeArea();
            break;
        case CONVEX_POLYGON:
            cov = this.generatingPointCount / (double) this.polygon.computeArea();
            break;
        case ORIENTED_RECTANGLE:
            cov = this.generatingPointCount / (double) getOrientedRectangle().computeArea();
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
    public Rectangle getOrientedRectangle() {
        if (this.orientedRectangle == null) {
            this.orientedRectangle = Rectangle.createOriented(this.polygon);
        }
        return this.orientedRectangle;
    }
    
    /**
     * @return the minimum area axis aligned rectangle enclosing the points
     *         spanning this region
     */
    public Rectangle getAxisAlignedRectangle() {
        if (this.axisAlignedRectangle == null) {
            this.axisAlignedRectangle = Rectangle.createAxisAligned(this.polygon);
        }
        return this.axisAlignedRectangle;
    }
}
