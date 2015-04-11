/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ml.matrix;

import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: WeigtedRSVD.java, v 0.1 2014-10-19 上午11:20:27 chench Exp $
 */
public class WeigtedRSVD extends MatrixFactorizationRecommender {

    /** the rating distribution w.r.t each user*/
    public float[][] userWeights;

    /** the rating distribution w.r.t each item*/
    public float[][] itemWeights;

    public float[]   totalWeights; //= { 0.0f, 0.0f, 0.0f, 0.5f, 0.0f };

    //===================================
    //      parameter
    //===================================
    public float     base1 = 0.45f;

    public float     base2 = 0.5f;

    public float     base3 = 0.0f;

    /*========================================
     * Constructors
     *========================================*/
    /**
     * Construct a matrix-factorization-based model with the given data.
     * 
     * @param uc The number of users in the dataset.
     * @param ic The number of items in the dataset.
     * @param max The maximum rating value in the dataset.
     * @param min The minimum rating value in the dataset.
     * @param fc The number of features used for describing user and item profiles.
     * @param lr Learning rate for gradient-based or iterative optimization.
     * @param r Controlling factor for the degree of regularization. 
     * @param m Momentum used in gradient-based or iterative optimization.
     * @param iter The maximum number of iterations.
     * @param b1 The base of learning rate.
     * @param b2 The base of userWeights or itemWeights.
     */
    public WeigtedRSVD(int uc, int ic, double max, double min, int fc, double lr, double r,
                       double m, int iter, float b1, float b2) {
        super(uc, ic, max, min, fc, lr, r, m, iter);
        this.base1 = b1;
        this.base2 = b2;
    }

    /**
     * Construct a matrix-factorization-based model with the given data.
     * 
     * @param uc The number of users in the dataset.
     * @param ic The number of items in the dataset.
     * @param max The maximum rating value in the dataset.
     * @param min The minimum rating value in the dataset.
     * @param fc The number of features used for describing user and item profiles.
     * @param lr Learning rate for gradient-based or iterative optimization.
     * @param r Controlling factor for the degree of regularization. 
     * @param m Momentum used in gradient-based or iterative optimization.
     * @param iter The maximum number of iterations.
     */
    public WeigtedRSVD(int uc, int ic, double max, double min, int fc, double lr, double r,
                       double m, int iter) {
        super(uc, ic, max, min, fc, lr, r, m, iter);
    }

