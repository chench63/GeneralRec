package edu.tongji.ml.matrix;

import prea.util.EvaluationMetrics;
import prea.util.MatrixInformationUtil;
import edu.tongji.data.MatlabFasionSparseMatrix;
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
    /** Indicate whether the row is common */
    private boolean[]                               isCommonRow;
    /** Indicate whether the column is common */
    private boolean[]                               isCommonCol;
    /** the rating distribution w.r.t the whole rating matrix*/
    public double[]                                 trainWeight;
    /** A global SVD model. */
    public transient MatrixFactorizationRecommender auxRec;

    //===================================
    //      parameter
    //===================================
    /** parameter used in training*/
    public double                                   beta0            = 0.8f;
    public double                                   ru               = 1.0f;
    public double                                   ri               = 1.0f;

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
        isCommonRow = new boolean[uc];
        isCommonCol = new boolean[ic];
    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#localizedModel(edu.tongji.data.SparseMatrix, int[], int[])
     */
    @Override
    public void localizedModel(SparseMatrix rateMatrix, int[] rowInModel, int[] colInModel) {
        for (int row : rowInModel) {
            isCommonRow[row] = true;
        }

        for (int col : colInModel) {
            isCommonCol[col] = true;
        }
    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.data.SparseRowMatrix)
     */
    @Override
    public void buildModel(SparseRowMatrix rateMatrix, SparseRowMatrix testMatrix) {
        super.buildModel(rateMatrix, null);
        trainWeight = MatrixInformationUtil.ratingDistribution(rateMatrix, maxValue, minValue,
            isCommonRow, isCommonCol);

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
                            if (isCommonRow[u] & isCommonCol[i]) {
                                userFeatures.setValue(u, s,
                                    Fus + learningRate
                                            * (errAui * Gis + errUui * gis - regularizer * Fus));
                                itemFeatures.setValue(s, i,
                                    Gis + learningRate
                                            * (errAui * Fus + errIui * fus - regularizer * Gis));
                            } else if (isCommonRow[u]) {
                                userFeatures.setValue(u, s, Fus + learningRate
                                                            * (errUui * gis - regularizer * Fus));
                            } else if (isCommonCol[i]) {
                                itemFeatures.setValue(s, i, Gis + learningRate
                                                            * (errIui * fus - regularizer * Gis));
                            }
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
    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.data.SparseMatrix)
     */
    @Override
    public void buildModel(SparseMatrix rateMatrix) {
        throw new RuntimeException("buildModel for SparseMatrix requires implementation!");
    }

    /** 
     * @see edu.tongji.ml.matrix.MatrixFactorizationRecommender#buildModel(edu.tongji.data.MatlabFasionSparseMatrix, edu.tongji.data.MatlabFasionSparseMatrix)
     */
    @Override
    public void buildModel(MatlabFasionSparseMatrix rateMatrix, MatlabFasionSparseMatrix tMatrix) {
        super.buildModel(rateMatrix, tMatrix);
        trainWeight = MatrixInformationUtil.ratingDistribution(rateMatrix, maxValue, minValue,
            isCommonRow, isCommonCol);

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
            int sensitiveCount = 0;

            for (int numSeq = 0; numSeq < rateCount; numSeq++) {
                int u = uIndx[numSeq];
                int i = iIndx[numSeq];

                if (!isCommonRow[u] | !isCommonCol[i]) {
                    if (Math.random() > 0.5) {
                        continue;
                    }
                }

                //global model
                double AuiEst = userDenseFeatures.innerProduct(u, i, itemDenseFeatures);
                double AuiReal = Auis[numSeq];
                double errAui = AuiReal - AuiEst;

                // item clustering local models
                double IuiEst = auxRec.userDenseFeatures.innerProduct(u, i, itemDenseFeatures);
                double errIui = AuiReal - IuiEst;

                // user clustering local models
                double UuiEst = userDenseFeatures.innerProduct(u, i, auxRec.itemDenseFeatures);
                double errUui = AuiReal - UuiEst;

                int weightIndx = Double.valueOf(AuiReal / minValue - 1).intValue();
                for (int s = 0; s < featureCount; s++) {
                    double Fus = userDenseFeatures.getValue(u, s);
                    double fus = auxRec.userDenseFeatures.getValue(u, s);
                    double Gis = itemDenseFeatures.getValue(s, i);
                    double gis = auxRec.itemDenseFeatures.getValue(s, i);

                    //global model updates
                    if (isCommonRow[u] & isCommonCol[i]) {
                        userDenseFeatures.setValue(u, s,
                            Fus
                                    + learningRate
                                    * (errAui * Gis * (1 + beta0 * trainWeight[weightIndx])
                                       + errUui * gis * ru - regularizer * Fus));
                        itemDenseFeatures.setValue(s, i,
                            Gis
                                    + learningRate
                                    * (errAui * Fus * (1 + beta0 * trainWeight[weightIndx])
                                       + errIui * fus * ri - regularizer * Gis));
                        sum += Math.pow(errAui, 2.0d);
                        sensitiveCount++;
                    } else if (isCommonRow[u]) {
                        userDenseFeatures.setValue(u, s, Fus + learningRate
                                                         * (errUui * gis * ru - regularizer * Fus));
                    } else if (isCommonCol[i]) {
                        itemDenseFeatures.setValue(s, i, Gis + learningRate
                                                         * (errIui * fus * ri - regularizer * Gis));
                    }
                }
            }

            prevErr = currErr;
            currErr = Math.sqrt(sum / sensitiveCount);

            round++;
            if (showProgress && (round % 10 == 0) && tMatrix != null) {
                double rmse = this.evaluate(tMatrix);
                FileUtil.writeAsAppend(
                    "E://BFCSVD[" + featureCount + "]" + "_" + maxIter,
                    round + "\t" + String.format("%.6f", currErr) + "\t"
                            + String.format("%.6f", rmse) + "\n");
            }

            // Show progress:
            LoggerUtil.info(logger, round + "\t" + currErr);
        }
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
