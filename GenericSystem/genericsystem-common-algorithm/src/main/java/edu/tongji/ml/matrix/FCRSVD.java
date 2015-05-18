package edu.tongji.ml.matrix;

import prea.util.EvaluationMetrics;
import edu.tongji.data.DenseMatrix;
import edu.tongji.data.MatlabFasionSparseMatrix;
import edu.tongji.data.SparseColumnMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: FCRSVD.java, v 0.1 2015-4-1 上午11:40:24 Exp $
 */
public class FCRSVD extends MatrixFactorizationRecommender {
    /** SerialVersionNum */
    private static final long      serialVersionUID = 1L;

    /** User profile in low-rank matrix form. */
    protected DenseMatrix[]        userDenseFeaturesAss;
    protected DenseMatrix[]        itemDenseFeaturesAss;

    /** User profile in low-rank matrix form. */
    protected SparseRowMatrix[]    userFeaturesAss;
    protected SparseColumnMatrix[] itemFeaturesAss;
    /** User profile in low-rank matrix form. */
    protected int[]                iAssigmnt;
    protected int[]                uAssigmnt;

    /*========================================
     * Constructors
     *========================================*/
    /**
     * Construct a matrix-factorization model with the given data.
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
    public FCRSVD(int uc, int ic, double max, double min, int fc, double lr, double r, double m,
                  int iter, int k, int l, int[] ua, int[] ia, boolean verbose) {
        super(uc, ic, max, min, fc, lr, r, m, iter, verbose);
        userFeaturesAss = new SparseRowMatrix[l];
        itemFeaturesAss = new SparseColumnMatrix[k];
        iAssigmnt = ua;
        uAssigmnt = ia;

        userDenseFeaturesAss = new DenseMatrix[l];
        itemDenseFeaturesAss = new DenseMatrix[k];
    }

    /*========================================
     * Model Builder
     *========================================*/
    /**
     * Build a model with given training set.
     * 
     * @param rateMatrix Training data set.
     */
    @Override
    public void buildModel(SparseRowMatrix rateMatrix) {
        // Initialize user features:
        userFeatures = new SparseRowMatrix(userCount, featureCount);
        for (int k = 0; k < userFeaturesAss.length; k++) {
            userFeaturesAss[k] = new SparseRowMatrix(userCount, featureCount);
        }
        for (int u = 0; u < userCount; u++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                userFeatures.setValue(u, f, rdm);
            }

            for (int k = 0; k < userFeaturesAss.length; k++) {
                for (int f = 0; f < featureCount; f++) {
                    double rdm = Math.random() / featureCount;
                    userFeaturesAss[k].setValue(u, f, rdm);
                }
            }
        }

        // Initialize item features:
        itemFeatures = new SparseColumnMatrix(featureCount, itemCount);
        for (int k = 0; k < itemFeaturesAss.length; k++) {
            itemFeaturesAss[k] = new SparseColumnMatrix(featureCount, itemCount);
        }
        for (int i = 0; i < itemCount; i++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                itemFeatures.setValue(f, i, rdm);
            }

