package de.tu_chemnitz.mi.barcd.geometry;

import java.util.Arrays;
import java.util.List;

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
        this.polygon = ConvexPolygon.fromPoints(points);
        this.generatingPointCount = points.size();
    }
    
    public double coverage(BoundType type) {
        double cov;
        switch (type) {
        case AXIS_ALIGNED_RECTANGLE:
            cov = this.generatingPointCount / (double) axisAlignedRectangle().area();
            break;
        case CONVEX_POLYGON:
            cov = this.generatingPointCount / (double) this.polygon.area();
            break;
        case ORIENTED_RECTANGLE:
            cov = this.generatingPointCount / (double) orientedRectangle().area();
            break;
        default:
            throw new IllegalArgumentException("unknown region bound type");
        }
        return Math.max(0, Math.min(cov, 1));
    }
    
    /**
     * @return the convex polygon containing the points spanning this region
     */
    public ConvexPolygon convexPolygon() {
        return this.polygon;
    }
    
    /**
     * @return the minimum area rectangle enclosing the points spanning this
     *         region
     */
    public Rectangle orientedRectangle() {
        if (this.orientedRectangle == null) {
            this.orientedRectangle = Rectangle.oriented(this.polygon);
        }
        return this.orientedRectangle;
    }
    
    /**
     * @return the minimum area axis aligned rectangle enclosing the points
     *         spanning this region
     */
    public Rectangle axisAlignedRectangle() {
        if (this.axisAlignedRectangle == null) {
            this.axisAlignedRectangle = Rectangle.axisAligned(this.polygon);
        }
        return this.axisAlignedRectangle;
    }
}
