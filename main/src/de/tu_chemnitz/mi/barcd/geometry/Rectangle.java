package de.tu_chemnitz.mi.barcd.geometry;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Rectangle extends ConvexPolygon {
    public Rectangle(Point a, Point b, Point c, Point d) {
        this(new Point[] { a, b, c, d });
    }
    
    public Rectangle(Point[] points) {
        super(points);
        // TODO check if rectangular
    }
    
    public int getWidth() {
        Point p = this.getPoints()[0];
        Point q = this.getPoints()[1];
        int dx = p.getX() - q.getX();
        int dy = p.getY() - q.getY();
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
    
    public int getHeight() {
        Point p = this.getPoints()[1];
        Point q = this.getPoints()[2];
        int dx = p.getX() - q.getX();
        int dy = p.getY() - q.getY();
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
    
    public static Rectangle createAxisAligned(ConvexPolygon polygon) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        Point[] coords = polygon.getPoints();
        
        for (int i = 0; i < coords.length; ++i) {
            int x = coords[i].getX();
            int y = coords[i].getY();
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
    
    public static Rectangle createOriented(ConvexPolygon polygon) {
        Point[] hull = polygon.getPoints();
        double[] boxx = new double[4];
        double[] boxy = new double[4];
        double minArea = Integer.MAX_VALUE;
        double invAngle = 0;
        for (int i = 0; i < hull.length; ++i) {
            Point p = hull[i];
            Point q = hull[(i+1) % hull.length];
            int dx = q.getX() - p.getX();
            int dy = q.getY() - p.getY();
            double angle = Math.acos(dx / (Math.sqrt(dx*dx + dy*dy)));
            double c = Math.cos(angle);
            double s = Math.sin(angle);
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            for (int j = 0; j < hull.length; ++j) {
                Point r = hull[j];
                double x = r.getX() * c + r.getY() * -s;
                double y = r.getX() * s + r.getY() * c;
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