package de.tu_chemnitz.mi.barcd.image;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ConnectedComponentLabeler {
    private static final int BACKGROUND_VALUE = LuminanceImage.MIN_INTENSITY;
    
    public int[][] process(LuminanceImage image) {
        return process(image, BACKGROUND_VALUE);
    }
    
    public int[][] process(LuminanceImage image, int backgroundValue) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] labels = new int[width][height];
        int nextLabel = 1;
        UnionFind eq = new UnionFind(width * height);
        
        for (int x = 1; x < width; ++x) {
            int v = image.getIntensityAt(x, 0);
            if (v == backgroundValue) continue;
            if (v == image.getIntensityAt(x - 1, 0)) {
                labels[x][0] = labels[x - 1][0];
            } else {
                labels[x][0] = nextLabel++;
            }
        }
        
        for (int y = 1; y < height; ++y) {
            int v = image.getIntensityAt(0, y);
            if (v == backgroundValue) continue;
            if (v == image.getIntensityAt(0, y - 1)) {
                labels[0][y] = labels[0][y - 1];
            } else {
                labels[0][y] = nextLabel++;
            }
        }
        
        for (int y = 1; y < height; ++y) {
            for (int x = 1; x < width; ++x) {
                int v = image.getIntensityAt(x, y);
                if (v == backgroundValue) continue;
                
                int vn = image.getIntensityAt(x, y - 1);
                int vw = image.getIntensityAt(x - 1, y);
                
                if (v == vn && v == vw) {
                    int ln = labels[x][y - 1];
                    int lw = labels[x - 1][y];
                    labels[x][y] = Math.min(ln, lw);
                    eq.union(lw, ln);
                    eq.union(ln, lw);
                } else if (v == vn) {
                    labels[x][y] = labels[x][y - 1];
                } else if (v == vw) {
                    labels[x][y] = labels[x - 1][y];
                } else {
                    labels[x][y] = nextLabel++;
                }
            }
        }
        
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (image.getIntensityAt(x, y) == backgroundValue) continue;
                int label = eq.find(labels[x][y]);
                labels[x][y] = label;
            }
        }
        
        return labels;
    }
    
    private static class UnionFind {
        private int[] parent;
        private int[] rank;
        
        public UnionFind(int max) {
            parent = new int[max];
            rank = new int[max];
            for (int i = 0; i < max; ++i) {
                parent[i] = i;
            }
        }
        
        public int find(int i) {
            int p = parent[i];
            if (i == p) {
                return i;
            }
            return parent[i] = find(p);
        }
        
        public void union(int i, int j) {
            int p = find(i);
            int q = find(j);
            if (p == q) return;
            if (rank[p] > rank[q]) {
                parent[q] = p;
            } else if (rank[p] < rank[q]) {
                parent[p] = q;
            } else {
                parent[q] = p;
                rank[p] += 1;
            }
        }
    }
}
