package de.tu_chemnitz.mi.barcd.geometry;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public abstract class Rectangle extends ConvexPolygon {
    /**
     * @return
     */
    public abstract double getWidth();
    
    /**
     * @return
     */
    public abstract double getHeight();
    
    @Override
    public double computeArea() {
        return getWidth() * getHeight();
    }
}
