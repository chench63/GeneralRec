package edu.tongji.ml.matrix;

import prea.util.EvaluationMetrics;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: BorderFormConstraintSVD.java, v 0.1 2015-4-26 下午12:30:34 Exp $
 */
public class BorderFormConstraintSVD extends MatrixFactorizationRecommender {
    /** SerialVersionNum */
    private static final long                       serialVersionUID = 1L;

    /** A global SVD model. */
    public transient MatrixFactorizationRecommender auxRec;

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
     * @param verbose Indicating whether to show iteration steps and train error.
     */
    public BorderFormConstraintSVD(int uc, int ic, double max, double min, int fc, double lr,
                                   double r, double m, int iter, boolean verbose) {
        super(uc, ic, max, min, fc, lr, r, m, iter, verbose);
    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.data.SparseRowMatrix)
     */
    @Override
    public void buildModel(SparseRowMatrix rateMatrix, SparseRowMatrix testMatrix) {
        super.buildModel(rateMatrix, null);

        // Gradient Descent:
        int round = 0;
        int rateCount = rateMatrix.itemCount();
        double prevErr = 99999;
        double currErr = 9999;

        // Available Feature:
        boolean[] uAvail = new boolean[userCount];
        boolean[] iAvail = new boolean[itemCount];

        while (Math.abs(prevErr - currErr) > 0.0001 && round < maxIter) {
            double sum = 0.0;
            for (int u = 0; u < userCount; u++) {
                SparseVector items = rateMatrix.getRowRef(u);
                int[] itemIndexList = items.indexList();

                uAvail[u] = true;
                if (itemIndexList != null) {
                    for (int i : itemIndexList) {
                        iAvail[i] = true;

                        //global model
                        SparseVector Fu = userFeatures.getRowRef(u);
                        SparseVector Gi = itemFeatures.getColRef(i);
                        double AuiEst = Fu.innerProduct(Gi);
                        double AuiReal = rateMatrix.getValue(u, i);
                        double errAui = AuiReal - AuiEst;
                        sum += Math.pow(errAui, 2.0d);

                        // item clustering local models
                        SparseVector fu = auxRec.getU().getRowRef(u);
                        double IuiEst = fu.innerProduct(Gi);
                        double errIui = AuiReal - IuiEst;

                        // user clustering local models
                        SparseVector gi = auxRec.getV().getCol(i);
                        double UuiEst = Fu.innerProduct(gi);
                        double errUui = AuiReal - UuiEst;

                        for (int s = 0; s < featureCount; s++) {
                            double Fus = userFeatures.getValue(u, s);
                            double fus = auxRec.getU().getValue(u, s);
                            double Gis = itemFeatures.getValue(s, i);
                            double gis = auxRec.getV().getValue(s, i);

                            //global model updates
                            userFeatures.setValue(u, s,
                                Fus + learningRate
                                        * (errAui * Gis + errUui * gis - regularizer * Fus));
                            itemFeatures.setValue(s, i,
                                Gis + learningRate
                                        * (errAui * Fus + errIui * fus - regularizer * Gis));
                        }
                    }
                }
            }

            prevErr = currErr;
            currErr = Math.sqrt(sum / rateCount);

            round++;
            if (showProgress && (round % 10 == 0) && tMatrix != null) {
                EvaluationMetrics metric = this.evaluate(tMatrix);
                FileUtil.writeAsAppend(
                    "E://BFCSVD[" + featureCount + "]" + "_" + maxIter,
                    round + "\t" + String.format("%.6f", currErr) + "\t"
                            + String.format("%.6f", metric.getRMSE()) + "\n");
            }

            // Show progress:
            LoggerUtil.info(logger, round + "\t" + currErr);
        }

        //minimize the storage
        for (int u = 0; u < userCount; u++) {
            if (!uAvail[u]) {
                userFeatures.getRowRef(u).clear();
            }
        }
        for (int i = 0; i < itemCount; i++) {
            if (!iAvail[i]) {
                itemFeatures.getColRef(i).clear();
            }
        }
    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.data.SparseMatrix)
     */
    @Override
    public void buildModel(SparseMatrix rateMatrix) {
        throw new RuntimeException("buildModel for SparseMatrix requires implementation!");
    }

    /**
     * Setter method for property <tt>auxRec</tt>.
     * 
     * @param auxRec value to be assigned to property auxRec
     */
    public void setAuxRec(MatrixFactorizationRecommender auxRec) {
        this.auxRec = auxRec;
    }

}
