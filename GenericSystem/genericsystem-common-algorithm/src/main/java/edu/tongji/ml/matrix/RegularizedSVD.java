package edu.tongji.ml.matrix;

import edu.tongji.matrix.SparseMatrix;
import edu.tongji.matrix.SparseVector;
import edu.tongji.util.LoggerUtil;

/**
 * This is a class implementing Regularized SVD (Singular Value Decomposition).
 * Technical detail of the algorithm can be found in
 * Arkadiusz Paterek, Improving Regularized Singular Value Decomposition Collaborative Filtering,
 * Proceedings of KDD Cup and Workshop, 2007.
 * 
 * @author Joonseok Lee
 * @since 2012. 4. 20
 * @version 1.1
 */
public class RegularizedSVD extends MatrixFactorizationRecommender {

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
    public RegularizedSVD(int uc, int ic, double max, double min, int fc, double lr, double r,
                          double m, int iter) {
        super(uc, ic, max, min, fc, lr, r, m, iter);
    }

    /*========================================
     * Model Builder
     *========================================*/
    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.matrix.SparseMatrix)
     */
    @Override
    public void buildModel(SparseMatrix rateMatrix) {
        super.buildModel(rateMatrix);

        //Gradient descent
        int round = 0;
        int rateCount = rateMatrix.itemCount();
        double prevErr = 9999;
        double currErr = 999;

        while (Math.abs(prevErr - currErr) > 0.0001 && round < this.maxIter) {
            double sae = 0.0d; // sum of absolute error
            for (int u = 1; u <= userCount; u++) {
                SparseVector items = rateMatrix.getRowRef(u);
                int[] itemIndexList = items.indexList();

                if (itemIndexList != null) {
                    for (int i : itemIndexList) {
                        SparseVector Fu = userFeatures.getRowRef(u);
                        SparseVector Gi = itemFeatures.getColRef(i);

                        double AuiEst = Fu.innerProduct(Gi);
                        double AuiReal = rateMatrix.getValue(u, i);
                        double err = AuiReal - AuiEst;
                        sae += Math.abs(err);

                        for (int k = 0; k < featureCount; k++) {
                            double Fuk = userFeatures.getValue(u, k);
                            double Gik = itemFeatures.getValue(k, i);

                            //F[u,k] += lrate * [r[u,i]*G[i,k] - lambda*F[u,k]]
                            userFeatures.setValue(u, k, Fuk + learningRate
                                                        * (err * Gik - regularizer * Fuk));
                            //G[i,k] += lrate * [r[u,i]*F[u,k] - lambda*G[i,k]]
                            itemFeatures.setValue(k, i, Gik + learningRate
                                                        * (err * Fuk - regularizer * Gik));
                        }
                    }
                }
            }

            prevErr = currErr;
            currErr = sae / rateCount;

            round++;

            //show log
            LoggerUtil.info(logger, round + " \t" + currErr);

        }
    }
}
