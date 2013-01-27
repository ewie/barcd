package de.tu_chemnitz.mi.barcd.image;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface KernelOperator extends Operator {
    /**
     * @return the kernel's width
     */
    public int getWidth();
    
    /**
     * @return the kernel's height
     */
    public int getHeight();
}
