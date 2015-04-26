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
    public double[][] ensnblWeightInU;
    /** the rating distribution w.r.t each item*/
    public double[][] ensnblWeightInI;
    /** the rating distribution w.r.t the whole rating matrix*/
    public double[]   trainWeight;

    //===================================
    //      parameter
    //===================================
    /** parameter used in training*/
    public double     beta0 = 0.4f;
    /** parameter used in ensemble */
    public double     beta1 = 0.7f;
    /** parameter used in ensemble*/
    public double     beta2 = 0.8f;

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
                       double m, int iter, double b0, double b1, double b2) {
        super(uc, ic, max, min, fc, lr, r, m, iter, false);
        this.beta1 = b1;
        this.beta2 = b2;
        this.beta0 = b0;
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
        super(uc, ic, max, min, fc, lr, r, m, iter, false);
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
                                            * (err * Gis * (1 + beta0 * trainWeight[weightIndx]) - regularizer
                                                                                                   * Fus));
                            itemFeatures
                                .setValue(
                                    s,
                                    i,
                                    Gis
                                            + learningRate
                                            * (err * Fus * (1 + beta0 * trainWeight[weightIndx]) - regularizer
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
        throw new RuntimeException("buildModel for SparseMatrix requires implementation!");
    }

    /**
     * initialize the training parameters
     * 
     * @param rateMatrix
     */
    public void init(SparseRowMatrix rateMatrix) {
        trainWeight = new double[Double.valueOf(maxValue / minValue).intValue()];

        for (int u = 0; u < userCount; u++) {
            SparseVector items = rateMatrix.getRowRef(u);
            int[] itemIndexList = items.indexList();

            if (itemIndexList == null) {
                continue;
            }
            for (int i : itemIndexList) {
                double AuiReal = items.getValue(i);
                int weightIndx = Double.valueOf(AuiReal / minValue - 1).intValue();
                trainWeight[weightIndx] += 1;
            }
        }

        int totalCount = rateMatrix.itemCount();
        for (int i = 0; i < trainWeight.length; i++) {
            trainWeight[i] /= totalCount;
        }
    }

    /**
     * Return the weight of the given rating
     * 
     * @param u
     * @param i
     * @param weightIndx
     * @return
     */
    public double getWeight(int u, int i, double rating) {
        int weightIndx = Double.valueOf(rating / minValue - 1).intValue();
        return 1.0 + beta1 * ensnblWeightInU[u][weightIndx] + beta2
               * ensnblWeightInI[i][weightIndx];
    }

    public double getPu(int u, double rating) {
        int weightIndx = Double.valueOf(rating / minValue - 1).intValue();
        return ensnblWeightInU[u][weightIndx];
    }

    public double getPi(int i, double rating) {
        int weightIndx = Double.valueOf(rating / minValue - 1).intValue();
        return ensnblWeightInI[i][weightIndx];
    }

    /**
     * Setter method for property <tt>ensnblWeightInU</tt>.
     * 
     * @param ensnblWeightInU value to be assigned to property ensnblWeightInU
     */
    public void setEnsnblWeightInU(double[][] ensnblWeightInU) {
        this.ensnblWeightInU = ensnblWeightInU;
    }

    /**
     * Setter method for property <tt>ensnblWeightInI</tt>.
     * 
     * @param ensnblWeightInI value to be assigned to property ensnblWeightInI
     */
    public void setEnsnblWeightInI(double[][] ensnblWeightInI) {
        this.ensnblWeightInI = ensnblWeightInI;
    }

}
