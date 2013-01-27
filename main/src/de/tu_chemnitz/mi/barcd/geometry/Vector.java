package de.tu_chemnitz.mi.barcd.geometry;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Vector {
    private int x;
    private int y;
    
    public Vector(int x, int y) {
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
