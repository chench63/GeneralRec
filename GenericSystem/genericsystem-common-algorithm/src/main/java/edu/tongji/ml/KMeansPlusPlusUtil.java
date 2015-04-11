/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ml;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.log4j.Logger;

import edu.tongji.data.Cluster;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;

/**
 * This is a class implementing K-means++.
 * Technical detail of the algorithm can be found in
 * Arthur, David, and Sergei Vassilvitskii, K-means++: The Advantages of Careful Seeding.
 * Proceedings of the eighteenth annual ACM-SIAM symposium on Discrete algorithms, 2007.
 * 
 * @author Hanke Chen
 * @version $Id: KMeansUtil.java, v 0.1 2014-10-14 上午11:19:24 chench Exp $
 */
public class KMeansPlusPlusUtil {

    /** sine distance*/
    public final static int     SINE_DISTANCE                = 201;
    /** square error*/
    public final static int     SQUARE_EUCLIDEAN_DISTANCE    = 202;
    /** pearson correlation*/
    public final static int     PEARSON_CORRELATION_DISTANCE = 203;
    /** KL divergence*/
    public final static int     KL_DISTANCE                  = 204;

    /** logger */
    private final static Logger logger                       = Logger
                                                                 .getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /**
     * forbid construction
     */
    private KMeansPlusPlusUtil() {

    }

    /**
     * divide the samples into K classes
     * 
     * @param points        the sample to be clustered, which every row is a sample
     * @param K             the number of classes
     * @param maxIteration  the maximum number of iterations
     * @param type          the type of distance involved
     * @return
     */
    public static Cluster[] cluster(final SparseMatrix points, final int K, final int maxIteration,
                                    final int type) {
        final int pointCount = points.length()[0];
        if (pointCount < K) {
            throw new RuntimeException("Number of samples is less than the number of classes.");
        }

        //Create the initial clusters
        LoggerUtil.info(logger, "0. Create the initial clusters.");
        SparseVector[] centroids = new SparseVector[K];
        chooseInitialCenters(points, centroids, K, type);

        //Converge to a minimun
        LoggerUtil.info(logger, "1. Locally search optimal solution.");
        Cluster[] resultSet = new Cluster[K];
        int[] assigmnt = new int[pointCount];
        double oldErr = Double.MAX_VALUE;
        double curErr = 0.0d;
        int round = 0;
        while (round < maxIteration) {
            //Create new set of elements
            Cluster[] newSet = new Cluster[K];
            for (int i = 0; i < K; i++) {
                newSet[i] = new Cluster();
            }

            //Greedy Strategy
            int changes = 0;
            curErr = 0.0d;
            for (int i = 0; i < pointCount; i++) {
                SparseVector a = points.getRowRef(i);

                //choose optimal centroid
                int pivot = -1;
                double min = Double.MAX_VALUE;
                for (int k = 0; k < K; k++) {
                    double distnce = distance(a, centroids[k], type);

                    if (min > distnce) {
                        min = distnce;
                        pivot = k;
                    }
                }
                curErr += min;

                if (pivot == -1) {
                    throw new RuntimeException("pivot equals -1. a:\n" + a);
                }
                //check change
                if (pivot != assigmnt[i]) {
                    changes++;
                }

                assigmnt[i] = pivot;
                newSet[pivot].add(i);
            }

            //Convex Optimization
            for (int k = 0; k < K; k++) {
                centroids[k] = newSet[k].centroid(points);
            }

            //if no change, then exist
            round++;
            if (changes == 0) {
                LoggerUtil.info(logger, round + "\tNo Changes");
                break;
            } else if (curErr > oldErr) {
                LoggerUtil.info(logger, round + "\t" + curErr + ">" + oldErr);
                break;
            } else {
                resultSet = newSet;
                LoggerUtil.info(logger, round + "\t" + curErr);
            }

        }

        return resultSet;
    }

    /**
     * make a initial division, and make sure no empty class
     * 
     * @param clusters
     * @param assigmnt
     * @param K
     */
    public static void chooseInitialCenters(final SparseMatrix points,
                                            final SparseVector[] centroids, final int K,
                                            final int type) {
        final int pointCount = points.length()[0];
        boolean[] taken = new boolean[pointCount];

        //Choose an initial center uniformly at random
        UniformRealDistribution ran = new UniformRealDistribution(0, 1);

        int indxOfFirstCentroid = (int) (ran.sample() * pointCount);
        centroids[0] = points.getRow(indxOfFirstCentroid);
        taken[indxOfFirstCentroid] = true;

        //Choose the rest centers
        int curCentroid = 1;
        while (curCentroid < K) {

            //Compute shortest distance from a data point to the closest center
            double[] D = new double[pointCount];
            double sum = 0.0d;
            for (int i = 0; i < pointCount; i++) {
                if (taken[i]) {
                    continue;
                }

                SparseVector point = points.getRowRef(i);
                double min = Double.MAX_VALUE;
                for (int j = 0; j < curCentroid; j++) {
                    double distance = distance(point, centroids[j], type);

                    if (min > distance) {
                        min = distance;
                    }
                }
                D[i] = min;
                sum += min;
            }

            //Choose next center x with probability D(x) / /Sigma D(x_i)
            double roullete = ran.sample();
            int pivot = -1;
            for (int i = 0; i < pointCount; i++) {
                if (taken[i]) {
                    continue;
                }

                roullete -= D[i] / sum;
                if (roullete < 0.0d) {
                    pivot = i;
                    break;
                }
            }

            //Update centroid information
            centroids[curCentroid] = points.getRow(pivot);
            taken[pivot] = true;
            curCentroid++;
        }
    }

    /**
     * calculate the distance between two vectors
     *  
     * @param a     given vector
     * @param b     given vector
     * @param type  the distance to compute
     * @return
     */
    public static double distance(final SparseVector a, final SparseVector centroid, final int type) {
        //check vector with all zeros
        if (a.norm() == 0 || centroid.norm() == 0) {
            return 0.0;
        }

        switch (type) {
            case SINE_DISTANCE:
                double cosine = a.innerProduct(centroid) / (a.norm() * centroid.norm());// a*b / (|a|*|b|)
                return Math.sqrt(1 - cosine * cosine);
            case SQUARE_EUCLIDEAN_DISTANCE:
                SparseVector c = a.minus(centroid);
                return c.innerProduct(c); // |a-b|
            case PEARSON_CORRELATION_DISTANCE:
                a.sub(a.average());
                centroid.sub(centroid.average());
                return a.innerProduct(centroid) / (a.norm() * centroid.norm());
            case KL_DISTANCE:
                double Dkl = 0.0d;
                for (int indx : centroid.indexList()) {
                    Dkl += centroid.getValue(indx)
                           * Math.log(centroid.getValue(indx) / a.getValue(indx));
                }
                return Dkl;
            default:
                throw new RuntimeException("Wrong Distance Type! ");
        }
    }
}
