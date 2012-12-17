package de.tu_chemnitz.mi.barcd;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

class PolygonRegion {
    private Coordinate[] coords;
    private double coverage;
    
    private static int area(Coordinate[] coords) {
        int area = 0;
        for (int i = 0; i < coords.length - 1; ++i) {
            area += coords[i].getX() * coords[i+1].getY()
                  - coords[i+1].getX() * coords[i].getY();
        }
        return area / 2;
    }
    
    private static long cross(Coordinate p, Coordinate a, Coordinate b) {
        return (a.getX() - p.getX()) * (b.getY() - p.getY()) - (a.getY() - p.getY()) * (b.getX() - p.getX());
    }
    
    public static PolygonRegion fromCoordinates(Coordinate[] coords) {
        return fromCoordinates(Arrays.asList(coords));
    }
    
    public static PolygonRegion fromCoordinates(List<Coordinate> coords) {
        Collections.sort(coords, new Comparator<Coordinate>() {
            @Override
            public int compare(Coordinate p, Coordinate q) {
                if (p.getX() < q.getX()) return -1;
                if (p.getX() > q.getX()) return 1;
                if (p.getY() < q.getY()) return -1;
                if (p.getY() > q.getY()) return 1;
                return 0;
            }
        });

        LinkedList<Coordinate> lower = new LinkedList<Coordinate>();
        LinkedList<Coordinate> upper = new LinkedList<Coordinate>();
        
        for (Coordinate p : coords) {
            while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0) {
                lower.removeLast();
            }
            lower.add(p);
        }
        
        ListIterator<Coordinate> it = coords.listIterator(coords.size());
        while (it.hasPrevious()) {
            Coordinate p = it.previous();
            while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0) {
                upper.removeLast();
            }
            upper.add(p);
        }
        
        lower.removeLast();
        upper.removeLast();
        
        Coordinate[] hull = new Coordinate[lower.size() + upper.size()];
        int i = 0;
        for (Coordinate c : lower) hull[i++] = c;
        for (Coordinate c : upper) hull[i++] = c;
        return new PolygonRegion(hull, (double) coords.size() / area(hull));
    }
    
    public PolygonRegion(Coordinate[] coords, double coverage) {
        this.coords = coords;
        this.coverage = coverage;
    }
    
    public Coordinate[] getCoords() {
        return this.coords;
    }
    
    public double getCoverage() {
        return this.coverage;
    }
    
    public int getArea() {
        return area(this.coords);
    }
}