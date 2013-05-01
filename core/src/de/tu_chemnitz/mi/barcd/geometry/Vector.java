package de.tu_chemnitz.mi.barcd.geometry;

/**
 * A 2-dimensional vector.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Vector {
    private final double x;
    private final double y;

    /**
     * @param x the x-component
     * @param y the y-component
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a vector from one point to another.
     *
     * @param p the start of the vector
     * @param q the end of the vector
     */
    public Vector(Point p, Point q) {
        this(q.getX() - p.getX(), q.getY() - p.getY());
    }

    /**
     * @return the x-component
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y-component
     */
    public double getY() {
        return y;
    }

    /**
     * @return the vector's length
     */
    public double getLength() {
        return Math.sqrt(inner(this));
    }

    /**
     * Compute the angle between this and a given vector.
     *
     * @param other the other vector
     *
     * @return the angle between the two vectors in radiant
     */
    public double computeAngle(Vector other) {
        return inner(other) / (getLength() * other.getLength());
    }

    /**
     * Apply the given length to this vector.
     *
     * @param length the length
     *
     * @return a new vector with same direction but the given length
     */
    public Vector applyLength(double length) {
        return normalize().scale(length);
    }

    /**
     * Normalize this vector.
     *
     * @return a normalized version of this vector
     */
    public Vector normalize() {
        double len = getLength();
        if (len == 0) {
            return this;
        }
        return scale(1 / len);
    }

    /**
     * Scale this vector by a factor.
     *
     * @param scale the factor
     *
     * @return a scaled version of this vector
     */
    public Vector scale(double scale) {
        return new Vector(x * scale, y * scale);
    }

    /**
     * Compute the inner product of this and a given vector
     *
     * @param other the other vector
     *
     * @return the inner product of the two vectors
     */
    private double inner(Vector other) {
        return x * other.x + y * other.y;
    }
}
