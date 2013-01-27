package de.tu_chemnitz.mi.barcd.image;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class DCT {
    private final int SIZE = 8;
    
    public LuminanceImage process(LuminanceImage in) {
        int width = (in.width() / SIZE) * SIZE;
        int height = (in.height() / SIZE) * SIZE;
        
        double n1 = Math.sqrt(1.0 / SIZE);
        double n2 = Math.sqrt(2.0 / SIZE);
        
        double[][] sum2 = new double[width / SIZE][height / SIZE];
        double[][] dct = new double[width][height];
        double pi = Math.PI / (2 * SIZE);
        
        BufferedLuminanceImage out = new BufferedLuminanceImage(width, height);
        
        for (int x = 0; x < width; x += SIZE) {
            for (int y = 0; y < height; y += SIZE) {
                for (int u = x; u < x + SIZE; ++u) {
                    double au = (u == x) ? n1 : n2;
                    for (int v = y; v < y + SIZE; ++v) {
                        double av = (v == y) ? n1 : n2;
                        double sum = 0;
                        for (int i = x; i < x + SIZE; ++i) {
                            for (int j = y; j < y + SIZE; ++j) {
                                //System.out.printf("%d %d\n", i, j);
                                sum += (in.intensityAt(i, j) - 128)
                                     * Math.cos((2 * (i-x) + 1) * (u-x) * pi)
                                     * Math.cos((2 * (j-y) + 1) * (v-y) * pi);
                            }
                        }
                        dct[u][v] = au * av * sum;
                        out.setIntensityAt(u, v, (int) dct[u][v]);
                        //if ((u-x) > (SIZE/2) && (v-y) > (SIZE/2)) {
                            sum2[x/SIZE][y/SIZE] += dct[u][v] * (u-x) * (v-y);
                        //}
                    }
                }
            }
        }
        
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        System.out.printf("%g %g\n", min, max);
        for (int i = 0; i < width/SIZE; ++i) {
            for (int j = 0; j < height/SIZE; ++j) {
                double d = sum2[i][j];
                if (d < min) min = d;
                if (d > max) max = d;
            }
        }
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                double d = sum2[x/SIZE][y/SIZE];
                out.setIntensityAt(x, y, Math.abs((int) ((d - min) / (max - min) * LuminanceImage.MAX_INTENSITY) - 128));
            }
        }
        
        return out;
    }
}
