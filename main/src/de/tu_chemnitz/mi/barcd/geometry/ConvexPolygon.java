package de.tu_chemnitz.mi.barcd.geometry;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ConvexPolygon {
    /**
     * A comparator to order a collection of points in lexicographical order.
     *    (x, y) <= (u, v) iff x < u or (x = u and y <= v)
     */
    private static Comparator<Point> lexicalPointOrder = new Comparator<Point>() {
        @Override
        public int compare(Point p, Point q) {
            int px = p.x();
            int py = p.y();
            int qx = q.x();
            int qy = q.y();
            if (px < qx) return -1;
            if (px > qx) return 1;
            if (py < qy) return -1;
            if (py > qy) return 1;
            return 0;
        }
    };
    
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
    
    public static ConvexPolygon fromPoints(Point[] points) {
        return fromPoints(Arrays.asList(points));
    }
    
    public static ConvexPolygon fromPoints(List<Point> points) {
        Collections.sort(points, lexicalPointOrder);
        
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
        
        return new ConvexPolygon(hull);
    }
    
    private static long cross(Point p, Point a, Point b) {
        return (a.x() - p.x()) * (b.y() - p.y()) - (a.y() - p.y()) * (b.x() - p.x());
    }
}