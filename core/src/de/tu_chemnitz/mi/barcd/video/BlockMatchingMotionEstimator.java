package de.tu_chemnitz.mi.barcd.video;

import de.tu_chemnitz.mi.barcd.geometry.Vector;
import de.tu_chemnitz.mi.barcd.geometry.VectorField;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public abstract class BlockMatchingMotionEstimator {
    private int blockSize;
    
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
        
        for (int i = 0, y = 0; y < height; y += blockSize) {
            for (int x = 0; x < width; x += blockSize) {
                vectors[i++] = processBlock(x, y, image, referenceImage);
            }
        }
        
        return new VectorField(blockSize, width, height, vectors);
    }
    
    public abstract Vector processBlock(int x, int y, LuminanceImage image, LuminanceImage referenceImage);
}
