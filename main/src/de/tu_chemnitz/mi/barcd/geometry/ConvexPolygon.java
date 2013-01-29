package de.tu_chemnitz.mi.barcd.geometry;

import java.util.Arrays;
import java.util.List;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ConvexPolygon {
    
    private Point[] points;
    
    public ConvexPolygon(Point[] points) {
        this.points = points;
    }

    public Point[] points() {
        return points;
    }
    
    public long area() {
        long area = 0;
        for (int i = 0; i < points.length; ++i) {
            Point p = points[i];
            Point q = points[(i+1) % points.length];
            area += q.x() * p.y() - p.x() * q.y();
        }
        return Math.abs(area / 2);
    }
    
    public static ConvexPolygon createFromConvexHull(Point[] points) {
        return createFromConvexHull(Arrays.asList(points));
    }
    
    public static ConvexPolygon createFromConvexHull(List<Point> points) {
        ConvexHullAlgorithm convexHull = new ConvexHullAlgorithm();
        return new ConvexPolygon(convexHull.computeConvexHull(points));
    }
}
