package de.tu_chemnitz.mi.barcd.image;

/**
 * An operator to perform image dilation.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface DilationOperator {
    /**
     * Perform the dilation on the given pixels.
     *
     * @param pixels the row-major pixel data to dilate,
     *   expects {@code width * height} elements
     * @param width the image width
     * @param height the image height
     *
     * @return the dilated pixel data in row-major order
     *   ({@code width * height} elements)
     */
    public abstract int[] dilate(int[] pixels, int width, int height);
}
