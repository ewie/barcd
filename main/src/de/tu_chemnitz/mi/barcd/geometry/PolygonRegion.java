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
class PolygonRegion {
    private Point[] coords;
    private double coverage;
    
    private static int area(Point[] coords) {
        int area = 0;
        for (int i = 0; i < coords.length - 1; ++i) {
            area += coords[i].x() * coords[i+1].y()
                  - coords[i+1].x() * coords[i].y();
        }
        return area / 2;
    }
    
    private static double cross(Point p, Point a, Point b) {
        return (a.x() - p.x()) * (b.y() - p.y()) - (a.y() - p.y()) * (b.x() - p.x());
    }
    
    public static PolygonRegion fromCoordinates(Point[] coords) {
        return fromCoordinates(Arrays.asList(coords));
    }
    
    public static PolygonRegion fromCoordinates(List<Point> coords) {
        Collections.sort(coords, new Comparator<Point>() {
            @Override
            public int compare(Point p, Point q) {
                if (p.x() < q.x()) return -1;
                if (p.x() > q.x()) return 1;
                if (p.y() < q.y()) return -1;
                if (p.y() > q.y()) return 1;
                return 0;
            }
        });

        LinkedList<Point> lower = new LinkedList<Point>();
        LinkedList<Point> upper = new LinkedList<Point>();
        
        for (Point p : coords) {
            while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0) {
                lower.removeLast();
            }
            lower.add(p);
        }
        
        ListIterator<Point> it = coords.listIterator(coords.size());
        while (it.hasPrevious()) {
            Point p = it.previous();
            while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0) {
                upper.removeLast();
            }
            upper.add(p);
        }
        
        lower.removeLast();
        upper.removeLast();
        
        Point[] hull = new Point[lower.size() + upper.size()];
        int i = 0;
        for (Point c : lower) hull[i++] = c;
        for (Point c : upper) hull[i++] = c;
        return new PolygonRegion(hull, (double) coords.size() / area(hull));
    }
    
    public PolygonRegion(Point[] coords, double coverage) {
        this.coords = coords;
        this.coverage = coverage;
    }

    public Point[] getCoordinates() {
        return this.coords;
    }

    public int getSize() {
        return this.coords.length;
    }

    public double getCoverage() {
        return this.coverage;
    }
    
    public int getArea() {
        return area(this.coords);
    }
}