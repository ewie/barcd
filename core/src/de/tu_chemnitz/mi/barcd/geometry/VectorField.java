package de.tu_chemnitz.mi.barcd.geometry;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public class VectorField {
    private int width;

    private int height;

    private int blockSize;

    private Vector[] vectors;

    public VectorField(int blockSize, int width, int height, Vector[] vectors) {
        this.width = width;
        this.height = height;
        this.blockSize = blockSize;
        this.vectors = vectors;
    }

    public Vector getVectorAt(int x, int y) {
        int at = (x / blockSize) + (y / blockSize) * (width / blockSize);
        return vectors[at];
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
