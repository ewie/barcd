package de.tu_chemnitz.mi.barcd.image;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface Operator {
    /**
     * Apply the operator to an input image.
     * 
     * @param input the image to process
     * 
     * @return a new image as the result of the operator applied to the input
     */
    public LuminanceImage apply(LuminanceImage input);
}