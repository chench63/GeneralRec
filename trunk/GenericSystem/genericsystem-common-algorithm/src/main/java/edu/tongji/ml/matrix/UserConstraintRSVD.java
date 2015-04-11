package edu.tongji.ml.matrix;

import prea.util.EvaluationMetrics;
import edu.tongji.data.SparseColumnMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: UserCOnstraintRSVD.java, v 0.1 2015-4-9 下午2:36:53 Exp $
 */
public class UserConstraintRSVD extends MatrixFactorizationRecommender {
    /** User profile in low-rank matrix form. */
    protected SparseColumnMatrix[] itemFeaturesAss;

    /** User profile in low-rank matrix form. */
    protected int[]                iAssigmnt;

    public SparseRowMatrix         test;

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
     * @param l The number of item clusters.
     * @param ua 
     */
    public UserConstraintRSVD(int uc, int ic, double max, double min, int fc, double lr, double r,
                              double m, int iter, int l, int[] ua, boolean verbose) {
        super(uc, ic, max, min, fc, lr, r, m, iter, verbose);
        itemFeaturesAss = new SparseColumnMatrix[l];
        iAssigmnt = ua;
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
        initFeatures();

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

                        // user clustering local models
                        SparseVector gi = itemFeaturesAss[iAssigmnt[u]].getCol(i);
                        double UuiEst = Fu.innerProduct(gi);
                        double errUui = AuiReal - UuiEst;

                        for (int s = 0; s < featureCount; s++) {
                            double Fus = userFeatures.getValue(u, s);
                            double Gis = itemFeatures.getValue(s, i);
                            double gis = itemFeaturesAss[iAssigmnt[u]].getValue(s, i);
                            //local models updates
                            itemFeaturesAss[iAssigmnt[u]].setValue(s, i,
                                gis + learningRate * (errUui * Fus - regularizer * gis));

                            //global model updates
                            userFeatures.setValue(u, s, Fus
                                                        + learningRate
                                                        * (err * Gis + errUui * gis - regularizer
                                                                                      * Fus));
                            itemFeatures.setValue(s, i, Gis + learningRate
                                                        * (err * Fus - regularizer * Gis));
                        }
                    }
                }
            }

            prevErr = currErr;
            currErr = Math.sqrt(sum / rateCount);

            round++;
            if (showProgress && (round % 10 == 0)) {
                EvaluationMetrics metric = this.evaluate(test);
                FileUtil.writeAsAppend(
                    "E://UC[" + featureCount + "]_k" + itemFeaturesAss.length + "_" + maxIter,
                    round + "\t" + String.format("%.4f", currErr) + "\t"
                            + String.format("%.4f", metric.getRMSE()) + "\n");
            }

            // Show progress:
            LoggerUtil.info(logger, round + "\t" + currErr);
        }
    }

    protected void initFeatures() {
        // Initialize user features:
        userFeatures = new SparseRowMatrix(userCount, featureCount);
        for (int u = 0; u < userCount; u++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                userFeatures.setValue(u, f, rdm);
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
                    itemFeaturesAss[k].setValue(i, f, rdm);
                }
            }
        }
    }
}
