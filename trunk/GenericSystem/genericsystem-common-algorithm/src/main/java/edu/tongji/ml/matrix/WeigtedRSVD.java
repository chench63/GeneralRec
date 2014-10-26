/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ml.matrix;

import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: WeigtedRSVD.java, v 0.1 2014-10-19 上午11:20:27 chench Exp $
 */
public class WeigtedRSVD extends MatrixFactorizationRecommender {

    /**  */
    private static final long serialVersionUID = -6860703746675880356L;

    /** */
    private float[][]         userWeights;

    /** */
    private float[][]         itemWeights;

    /**
     * @param uc
     * @param ic
     * @param max
     * @param min
     * @param fc
     * @param lr
     * @param r
     * @param m
     * @param iter
     */
    public WeigtedRSVD(int uc, int ic, double max, double min, int fc, double lr, double r,
                       double m, int iter) {
        super(uc, ic, max, min, fc, lr, r, m, iter);
    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.data.SparseMatrix)
     */
    @Override
    public void buildModel(SparseMatrix rateMatrix) {
        super.buildModel(rateMatrix);

        //initialize weights
        getUserWeights(rateMatrix);
        getItemWeights(rateMatrix);

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
                                        * (err
                                           * Gis
                                           * (userWeights[u][weightIndx]
                                              * itemWeights[i][weightIndx] + base1) - regularizer
                                                                                      * Fus));
                            itemFeatures.setValue(s, i,
                                Gis
                                        + learningRate
                                        * (err
                                           * Fus
                                           * (userWeights[u][weightIndx]
                                              * itemWeights[i][weightIndx] + base1) - regularizer
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

    static float base1 = 1.350f;
    static float base2 = 0.5f;

    protected void getUserWeights(SparseMatrix rateMatrix) {
        int weightSize = Double.valueOf(maxValue / minValue).intValue();
        userWeights = new float[userCount][weightSize];
        for (int u = 0; u < userCount; u++) {
            SparseVector Fu = rateMatrix.getRowRef(u);
            int[] itemIndexList = Fu.indexList();

            if (itemIndexList == null) {
                for (int s = 0; s < weightSize; s++) {
                    userWeights[u][s] = base2 + 1.0f / weightSize;
                }
            } else {
                int total = weightSize;
                int[] rates = new int[weightSize];

                // count
                for (int i : itemIndexList) {
                    int indx = Double.valueOf(Fu.getValue(i) / minValue - 1).intValue();
                    rates[indx]++;
                    total++;
                }

                // compute weights
                for (int s = 0; s < weightSize; s++) {
                    userWeights[u][s] = base2 + (rates[s] + 1.0f) / total;
                }
            }
        }
    }

    protected void getItemWeights(SparseMatrix rateMatrix) {
        int weightSize = Double.valueOf(maxValue / minValue).intValue();
        itemWeights = new float[itemCount][weightSize];
        for (int i = 0; i < itemCount; i++) {
            SparseVector Gi = rateMatrix.getColRef(i);
            int[] userIndexList = Gi.indexList();

            if (userIndexList == null) {
                for (int s = 0; s < weightSize; s++) {
                    itemWeights[i][s] = base2 + 1.0f / weightSize;
                }
            } else {
                int total = weightSize;
                int[] rates = new int[weightSize];

                // count
                for (int u : userIndexList) {
                    int indx = Double.valueOf(Gi.getValue(u) / minValue - 1).intValue();
                    rates[indx]++;
                    total++;
                }

                // compute weights
                for (int s = 0; s < weightSize; s++) {
                    itemWeights[i][s] = base2 + (rates[s] + 1.0f) / total;
                }
            }

        }
    }

}
