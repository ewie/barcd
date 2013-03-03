package de.tu_chemnitz.mi.barcd.geometry;

import java.util.List;

/**
 * A generic n-sides convex polygon.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class GenericConvexPolygon extends ConvexPolygon {
    private Point[] points;

    /**
     * Create a convex polygon from some vertices.
     *
     * @param points the convex polygon's vertices
     */
    public GenericConvexPolygon(Point[] points) {
        this.points = points;
    }

    @Override
    public Point[] getPoints() {
        return points;
    }

    /**
     * Create a convex polygon from the convex hull of the given points.
     *
     * @param points the points whose convex hull is computed
     *
     * @return the convex polygon containg all given points
     */
    public static GenericConvexPolygon createFromConvexHull(List<Point> points) {
        ConvexHullAlgorithm convexHull = new ConvexHullAlgorithm();
        return new GenericConvexPolygon(convexHull.computeConvexHull(points));
    }
}
