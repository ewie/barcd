package de.tu_chemnitz.mi.barcd.geometry;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Point {
    private int x;
    private int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int x() {
        return this.x;
    }
    
    public int y() {
        return this.y;
    }
}