    public void init(SparseRowMatrix rateMatrix) {
        totalWeights = new float[Double.valueOf(maxValue / minValue).intValue()];

        for (int u = 0; u < userCount; u++) {
            SparseVector items = rateMatrix.getRowRef(u);
            int[] itemIndexList = items.indexList();

            if (itemIndexList == null) {
                continue;
            }
            for (int i : itemIndexList) {
                double AuiReal = items.getValue(i);
                int weightIndx = Double.valueOf(AuiReal / minValue - 1).intValue();
                totalWeights[weightIndx] += 1;
            }
        }

        int totalCount = rateMatrix.itemCount();
        for (int i = 0; i < totalWeights.length; i++) {
            totalWeights[i] /= totalCount;
        }
    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.data.SparseMatrix)
     */
    @Override
    public void buildModel(SparseRowMatrix rateMatrix) {
        super.buildModel(rateMatrix);
        init(rateMatrix);

        // Gradient Descent:
        int round = 0;
        int rateCount = rateMatrix.itemCount();
        double prevErr = 99999;
        double currErr = 9999;

        while (Math.abs(prevErr - currErr) > 0.0001 && round < maxIter) {
            double sum = 0.0;
            for (int u = 0; u < userCount; u++) {
                SparseVector items = rateMatrix.getRowRef(u);
                int[] itemIndexList = items.indexList();

                if (itemIndexList != null) {
                    for (int i : itemIndexList) {
                        SparseVector Fu = userFeatures.getRowRef(u);
                        SparseVector Gi = itemFeatures.getColRef(i);

                        double AuiEst = Fu.innerProduct(Gi);
                        double AuiReal = rateMatrix.getValue(u, i);
                        double err = AuiReal - AuiEst;
                        sum += Math.abs(err);

                        int weightIndx = Double.valueOf(AuiReal / minValue - 1).intValue();
                        for (int s = 0; s < featureCount; s++) {
                            double Fus = userFeatures.getValue(u, s);
                            double Gis = itemFeatures.getValue(s, i);
                            //                            userFeatures.setValue(u, s,
                            //                                Fus
                            //                                        + learningRate
                            //                                        * (err * Gis * getWeight(u, i, weightIndx) - regularizer
                            //                                                                                     * Fus));
                            //                            itemFeatures.setValue(s, i,
                            //                                Gis
                            //                                        + learningRate
                            //                                        * (err * Fus * getWeight(u, i, weightIndx) - regularizer
                            //                                                                                     * Gis));

                            userFeatures
                                .setValue(
                                    u,
                                    s,
                                    Fus
                                            + learningRate
                                            * (err * Gis * (1 + base3 * totalWeights[weightIndx]) - regularizer
                                                                                                    * Fus));
                            itemFeatures
                                .setValue(
                                    s,
                                    i,
                                    Gis
                                            + learningRate
                                            * (err * Fus * (1 + base3 * totalWeights[weightIndx]) - regularizer
                                                                                                    * Gis));
                        }
                    }
                }
            }

            prevErr = currErr;
            currErr = sum / rateCount;

            round++;

            // Show progress:
            LoggerUtil.info(logger, round + "\t" + currErr);
        }

    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.data.SparseMatrix)
     */
    public void buildModel(SparseMatrix rateMatrix) {
        super.buildModel(rateMatrix);

        // Gradient Descent:
        int round = 0;
        int rateCount = rateMatrix.itemCount();
        double prevErr = 99999;
        double currErr = 9999;

        while (Math.abs(prevErr - currErr) > 0.0001 && round < maxIter) {
            double sum = 0.0;
            for (int u = 0; u < userCount; u++) {
                SparseVector items = rateMatrix.getRowRef(u);
                int[] itemIndexList = items.indexList();

                if (itemIndexList != null) {
                    for (int i : itemIndexList) {
                        SparseVector Fu = userFeatures.getRowRef(u);
                        SparseVector Gi = itemFeatures.getColRef(i);

                        double AuiEst = Fu.innerProduct(Gi);
                        double AuiReal = rateMatrix.getValue(u, i);
                        double err = AuiReal - AuiEst;
                        sum += Math.abs(err);

                        int weightIndx = Double.valueOf(AuiReal / minValue - 1).intValue();
                        for (int s = 0; s < featureCount; s++) {
                            double Fus = userFeatures.getValue(u, s);
                            double Gis = itemFeatures.getValue(s, i);
                            userFeatures.setValue(u, s,
                                Fus
                                        + learningRate
                                        * (err * Gis * getWeight(u, i, weightIndx) - regularizer
                                                                                     * Fus));
                            itemFeatures.setValue(s, i,
                                Gis
                                        + learningRate
                                        * (err * Fus * getWeight(u, i, weightIndx) - regularizer
                                                                                     * Gis));
                        }
                    }
                }
            }

            prevErr = currErr;
            currErr = sum / rateCount;

            round++;

            // Show progress:
            LoggerUtil.info(logger, round + "\t" + currErr);
        }

    }

    /**
     * compute the weight of the given rating
     * 
     * @param u
     * @param i
     * @param weightIndx
     * @return
     */
    public double getWeight(int u, int i, int weightIndx) {
        //b1 + (b2 + Pu)(b2 + Pi) + Pu*Pu +Pi*Pi
        //        return base1 + (base2 + userWeights[u][weightIndx]) * (base2 + itemWeights[i][weightIndx])
        //               + userWeights[u][weightIndx] * userWeights[u][weightIndx]
        //               + itemWeights[i][weightIndx] * itemWeights[i][weightIndx];

        //        return userWeights[u][weightIndx] + itemWeights[i][weightIndx];

        //        return 1.0;

        //        return 1.0 + base3 * totalWeights[weightIndx];

        //b1 + (b2 + Pu)(b2 + Pi)
        return base1 + (base2 + userWeights[u][weightIndx]) * (base2 + itemWeights[i][weightIndx]);
    }

    /**
     * explicit clear the reference
     */
    public void explicitClear() {
        itemFeatures.clear();
        userFeatures.clear();
        this.itemFeatures = null;
        this.userFeatures = null;
        this.itemWeights = null;
        this.userWeights = null;
    }

    /**
     * Setter method for property <tt>userWeights</tt>.
     * 
     * @param userWeights value to be assigned to property userWeights
     */
    public void setUserWeights(float[][] userWeights) {
        this.userWeights = userWeights;
    }

    /**
     * Setter method for property <tt>itemWeights</tt>.
     * 
     * @param itemWeights value to be assigned to property itemWeights
     */
    public void setItemWeights(float[][] itemWeights) {
        this.itemWeights = itemWeights;
    }

}
