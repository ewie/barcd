package de.tu_chemnitz.mi.barcd.geometry;

public class Rectangle extends ConvexPolygon {
    public Rectangle(Point a, Point b, Point c, Point d) {
        this(new Point[] { a, b, c, d });
    }
    
    public Rectangle(Point[] points) {
        super(points);
        // TODO check if rectangular
    }
    
    public int width() {
        Point p = this.points()[0];
        Point q = this.points()[1];
        int dx = p.x() - q.x();
        int dy = p.y() - q.y();
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
    
    public int height() {
        Point p = this.points()[1];
        Point q = this.points()[2];
        int dx = p.x() - q.x();
        int dy = p.y() - q.y();
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
    
    public static Rectangle axisAligned(ConvexPolygon polygon) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        Point[] coords = polygon.points();
        
        for (int i = 0; i < coords.length; ++i) {
            int x = coords[i].x();
            int y = coords[i].y();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        
        return new Rectangle(
            new Point(minX, maxY),
            new Point(maxX, maxY),
            new Point(maxX, minY),
            new Point(minX, minY));
    }
    
    public static Rectangle oriented(ConvexPolygon polygon) {
        Point[] hull = polygon.points();
        double[] boxx = new double[4];
        double[] boxy = new double[4];
        double minArea = Integer.MAX_VALUE;
        double invAngle = 0;
        for (int i = 0; i < hull.length; ++i) {
            Point p = hull[i];
            Point q = hull[(i+1) % hull.length];
            int dx = q.x() - p.x();
            int dy = q.y() - p.y();
            double angle = Math.acos(dx / (Math.sqrt(dx*dx + dy*dy)));
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            for (int j = 0; j < hull.length; ++j) {
                Point r = hull[j];
                double c = Math.cos(angle);
                double s = Math.sin(angle);
                double x = r.x() * c + r.y() * -s;
                double y = r.x() * s + r.y() * c;
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
            double area = (maxX - minX) * (maxY - minY);
            if (area < minArea) {
                minArea = area;
                invAngle = -angle;
                boxx[0] = minX;
                boxx[1] = maxX;
                boxx[2] = maxX;
                boxx[3] = minX;
                boxy[0] = minY;
                boxy[1] = minY;
                boxy[2] = maxY;
                boxy[3] = maxY;
            }
        }
        double c = Math.cos(invAngle);
        double s = Math.sin(invAngle);
        Point[] box = new Point[4];
        for (int i = 0; i < 4; ++i) {
            box[i] = new Point(
                (int) (boxx[i] * c + boxy[i] * -s),
                (int) (boxx[i] * s + boxy[i] * c));
        }
        return new Rectangle(box);
    }
}