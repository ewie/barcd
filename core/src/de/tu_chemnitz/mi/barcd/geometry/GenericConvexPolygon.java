package de.tu_chemnitz.mi.barcd.geometry;

import java.util.List;

/**
 * A generic n-sides convex polygon.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class GenericConvexPolygon extends ConvexPolygon {
    private Point[] vertices;

    /**
     * Create a convex polygon from some vertices.
     *
     * @param vertices the convex polygon's vertices
     */
    public GenericConvexPolygon(Point[] vertices) {
        if (vertices.length < 3) {
            throw new IllegalArgumentException("create a convex polygon with at least 3 vertices");
        }
        this.vertices = vertices;
    }

    @Override
    public Point[] getVertices() {
        return vertices;
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
