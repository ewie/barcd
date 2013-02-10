package de.tu_chemnitz.mi.barcd.image.lum;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
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
