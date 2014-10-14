/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ai.support;

import java.util.LinkedList;
import java.util.List;

import edu.tongji.matrix.SparseMatrix;
import edu.tongji.matrix.SparseVector;

/**
 * The class is used in the K-means algorithm, which 
 * present a set of points.
 * 
 * @author Hanke Chen
 * @version $Id: Cluster.java, v 0.1 10 Apr 2014 11:17:20 chench Exp $
 */
public final class Cluster {

    /** the set of index w.r.t the training data */
    private final List<Integer> indexList = new LinkedList<Integer>();

    /**
     * add point to this cluster
     * 
     * @param pposition
     */
    public void put(int index) {
        indexList.add(index);
    }

    /**
     * remove all the points
     */
    public void clear() {
        indexList.clear();
    }

    /**
     * return the index w.r.t the training matrix
     * 
     * @return
     */
    public int[] indexList() {
        if (indexList.size() == 0)
            return null;

        int len = indexList.size();
        int[] result = new int[len];
        for (int idx = 0; idx < len; idx++) {
            result[idx] = indexList.get(idx);
        }
        return result;
    }

    /**
     * return the number of points in this cluster
     * 
     * @return
     */
    public int size() {
        return indexList.size();
    }

    /**
     * calculate the centroid point
     */
    public SparseVector centroid(SparseMatrix trainingSet) {
        if (indexList.isEmpty()) {
            return null;
        }

        SparseVector result = trainingSet.getRowRef(0);
        for (int i = 1; i < indexList.size(); i++) {
            result = result.plus(trainingSet.getRowRef(indexList.get(i)));
        }
        return result.scale(1.0 / indexList.size());
    }
}