            for (int k = 0; k < itemFeaturesAss.length; k++) {
                for (int f = 0; f < featureCount; f++) {
                    double rdm = Math.random() / featureCount;
                    itemFeaturesAss[k].setValue(f, i, rdm);
                }
            }
        }

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
                        //global model
                        SparseVector Fu = userFeatures.getRowRef(u);
                        SparseVector Gi = itemFeatures.getColRef(i);
                        double AuiEst = Fu.innerProduct(Gi);
                        double AuiReal = rateMatrix.getValue(u, i);
                        double err = AuiReal - AuiEst;
                        sum += Math.pow(err, 2.0d);

                        // item clustering local models
                        SparseVector fu = userFeaturesAss[uAssigmnt[i]].getRowRef(u);
                        double IuiEst = fu.innerProduct(Gi);
                        double errIui = AuiReal - IuiEst;

                        // user clustering local models
                        SparseVector gi = itemFeaturesAss[iAssigmnt[u]].getCol(i);
                        double UuiEst = Fu.innerProduct(gi);
                        double errUui = AuiReal - UuiEst;

                        for (int s = 0; s < featureCount; s++) {
                            double Fus = userFeatures.getValue(u, s);
                            double fus = userFeaturesAss[uAssigmnt[i]].getValue(u, s);
                            double Gis = itemFeatures.getValue(s, i);
                            double gis = itemFeaturesAss[iAssigmnt[u]].getValue(s, i);
                            //local models updates
                            userFeaturesAss[uAssigmnt[i]].setValue(u, s,
                                fus + learningRate * (errIui * Gis - regularizer * fus));
                            itemFeaturesAss[iAssigmnt[u]].setValue(s, i,
                                gis + learningRate * (errUui * Fus - regularizer * gis));

                            //global model updates
                            userFeatures.setValue(u, s, Fus
                                                        + learningRate
                                                        * (err * Gis + errUui * gis - regularizer
                                                                                      * Fus));
                            itemFeatures.setValue(s, i, Gis
                                                        + learningRate
                                                        * (err * Fus + errIui * fus - regularizer
                                                                                      * Gis));
                        }
                    }
                }
            }

            prevErr = currErr;
            currErr = Math.sqrt(sum / rateCount);

            round++;
            if (showProgress && (round % 5 == 0) && tMatrix != null) {
                EvaluationMetrics metric = this.evaluate(tMatrix);
                FileUtil.writeAsAppend(
                    "E://10m[" + featureCount + "]_k" + userFeaturesAss.length + "_" + maxIter,
                    round + "\t" + String.format("%.4f", currErr) + "\t"
                            + String.format("%.4f", metric.getRMSE()) + "\n");
            }

            // Show progress:
            LoggerUtil.info(logger, round + "\t" + currErr);
        }
    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.data.SparseMatrix)
     */
    @Override
    public void buildModel(SparseMatrix rateMatrix) {
        throw new RuntimeException("buildModel for SparseMatrix requires implementation!");
    }

    public void buildModel(MatlabFasionSparseMatrix rateMatrix, MatlabFasionSparseMatrix tMatrix) {
        // Initialize user/item features:
        userDenseFeatures = new DenseMatrix(userCount, featureCount);
        for (int k = 0; k < userDenseFeaturesAss.length; k++) {
            userDenseFeaturesAss[k] = new DenseMatrix(userCount, featureCount);
        }
        for (int u = 0; u < userCount; u++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                userDenseFeatures.setValue(u, f, rdm);
            }
            for (int k = 0; k < userDenseFeaturesAss.length; k++) {
                for (int f = 0; f < featureCount; f++) {
                    double rdm = Math.random() / featureCount;
                    userDenseFeaturesAss[k].setValue(u, f, rdm);
                }
            }
        }

        itemDenseFeatures = new DenseMatrix(featureCount, itemCount);
        for (int k = 0; k < itemDenseFeaturesAss.length; k++) {
            itemDenseFeaturesAss[k] = new DenseMatrix(featureCount, itemCount);
        }
        for (int i = 0; i < itemCount; i++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                itemDenseFeatures.setValue(f, i, rdm);
            }
            for (int k = 0; k < itemDenseFeaturesAss.length; k++) {
                for (int f = 0; f < featureCount; f++) {
                    double rdm = Math.random() / featureCount;
                    itemDenseFeaturesAss[k].setValue(f, i, rdm);
                }
            }
        }

        // Gradient Descent:
        int round = 0;
        int rateCount = rateMatrix.getNnz();
        double prevErr = 99999;
        double currErr = 9999;

        int[] uIndx = rateMatrix.getRowIndx();
        int[] iIndx = rateMatrix.getColIndx();
        double[] Auis = rateMatrix.getVals();
        while (Math.abs(prevErr - currErr) > 0.0001 && round < maxIter) {
            double sum = 0.0;

            for (int numSeq = 0; numSeq < rateCount; numSeq++) {
                int u = uIndx[numSeq];
                int i = iIndx[numSeq];

                //global model
                double AuiReal = Auis[numSeq];
                double AuiEst = innerPrediction(u, i, userDenseFeatures, itemDenseFeatures);
                double err = AuiReal - AuiEst;
                sum += Math.pow(err, 2.0d);

                // item clustering local models
                double IuiEst = innerPrediction(u, i, userDenseFeaturesAss[uAssigmnt[i]],
                    itemDenseFeatures);
                double errIui = AuiReal - IuiEst;

                // user clustering local models
                double UuiEst = innerPrediction(u, i, userDenseFeatures,
                    itemDenseFeaturesAss[iAssigmnt[u]]);
                double errUui = AuiReal - UuiEst;

                for (int s = 0; s < featureCount; s++) {
                    double Fus = userDenseFeatures.getValue(u, s);
                    double fus = userDenseFeaturesAss[uAssigmnt[i]].getValue(u, s);
                    double Gis = itemDenseFeatures.getValue(s, i);
                    double gis = itemDenseFeaturesAss[iAssigmnt[u]].getValue(s, i);
                    //local models updates
                    userDenseFeaturesAss[uAssigmnt[i]].setValue(u, s, fus
                                                                      + learningRate
                                                                      * (errIui * Gis - regularizer
                                                                                        * fus));
                    itemDenseFeaturesAss[iAssigmnt[u]].setValue(s, i, gis
                                                                      + learningRate
                                                                      * (errUui * Fus - regularizer
                                                                                        * gis));

                    //global model updates
                    userDenseFeatures.setValue(u, s, Fus
                                                     + learningRate
                                                     * (err * Gis + errUui * gis - regularizer
                                                                                   * Fus));
                    itemDenseFeatures.setValue(s, i, Gis
                                                     + learningRate
                                                     * (err * Fus + errIui * fus - regularizer
                                                                                   * Gis));
                }
            }

            prevErr = currErr;
            currErr = Math.sqrt(sum / rateCount);

            round++;
            if (showProgress && (round % 5 == 0) && tMatrix != null) {
                double rmse = this.evaluate(tMatrix);
                FileUtil.writeAsAppend(
                    "E://10m[" + featureCount + "]_" + itemFeaturesAss.length + "_"
                            + userFeaturesAss.length + "_" + maxIter,
                    round + "\t" + String.format("%.4f", currErr) + "\t"
                            + String.format("%.4f", rmse) + "\n");
            }

            // Show progress:
            LoggerUtil.info(logger, round + "\t" + currErr);
        }
    }

}
