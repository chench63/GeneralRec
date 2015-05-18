package edu.tongji.ml.matrix;

import java.io.Serializable;

import prea.util.EvaluationMetrics;

import org.apache.log4j.Logger;

import edu.tongji.data.DenseMatrix;
import edu.tongji.data.MatlabFasionSparseMatrix;
import edu.tongji.data.SparseColumnMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.log4j.LoggerDefineConstant;

/**
 * This is an abstract class implementing four matrix-factorization-based methods
 * including Regularized SVD, NMF, PMF, and Bayesian PMF.
 * 
 * @author Joonseok Lee
 * @since 2012. 4. 20
 * @version 1.1
 */
public abstract class MatrixFactorizationRecommender implements Serializable {
    /** SerialVersionNum */
    private static final long        serialVersionUID = 1L;

    /*========================================
     * Common Variables
     *========================================*/
    /** The number of users. */
    public int                       userCount;
    /** The number of items. */
    public int                       itemCount;
    /** Maximum value of rating, existing in the dataset. */
    public double                    maxValue;
    /** Minimum value of rating, existing in the dataset. */
    public double                    minValue;

    /** The number of features. */
    public int                       featureCount;
    /** Learning rate parameter. */
    public double                    learningRate;
    /** Regularization factor parameter. */
    public double                    regularizer;
    /** Momentum parameter. */
    public double                    momentum;
    /** Maximum number of iteration. */
    public int                       maxIter;

    /** Indicator whether to show progress of iteration. */
    public boolean                   showProgress;
    /** Offset to rating estimation. Usually this is the average of ratings. */
    protected double                 offset;

    /** User profile in low-rank matrix form. */
    protected SparseRowMatrix        userFeatures;
    /** Item profile in low-rank matrix form. */
    protected SparseColumnMatrix     itemFeatures;
    /** User profile in low-rank matrix form. */
    protected DenseMatrix            userDenseFeatures;
    /** Item profile in low-rank matrix form. */
    protected DenseMatrix            itemDenseFeatures;

    /** testMatrix used to render the fitted process. */
    transient public SparseRowMatrix tMatrix;

    /** logger */
    protected final static Logger    logger           = Logger
                                                          .getLogger(LoggerDefineConstant.SERVICE_CORE);

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
     * @param verbose Indicating whether to show iteration steps and train error.
     */
    public MatrixFactorizationRecommender(int uc, int ic, double max, double min, int fc,
                                          double lr, double r, double m, int iter, boolean verbose) {
        userCount = uc;
        itemCount = ic;
        maxValue = max;
        minValue = min;

        featureCount = fc;
        learningRate = lr;
        regularizer = r;
        momentum = m;
        maxIter = iter;

        showProgress = verbose;
    }

    public SparseRowMatrix getU() {
        return userFeatures;
    }

    public SparseColumnMatrix getV() {
        return itemFeatures;
    }

    /*========================================
     * Model Builder
     *========================================*/
    /**
     * Build a model with given training set.
     * 
     * @param rateMatrix The rating matrix with train data.
     */
    public void buildModel(SparseRowMatrix rateMatrix) {
        userFeatures = new SparseRowMatrix(userCount, featureCount);
        itemFeatures = new SparseColumnMatrix(featureCount, itemCount);

        // Initialize user/item features:
        for (int u = 0; u < userCount; u++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                userFeatures.setValue(u, f, rdm);
            }
        }
        for (int i = 0; i < itemCount; i++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                itemFeatures.setValue(f, i, rdm);
            }
        }
    }

    /**
     * Build a model with given training set.
     * 
     * @param rateMatrix The rating matrix with train data.
     */
    public void buildModel(SparseMatrix rateMatrix) {
        userFeatures = new SparseRowMatrix(userCount, featureCount);
        itemFeatures = new SparseColumnMatrix(featureCount, itemCount);

        // Initialize user/item features:
        for (int u = 0; u < userCount; u++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                userFeatures.setValue(u, f, rdm);
            }
        }
        for (int i = 0; i < itemCount; i++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                itemFeatures.setValue(f, i, rdm);
            }
        }
    }

    /*========================================
     * Prediction
     *========================================*/
    /**
     * Evaluate the designated algorithm with the given test data.
     * 
     * @param testMatrix The rating matrix with test data.
     * 
     * @return The result of evaluation, such as MAE, RMSE, and rank-score.
     */
    public EvaluationMetrics evaluate(SparseRowMatrix testMatrix) {
        SparseRowMatrix predicted = new SparseRowMatrix(userCount, itemCount);

        for (int u = 0; u < userCount; u++) {
            int[] testItems = testMatrix.getRowRef(u).indexList();

            if (testItems != null) {
                for (int t = 0; t < testItems.length; t++) {
                    int i = testItems[t];
                    double prediction = this.offset
                                        + userFeatures.getRowRef(u).innerProduct(
                                            itemFeatures.getColRef(testItems[t]));

                    if (prediction > maxValue) {
                        prediction = maxValue;
                    } else if (prediction < minValue) {
                        prediction = minValue;
                    }

                    predicted.setValue(u, i, prediction);
                }
            }
        }

        return new EvaluationMetrics(testMatrix, predicted, maxValue, minValue);
    }

    /**
     * Evaluate the designated algorithm with the given test data.
     * 
     * @param testMatrix The rating matrix with test data.
     * 
     * @return The result of evaluation, such as MAE, RMSE, and rank-score.
     */
    public double evaluate(MatlabFasionSparseMatrix testMatrix) {
        double RMSE = 0.0d;

        int rateCount = testMatrix.getNnz();
        int[] uIndx = testMatrix.getRowIndx();
        int[] iIndx = testMatrix.getColIndx();
        double[] Auis = testMatrix.getVals();
        for (int numSeq = 0; numSeq < rateCount; numSeq++) {

            int u = uIndx[numSeq];
            int i = iIndx[numSeq];
            double AuiReal = Auis[numSeq];
            double AuiEst = this.offset
                            + innerPrediction(u, i, userDenseFeatures, itemDenseFeatures);

            if (AuiEst > maxValue) {
                AuiEst = maxValue;
            } else if (AuiEst < minValue) {
                AuiEst = minValue;
            }

            RMSE += Math.pow(AuiReal - AuiEst, 2.0d);
        }

        return Math.sqrt(RMSE / rateCount);
    }

    /**
     * return the predicted rating
     * 
     * @param u the given user index
     * @param i the given item index
     * @return the predicted rating
     */
    public double predict(int u, int i) {
        double prediction = this.offset
                            + userFeatures.getRowRef(u).innerProduct(itemFeatures.getColRef(i));

        if (prediction > maxValue) {
            return maxValue;
        } else if (prediction < minValue) {
            return minValue;
        } else {
            return prediction;
        }
    }

    protected double innerPrediction(int u, int i, DenseMatrix uDenseFeature,
                                     DenseMatrix iDenseFeature) {
        double AuiEst = 0.0d;
        for (int f = 0; f < featureCount; f++) {
            AuiEst += uDenseFeature.getValue(u, f) * iDenseFeature.getValue(f, i);
        }
        return AuiEst;
    }

    /**
     * Return the weight of the given rating
     * 
     * @param u
     * @param i
     * @param weightIndx
     * @return
     */
    public double weight(int u, int i, double rating) {
        return 1.0;
    }

    /**
     * explicit clear the reference
     */
    public void explicitClear() {
        itemFeatures.clear();
        userFeatures.clear();
    }
}
