package de.tu_chemnitz.mi.barcd.util;

/**
 * Implementation of the union-find algorithm.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class UnionFind {
    private final int[] parent;
    private final int[] rank;

    /**
     * Create a union-find data structure with a given size.
     *
     * @param size the size
     */
    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; ++i) {
            parent[i] = i;
        }
    }

    /**
     * @return the size of the union-find data structure
     */
    public int getSize() {
        return parent.length;
    }

    /**
     * Find the subset for an element.
     *
     * @param i the element
     *
     * @return the first element of the subset containing the requested element
     */
    public int find(int i) {
        int p = parent[i];
        if (i == p) {
            return i;
        }
        return parent[i] = find(p);
    }

    /**
     * Join the subsets each containing one of the given elements.
     *
     * @param i the element of one subset
     * @param j the element of another subset
     */
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