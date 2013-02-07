package de.tu_chemnitz.mi.barcd.geometry;

import java.util.Arrays;
import java.util.List;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class GenericConvexPolygon extends ConvexPolygon {
    private Point[] points;
    
    public GenericConvexPolygon(Point[] points) {
        this.points = points;
    }

    @Override
    public Point[] getPoints() {
        return points;
    }
    
    public static GenericConvexPolygon createFromConvexHull(Point[] points) {
        return createFromConvexHull(Arrays.asList(points));
    }
    
    public static GenericConvexPolygon createFromConvexHull(List<Point> points) {
        ConvexHullAlgorithm convexHull = new ConvexHullAlgorithm();
        return new GenericConvexPolygon(convexHull.computeConvexHull(points));
    }
}
