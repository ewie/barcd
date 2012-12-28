package de.tu_chemnitz.mi.barcd.image;

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
        long t = System.currentTimeMillis();
        
        int width = (in.width() / SIZE) * SIZE;
        int height = (in.height() / SIZE) * SIZE;
        
        LuminanceImage out = new LuminanceImage(width, height);
        
        int[][] block = new int[SIZE][SIZE];
        long[][] dc = new long[width/SIZE][height/SIZE];
        long[][] ac = new long[width/SIZE][height/SIZE];
        
        for (int x = 0; x < width; x += SIZE) {
            for (int y = 0; y < height; y += SIZE) {
                for (int j = 0; j < SIZE; ++j) {
                    int s07, s12, s34, s56;
                    int d07, d12, d34, d56;
                    int a, b;

                    s07 = in.valueAt(x + 0, y + j) + in.valueAt(x + 7, y + j);
                    d07 = in.valueAt(x + 0, y + j) - in.valueAt(x + 7, y + j);
                    s12 = in.valueAt(x + 1, y + j) + in.valueAt(x + 2, y + j);
                    d12 = in.valueAt(x + 1, y + j) - in.valueAt(x + 2, y + j);
                    s34 = in.valueAt(x + 3, y + j) + in.valueAt(x + 4, y + j);
                    d34 = in.valueAt(x + 3, y + j) - in.valueAt(x + 4, y + j);
                    s56 = in.valueAt(x + 5, y + j) + in.valueAt(x + 6, y + j);
                    d56 = in.valueAt(x + 5, y + j) - in.valueAt(x + 6, y + j);
                    
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
                
                dc[x/SIZE][y/SIZE] = block[0][0] / (SIZE * SIZE);
                
                for (int i = 0; i < SIZE; ++i) {
                    for (int j = 0; j < SIZE; ++j) {
                        ac[x/SIZE][y/SIZE] += block[i][j] * block[i][j];
                    }
                }
                
                ac[x/SIZE][y/SIZE] /= SIZE * SIZE;
                ac[x/SIZE][y/SIZE] -= dc[x/SIZE][y/SIZE];
                
                out.setValueAt(x, y, (int) ac[x/SIZE][y/SIZE]);
                
            }
        }

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
        
        t = System.currentTimeMillis() - t;
        
        System.out.printf("BinDCT: %d\n", t);
        
        return out;
    }
}
