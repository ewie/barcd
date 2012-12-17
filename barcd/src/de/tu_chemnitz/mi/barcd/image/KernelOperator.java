package de.tu_chemnitz.mi.barcd.image;

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
