/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.geometry;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ConvexHullAlgorithm {
    /**
     * A comparator to order a collection of points in lexicographical order.
     *    (x, y) <= (u, v) IFF x < u OR (x = u AND y <= v)
     */
    private static Comparator<Point> lexicalPointComparator = new Comparator<Point>() {
        @Override
        public int compare(Point p, Point q) {
            double px = p.getX();
            double py = p.getY();
            double qx = q.getX();
            double qy = q.getY();
            if (px < qx) return -1;
            if (px > qx) return 1;
            if (py < qy) return -1;
            if (py > qy) return 1;
            return 0;
        }
    };

    /**
     * Compute the convex hull for a collection of points.
     *
     * @param points the points within the resulting convex hull
     *
     * @return the points making up the convex hull in clockwise order
     */
    public Point[] computeConvexHull(List<Point> points) {
        Collections.sort(points, lexicalPointComparator);

        LinkedList<Point> lower = new LinkedList<Point>();
        LinkedList<Point> upper = new LinkedList<Point>();

        for (Point p : points) {
            while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0) {
                lower.removeLast();
            }
            lower.add(p);
        }

        ListIterator<Point> it = points.listIterator(points.size());
        while (it.hasPrevious()) {
            Point p = it.previous();
            while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0) {
                upper.removeLast();
            }
            upper.add(p);
        }

        lower.removeLast();
        upper.removeLast();

        // Construct the array of points forming a convex hull. Reverse the
        // points so they end up in clockwise order.
        Point[] hull = new Point[lower.size() + upper.size()];
        int i = hull.length;
        for (Point p : lower) hull[--i] = p;
        for (Point p : upper) hull[--i] = p;

        return hull;
    }

    /**
     * Compute the 2D cross product of two vectors given by 3 points.
     *
     * @param origin the origin of both vectors
     * @param p a point forming the first vector with {@link origin}
     * @param q a point forming the second vector with {@link origin}
     *
     * @return the 2D cross product
     */
    private static double cross(Point origin, Point p, Point q) {
        return (p.getX() - origin.getX()) * (q.getY() - origin.getY()) - (p.getY() - origin.getY()) * (q.getX() - origin.getX());
    }
}