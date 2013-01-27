package de.tu_chemnitz.mi.barcd.image;

import de.tu_chemnitz.mi.barcd.image.threshold.MeanValueThresholdSelector;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class BinDCT {
    private final int SIZE = 8;
    
    private final int C1 = 16070;
    private final int C3 = 13623;
    private final int C4 = 11586;
    private final int C6 = 6270;
    
    private final int S1 = 3196;
    private final int S3 = 9102;
    private final int S6 = 15137;
    
    public LuminanceImage process(LuminanceImage in) {
        int width = (in.width() / SIZE) * SIZE;
        int height = (in.height() / SIZE) * SIZE;
        
        LuminanceImage out = new BufferedLuminanceImage(width, height);
        
        int[][] block = new int[SIZE][SIZE];
        long[][] dc = new long[width/SIZE][height/SIZE];
        long[][] ac = new long[width/SIZE][height/SIZE];
        long[][] sum = new long[width/SIZE][height/SIZE];
        long sumMax = 0;
        
        for (int x = 0; x < width; x += SIZE) {
            for (int y = 0; y < height; y += SIZE) {
                for (int j = 0; j < SIZE; ++j) {
                    int s07, s12, s34, s56;
                    int d07, d12, d34, d56;
                    int a, b;

                    s07 = in.intensityAt(x + 0, y + j) + in.intensityAt(x + 7, y + j);
                    d07 = in.intensityAt(x + 0, y + j) - in.intensityAt(x + 7, y + j);
                    s12 = in.intensityAt(x + 1, y + j) + in.intensityAt(x + 2, y + j);
                    d12 = in.intensityAt(x + 1, y + j) - in.intensityAt(x + 2, y + j);
                    s34 = in.intensityAt(x + 3, y + j) + in.intensityAt(x + 4, y + j);
                    d34 = in.intensityAt(x + 3, y + j) - in.intensityAt(x + 4, y + j);
                    s56 = in.intensityAt(x + 5, y + j) + in.intensityAt(x + 6, y + j);
                    d56 = in.intensityAt(x + 5, y + j) - in.intensityAt(x + 6, y + j);
                    
                    a = s07 + s34;
                    b = s12 + s56;
                    block[0][j] = C4 * (a + b) >> 15;
                    block[4][j] = C4 * (a - b) >> 15;
        
                    a = d12 - d56;
                    b = s07 - s34;
                    block[2][j] = (C6 * a + S6 * b) >> 15;
                    block[6][j] = (C6 * b - S6 * a) >> 15;
                    
                    a = d07 - (C4 * (s12 - s56) >> 14);
                    b = d34 - (C4 * (d12 + d56) >> 14);
                    block[3][j] = (C3 * a - S3 * b) >> 15;
                    block[5][j] = (S3 * a - C3 * b) >> 15;
                    
                    a = (d07 << 1) - a;
                    b = (d34 << 1) - b;
                    block[1][j] = (C1 * a + S1 * b) >> 15;
                    block[7][j] = (S1 * a - C1 * b) >> 15;
                }
                
                for (int i = 0; i < SIZE; ++i) {
                    int s07, s12, s34, s56;
                    int d07, d12, d34, d56;
                    int a, b;

                    s07 = block[i][0] + block[i][7];
                    d07 = block[i][0] - block[i][7];
                    s12 = block[i][1] + block[i][2];
                    d12 = block[i][1] - block[i][2];
                    s34 = block[i][3] + block[i][4];
                    d34 = block[i][3] - block[i][4];
                    s56 = block[i][5] + block[i][6];
                    d56 = block[i][5] - block[i][6];
                    
                    a = s07 + s34;
                    b = s12 + s56;
                    block[i][0] = C4 * (a + b) >> 15;
                    block[i][4] = C4 * (a - b) >> 15;
        
                    a = d12 - d56;
                    b = s07 - s34;
                    block[i][2] = (C6 * a + S6 * b) >> 15;
                    block[i][6] = (C6 * b - S6 * a) >> 15;
                    
                    a = d07 - (C4 * (s12 - s56) >> 14);
                    b = d34 - (C4 * (d12 + d56) >> 14);
                    block[i][3] = (C3 * a - S3 * b) >> 15;
                    block[i][5] = (S3 * a - C3 * b) >> 15;
                    
                    a = (d07 << 1) - a;
                    b = (d34 << 1) - b;
                    block[i][1] = (C1 * a + S1 * b) >> 15;
                    block[i][7] = (S1 * a - C1 * b) >> 15;
                }
                
                /*
                dc[x/SIZE][y/SIZE] = block[0][0] / (SIZE * SIZE);
                
                for (int i = 0; i < SIZE; ++i) {
                    for (int j = 0; j < SIZE; ++j) {
                        ac[x/SIZE][y/SIZE] += block[i][j] * block[i][j];
                    }
                }
                
                ac[x/SIZE][y/SIZE] /= SIZE * SIZE;
                ac[x/SIZE][y/SIZE] -= dc[x/SIZE][y/SIZE];
                
                out.setValueAt(x, y, (int) ac[x/SIZE][y/SIZE]);
                */
                
                
                for (int i = 0; i < SIZE; ++i) {
                    for (int j = 0; j < SIZE; ++j) {
                        sum[x/SIZE][y/SIZE] += Math.abs(block[i][j]) * i * j;
                    }
                }
                
                if (sum[x/SIZE][y/SIZE] > sumMax) {
                    sumMax = sum[x/SIZE][y/SIZE];
                }
                
            }
        }
        
        /*
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        
        for (int i = 0; i < width/SIZE; ++i) {
            for (int j = 0; j < height/SIZE; ++j) {
                long d = ac[i][j];
                if (d < min) min = d;
                if (d > max) max = d;
            }
        }
        
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                long d = ac[i/SIZE][j/SIZE];
                out.setValueAt(i, j, Math.abs((int) (((double) (d-min) / (max-min)) * LuminanceImage.MAX_VALUE)));
            }
        }
        */
        
        for (int x = 0; x < width; x += SIZE) {
            for (int y = 0; y < height; y += SIZE) {
                int v = (int) (((double) sum[x/SIZE][y/SIZE] / sumMax) * BufferedLuminanceImage.MAX_INTENSITY);
                for (int i = 0; i < SIZE; ++i) {
                    for (int j = 0; j < SIZE; ++j) {
                        out.setIntensityAt(x + i, y + j, v);
                    }
                }
                //out.setValueAt(x, y, (int) (((double) sum[x/SIZE][y/SIZE] / sumMax) * LuminanceImage.MAX_VALUE));
            }
        }
        
        MeanValueThresholdSelector mvt = new MeanValueThresholdSelector();
        GlobalThresholdSelector gt = new GlobalThresholdSelector() {
            @Override
            public int getThreshold(LuminanceImage source) {
                int[] vv = new int[256];
                for (int x = 0; x < source.width(); ++x) {
                    for (int y = 0; y < source.height(); ++y) {
                        int v = source.intensityAt(x, y);
                        vv[v] += 1;
                    }
                }
                int vmax = 0;
                int k = 0;
                for (int i = 64; i < 256; ++i) {
                    if (vv[i] > vmax) {
                        vmax = vv[i];
                        k = i;
                    }
                }
                return k;
            }
        };
            
        //System.out.printf("%d %d\n", mvt.getThreshold(out), gt.getThreshold(out));
        
        
        //out = new GaussianFilter(5, 0.5).apply(out);
        
        Binarizer bin = new GlobalBinarizer(mvt);
        out = bin.apply(out);
        
        return out;
    }
}
