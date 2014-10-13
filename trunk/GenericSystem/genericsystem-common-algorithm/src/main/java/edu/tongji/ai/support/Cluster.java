/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ai.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.tongji.matrix.SparseVector;

/**
 * The class is used in the K-means algorithm, which 
 * present a set of points.
 * 
 * @author Hanke Chen
 * @version $Id: Cluster.java, v 0.1 10 Apr 2014 11:17:20 chench Exp $
 */
public final class Cluster implements Iterable<SparseVector> {

    /** the set of points*/
    private final List<SparseVector> vectors = new LinkedList<SparseVector>();

    /**
     * add point to this cluster
     * 
     * @param pposition
     */
    public void put(SparseVector vect) {
        vectors.add(vect);
    }

    /**
     * remove all the points
     */
    public void clear() {
        vectors.clear();
    }

    /**
     * return the number of points in this cluster
     * 
     * @return
     */
    public int size() {
        return vectors.size();
    }

    /**
     * return an array of the vectors
     * 
     * @return the array of the vectors
     */
    @Override
    public Iterator<SparseVector> iterator() {
        return vectors.iterator();
    }

    /**
     * calculate the centroid point
     */
    public SparseVector centroid() {
        if (vectors.isEmpty()) {
            return null;
        }

        SparseVector result = new SparseVector(vectors.get(0).length());
        for (SparseVector vect : vectors) {
            result = result.plus(vect);
        }

        return result.scale(1.0 / vectors.size());
    }
}
