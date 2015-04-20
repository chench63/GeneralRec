package edu.tongji.ml.matrix;

import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: WRSVD.java, v 0.1 2015-4-19 下午4:34:02 Exp $
 */
public class WRSVD extends MatrixFactorizationRecommender {

    //===================================
    //      parameter
    //===================================
    public double[] totalWeights;
    public double   beta0 = 0.0f;

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
     */
    public WRSVD(int uc, int ic, double max, double min, int fc, double lr, double r, double m,
                 int iter, boolean verbose, double b0) {
        super(uc, ic, max, min, fc, lr, r, m, iter, verbose);
        beta0 = b0;
    }

    public void init(SparseRowMatrix rateMatrix) {
        totalWeights = new double[Double.valueOf(maxValue / minValue).intValue()];

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
                        sum += Math.pow(err, 2.0d);

                        int weightIndx = Double.valueOf(AuiReal / minValue - 1).intValue();
                        for (int s = 0; s < featureCount; s++) {
                            double Fus = userFeatures.getValue(u, s);
                            double Gis = itemFeatures.getValue(s, i);

                            userFeatures
                                .setValue(
                                    u,
                                    s,
                                    Fus
                                            + learningRate
                                            * (err * Gis * (1 + beta0 * totalWeights[weightIndx]) - regularizer
                                                                                                    * Fus));
                            itemFeatures
                                .setValue(
                                    s,
                                    i,
                                    Gis
                                            + learningRate
                                            * (err * Fus * (1 + beta0 * totalWeights[weightIndx]) - regularizer
                                                                                                    * Gis));
                        }
                    }
                }
            }

            prevErr = currErr;
            currErr = Math.sqrt(sum / rateCount);

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
                            userFeatures
                                .setValue(
                                    u,
                                    s,
                                    Fus
                                            + learningRate
                                            * (err * Gis * (1 + beta0 * totalWeights[weightIndx]) - regularizer
                                                                                                    * Fus));
                            itemFeatures
                                .setValue(
                                    s,
                                    i,
                                    Gis
                                            + learningRate
                                            * (err * Fus * (1 + beta0 * totalWeights[weightIndx]) - regularizer
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

}
