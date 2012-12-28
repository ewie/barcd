package de.tu_chemnitz.mi.barcd.image;

public class MedianFilter implements Operation {
    private int size;
    
    public MedianFilter(int size) {
        this.size = size;
    }

    @Override
    public LuminanceImage apply(LuminanceImage in) {
        int w = in.width();
        int h = in.height();
        LuminanceImage out = new LuminanceImage(w, h);
        int[] neighbours;
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                neighbours = getNeighbours(in, x, y);
                out.setValueAt(x, y, getMedian(neighbours));
            }
        }
        return out;
    }
    
    private int[] getNeighbours(LuminanceImage in, int x, int y) {
        int xMin = x - size / 2;
        int yMin = y - size / 2;
        int xMax = x + size / 2;
        int yMax = y + size / 2;
        if (xMin < 0) xMin = 0;
        if (yMin < 0) yMin = 0;
        if (xMax >= in.width()) xMax = in.width() - 1;
        if (yMax >= in.height()) yMax = in.height() - 1;
        int[] neighbours = new int[(xMax - xMin) * (yMax - yMin)];
        int k = 0;
        for (int i = xMin; i <= xMax; ++i) {
            for (int j = yMin; j <= yMax; ++j) {
                neighbours[k] = in.valueAt(i, j);
            }
        }
        return neighbours;
    }
    
    private int getMedian(int[] neighbours) {
        int len = neighbours.length;
        for (int i = 0; i < len; ++i) {
            for (int j = 1; j < len; ++j) {
                if (neighbours[i] > neighbours[j]) {
                    neighbours[i] ^= neighbours[j];
                    neighbours[j] ^= neighbours[i];
                    neighbours[i] ^= neighbours[j];
                }
            }
        }
        return len % 2 == 1
            ? neighbours[len / 2]
            : (neighbours[len / 2] + neighbours[len / 2 - 1]) / 2;
    }
}
