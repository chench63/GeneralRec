/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ml;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.log4j.Logger;

import edu.tongji.data.Cluster;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * Basic K-Means Cluster
 * 
 * @author Hanke Chen
 * @version $Id: KMeansUtil.java, v 0.1 2014-10-14 上午11:19:24 chench Exp $
 */
public class KMeansUtil {

    /** cosine distance*/
    public final static int     SINE_DISTANCE              = 201;

    /** square error*/
    public final static int     SQUARE_ROOT_ERROR_DISTANCE = 202;

    /** logger */
    private final static Logger logger                     = Logger
                                                               .getLogger(LoggerDefineConstant.SERVICE_CORE);

    /**
     * forbid construction
     */
    private KMeansUtil() {

    }

    /**
     * divide the samples into K classes
     * 
     * @param points    the sample to be clustered, which every row is a sample
     * @param K         the number of classes
     * @param type      the type of distance involved
     * @return
     */
    public static Cluster[] divide(final SparseMatrix points, final int K, final int maxIteration,
                                   final int type) {
        int pointCount = points.length()[0];
        if (pointCount < K) {
            throw new RuntimeException("Number of samples is less than the number of classes.");
        }

        //make a initial division
        Cluster[] result = new Cluster[K];
        int[] assigmnt = new int[pointCount];
        chosenInitilization(result, assigmnt, pointCount, K);

        //converge to a minimun
        SparseVector[] centroids = new SparseVector[K];
        int round = 0;
        while (round < maxIteration) {

            //centroid
            for (int k = 0; k < K; k++) {
                centroids[k] = result[k].centroid(points);
                result[k].clear();
            }

            //minimize the sum of distance
            int changes = 0;
            double err = 0.0d;
            for (int i = 0; i < pointCount; i++) {
                int pivot = -1;
                double min = Double.MAX_VALUE;

                SparseVector a = points.getRow(i);
                for (int k = 0; k < K; k++) {
                    SparseVector b = centroids[k];
                    double distnce = distance(a, b, type);

                    if (min > distnce) {
                        min = distnce;
                        pivot = k;
                    }
                }
                err += min;

                if (pivot != assigmnt[i]) {
                    assigmnt[i] = pivot;
                    result[pivot].add(i);
                    changes++;
                }
            }

            //if no change, then exist
            if (changes == 0) {
                break;
            } else {
                round++;
                LoggerUtil.info(logger, round + "\t" + err);
            }

        }

        return result;
    }

    /**
     * make a initial division, and make sure no empty class
     * 
     * @param clusters
     * @param assigmnt
     * @param K
     */
    public static void chosenInitilization(Cluster[] clusters, int[] assigmnt, int pointCount,
                                           final int K) {
        for (int k = 0; k < K; k++) {
            clusters[k] = new Cluster();
        }

        UniformIntegerDistribution ran = new UniformIntegerDistribution(0, K - 1);
        boolean existEmptyCluster = false;
        do {
            for (int i = 0; i < pointCount; i++) {
                int k = ran.sample();
                clusters[k].add(i);
                assigmnt[i] = k;
            }

            for (int k = 0; k < K; k++) {
                if (clusters[k].isEmpty()) {
                    existEmptyCluster = true;
                    break;
                }
            }

        } while (existEmptyCluster);
    }

    /**
     * calculate the distance between two vectors
     *  
     * @param a     given vector
     * @param b     given vector
     * @param type  the distance to compute
     * @return
     */
    public static double distance(final SparseVector a, final SparseVector b, final int type) {

        switch (type) {
            case SINE_DISTANCE:
                double cos = a.innerProduct(b) / (a.norm() * b.norm());// a*b / (|a|*|b|)
                return Math.sqrt(1 - cos * cos);
            case SQUARE_ROOT_ERROR_DISTANCE:
                return (a.minus(b)).norm(); // |a-b|
            default:
                throw new RuntimeException("Wrong Distance Type! ");
        }
    }
}
