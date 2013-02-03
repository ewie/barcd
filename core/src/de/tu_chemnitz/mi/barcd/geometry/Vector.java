package de.tu_chemnitz.mi.barcd.geometry;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Vector {
    private double x;
    private double y;
    
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector(Point p, Point q) {
        this(q.getX() - p.getX(), q.getY() - p.getY());
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getLength() {
        return Math.sqrt(inner(this));
    }
    
    public double getAngle(Vector other) {
        return inner(other) / (getLength() * other.getLength());
    }
    
    public double inner(Vector other) {
        return x * other.x + y * other.y;
    }
}
