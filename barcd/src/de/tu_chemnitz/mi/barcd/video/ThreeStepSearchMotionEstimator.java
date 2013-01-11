package de.tu_chemnitz.mi.barcd.video;

import de.tu_chemnitz.mi.barcd.geometry.Vector;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;

public class ThreeStepSearchMotionEstimator extends BlockMatchingMotionEstimator {
    public ThreeStepSearchMotionEstimator(int blockSize) {
        super(blockSize);
    }
    
    @Override
    public Vector processBlock(int x, int y, LuminanceImage image, LuminanceImage referenceImage) {
        int x0 = x;
        int y0 = y;
        int width = image.width();
        int height = image.height();
        for (int size = this.blockSize(); size > 1; size /= 2) {
            int minCost = bdm(image, x, y, referenceImage, 0, 0);
            int dx = 0;
            int dy = 0;
            int d = size / 2;
            for (int i = -d; i <= d; i += d) {
                for (int j = -d; j <= d; j += d) {
                    int u = x + i;
                    int v = y + j;
                    if (u < 0 || v < 0 || (u + blockSize()) >= width || (v + blockSize()) >= height) {
                        continue;
                    }
                    int cost = bdm(image, x, y, referenceImage, i, j);
                    if (cost < minCost) {
                        minCost = cost;
                        dx = i;
                        dy = j;
                    }
                }
            }
            x += dx;
            y += dy;
        }
        return new Vector(x - x0, y - y0);
    }
    
    private int bdm(LuminanceImage image, int x, int y, LuminanceImage referenceImage, int dx, int dy) {
        int bdm = 0;
        for (int i = 0; i < blockSize(); ++i) {
            for (int j = 0; j < blockSize(); ++j) {
                bdm += Math.abs(image.valueAt(x + i, y + j) - referenceImage.valueAt(x + dx + i, y + dy + j));
            }
        }
        return bdm / (blockSize() * blockSize());
    }
}
