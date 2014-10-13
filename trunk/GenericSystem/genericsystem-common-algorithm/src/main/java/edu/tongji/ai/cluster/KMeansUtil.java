/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ai.cluster;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.log4j.Logger;

import edu.tongji.ai.support.Cluster;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.matrix.SparseMatrix;
import edu.tongji.matrix.SparseVector;
import edu.tongji.util.LoggerUtil;

/**
 * Clustering algorithm: basic K-means algorithm
 * 
 * @author Hanke Chen
 * @version $Id: KMeansUtil.java, v 0.1 10 Apr 2014 10:32:28 chench Exp $
 */
public final class KMeansUtil {

    //===================================
    //  Distance type
    //===================================
    /** L2 norm */
    public static final int       EUCLIDEAN_DISTANCE = 201;
    /** cosine distance*/
    public static final int       CONSINE_DISTANCE   = 202;

    /** logger */
    protected final static Logger logger             = Logger
                                                         .getLogger(LoggerDefineConstant.SERVICE_CORE);

    /**
     * forbid construction
     */
    private KMeansUtil() {

    }

    /**
     * divide points into k clusters
     * 
     * @param trnMatrix         the rows to cluster
     * @param K                 the number of clusters
     * @param maxIterations     the max iterations
     * @param type              the type of distance to use
     * @return
     */
    public static Cluster[] cluster(SparseMatrix trnMatrix, final int K, final int maxIterations,
                                    final int type) {
        // number of clusters has to be smaller or equal the number of data points
        if (trnMatrix == null | (trnMatrix.length())[0] < K) {
            throw new RuntimeException(
                "number of clusters has to be smaller or equal the number of data points !");
        }

        // create the initial clusters
        int pointCount = trnMatrix.length()[0];
        int[] assignments = new int[pointCount];
        Cluster[] resultSet = chooseInitialCenters(trnMatrix, assignments, K);

        //iterate through updating the centers until we're done
        int round = 1;
        while (round <= maxIterations) {

            // calculator centroind of each cluster
            SparseVector[] centroids = new SparseVector[K];
            for (int k = 0; k < K; k++) {
                centroids[k] = resultSet[k].centroid();
                resultSet[k].clear();
            }

            //assign every point to nearest centroid
            int change = 0;
            double SSE = 0.0d;
            for (int i = 0; i < pointCount; i++) {
                SparseVector point = trnMatrix.getRowRef(i);

                int pivot = -1;
                double min = Double.MAX_VALUE;
                for (int k = 0; k < K; k++) {
                    double distns = distance(centroids[k], point, type);
                    if (distns < min) {
                        min = distns;
                        pivot = k;
                    }
                }

                if (pivot != assignments[i]) {
                    LoggerUtil.debug(logger, " \tExchang: " + assignments[i] + "\t" + pivot);

                    assignments[i] = pivot;
                    change++;
                }
                resultSet[pivot].put(point);
                SSE += min;
            }
            LoggerUtil.info(logger, round + " \t" + SSE);
            round++;

            // if there were no more changes in the point-to-cluster assignment
            // and there are no empty clusters left, return the current clusters
            if (change == 0) {
                return resultSet;
            }
        }

        return resultSet;
    }

    /**
     * Use K-means to choose the initial centers.
     * 
     * @param points     the points to choose the initial centers from
     * @param K          the number of clusters
     * @return
     */
    private static Cluster[] chooseInitialCenters(SparseMatrix points, int[] assignments, int K) {
        Cluster[] clusters = new Cluster[K];
        for (int k = 0; k < K; k++) {
            clusters[k] = new Cluster();
        }

        int rowCount = points.length()[0];
        UniformRealDistribution uniform = new UniformRealDistribution(0, K);
        for (int i = 0; i < rowCount; i++) {
            SparseVector Ai = points.getRowRef(i);

            int k = (int) uniform.sample();
            clusters[k].put(Ai);
            assignments[i] = k;

        }

        return clusters;
    }

    /**
     * calculate the distance given two points
     * 
     * @param vct1      given point
     * @param vct2      given point
     * @param type      the type of the distance to calculate
     * @return
     */
    public static double distance(SparseVector vct1, SparseVector vct2, int type) {
        if (vct1 == null | vct2 == null) {
            return 0.0d;
        }

        switch (type) {
            case EUCLIDEAN_DISTANCE:
                SparseVector minus = vct1.minus(vct2);
                return minus.norm();
            case CONSINE_DISTANCE:
                if (vct1.itemCount() == 0 | vct2.itemCount() == 0) {
                    return 0.0d;
                }
                return vct1.innerProduct(vct2) / (vct1.norm() * vct2.norm());
            default:
                return 0.0d;
        }
    }
}
