package de.tu_chemnitz.mi.barcd.image.lum;

import de.tu_chemnitz.mi.barcd.geometry.Vector;
import de.tu_chemnitz.mi.barcd.geometry.VectorField;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public abstract class BlockMatchingMotionEstimator {
    private final int blockSize;

    public BlockMatchingMotionEstimator(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public VectorField estimateMotionVectors(LuminanceImage image, LuminanceImage referenceImage) {
        int width = image.getWidth();
        int height = image.getHeight();

        Vector[] vectors = new Vector[(width * height) / (blockSize * blockSize)];

        int vfwidth = (width / blockSize) * blockSize;
        int vfheight = (height / blockSize) * blockSize;

        int dx = (width - vfwidth) / 2;
        int dy = (height - vfheight) / 2;

        for (int i = 0, y = 0; y < vfheight; y += blockSize) {
            for (int x = 0; x < vfwidth; x += blockSize) {
                vectors[i++] = processBlock(x + dx, y + dy, image, referenceImage);
            }
        }

        return new VectorField(blockSize, vfwidth, vfheight, vectors);
    }

    public abstract Vector processBlock(int x, int y, LuminanceImage image, LuminanceImage referenceImage);
}
