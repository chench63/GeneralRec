package prea.recommender.matrix;

import prea.data.structure.FeatureMatrix;
import prea.data.structure.MatlabFasionSparseMatrix;
import prea.data.structure.SparseColumnMatrix;
import prea.data.structure.SparseMatrix;
import prea.data.structure.SparseRowMatrix;
import prea.recommender.Recommender;
import prea.util.EvaluationMetrics;
import prea.util.LoggerDefineConstant;

//import java.io.FileOutputStream;
//import java.io.PrintWriter;
import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * This is an abstract class implementing four matrix-factorization-based
 * methods including Regularized SVD, NMF, PMF, and Bayesian PMF.
 * 
 * @author Joonseok Lee
 * @since 2012. 4. 20
 * @version 1.1
 */
public abstract class MatrixFactorizationRecommender implements Recommender, Serializable {
    private static final long     serialVersionUID = 4000;

    /** logger */
    protected final static Logger logger           = Logger
        .getLogger(LoggerDefineConstant.SERVICE_CORE);

    /*
     * ======================================== Common Variables
     * ========================================
     */
    /** The number of users. */
    public int                    userCount;
    /** The number of items. */
    public int                    itemCount;
    /** Maximum value of rating, existing in the dataset. */
    public double                 maxValue;
    /** Minimum value of rating, existing in the dataset. */
    public double                 minValue;

    /** The number of features. */
    public int                    featureCount;
    /** Learning rate parameter. */
    public double                 learningRate;
    /** Regularization factor parameter. */
    public double                 regularizer;
    /** Momentum parameter. */
    public double                 momentum;
    /** Maximum number of iteration. */
    public int                    maxIter;

    /** Indicator whether to show progress of iteration. */
    public boolean                showProgress;
    /** Offset to rating estimation. Usually this is the average of ratings. */
    protected double              offset;

    /** User profile in low-rank matrix form. */
    protected SparseRowMatrix     userFeatures;
    /** Item profile in low-rank matrix form. */
    protected SparseColumnMatrix  itemFeatures;
    /** User profile in low-rank matrix form. */
    protected FeatureMatrix       userDenseFeatures;
    /** Item profile in low-rank matrix form. */
    protected FeatureMatrix       itemDenseFeatures;

    /*
     * ======================================== Constructors
     * ========================================
     */
    /**
     * Construct a matrix-factorization-based model with the given data.
     * 
     * @param uc
     *            The number of users in the dataset.
     * @param ic
     *            The number of items in the dataset.
     * @param max
     *            The maximum rating value in the dataset.
     * @param min
     *            The minimum rating value in the dataset.
     * @param fc
     *            The number of features used for describing user and item
     *            profiles.
     * @param lr
     *            Learning rate for gradient-based or iterative optimization.
     * @param r
     *            Controlling factor for the degree of regularization.
     * @param m
     *            Momentum used in gradient-based or iterative optimization.
     * @param iter
     *            The maximum number of iterations.
     * @param verbose
     *            Indicating whether to show iteration steps and train error.
     */
    public MatrixFactorizationRecommender(int uc, int ic, double max, double min, int fc, double lr,
                                          double r, double m, int iter, boolean verbose) {
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

    /*
     * ======================================== Model Builder
     * ========================================
     */
    /**
     * Build a model with given training set.
     * 
     * @param rateMatrix
     *            The rating matrix with train data.
     */
    @Override
    public void buildModel(SparseMatrix rateMatrix) {
        userFeatures = new SparseRowMatrix(userCount + 1, featureCount);
        itemFeatures = new SparseColumnMatrix(featureCount, itemCount + 1);

        // Initialize user/item features:
        for (int u = 1; u <= userCount; u++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                userFeatures.setValue(u, f, rdm);
            }
        }
        for (int i = 1; i <= itemCount; i++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                itemFeatures.setValue(f, i, rdm);
            }
        }
    }

    /**
     * Build a model with given training set.
     * 
     * @param rateMatrix
     *            The rating matrix with train data.
     */
    @Override
    public void buildModel(SparseRowMatrix rateMatrix) {
        userFeatures = new SparseRowMatrix(userCount + 1, featureCount);
        itemFeatures = new SparseColumnMatrix(featureCount, itemCount + 1);

        // Initialize user/item features:
        for (int u = 1; u <= userCount; u++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                userFeatures.setValue(u, f, rdm);
            }
        }
        for (int i = 1; i <= itemCount; i++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() / featureCount;
                itemFeatures.setValue(f, i, rdm);
            }
        }
    }

    /*
     * ======================================== Prediction
     * ========================================
     */
    /**
     * Evaluate the designated algorithm with the given test data.
     * 
     * @param testMatrix
     *            The rating matrix with test data.
     * 
     * @return The result of evaluation, such as MAE, RMSE, and rank-score.
     */
    @Override
    public EvaluationMetrics evaluate(SparseRowMatrix testMatrix) {
        SparseRowMatrix predicted = new SparseRowMatrix(userCount + 1, itemCount + 1);

        for (int u = 1; u <= userCount; u++) {
            int[] testItems = testMatrix.getRowRef(u).indexList();

            if (testItems != null) {
                for (int t = 0; t < testItems.length; t++) {
                    int i = testItems[t];
                    double prediction = this.offset + userFeatures.getRowRef(u)
                        .innerProduct(itemFeatures.getColRef(testItems[t]));
                    predicted.setValue(u, i, prediction);
                }
            }
        }

        return new EvaluationMetrics(testMatrix, predicted, maxValue, minValue);
    }

    public EvaluationMetrics evaluateX(SparseRowMatrix testMatrix) {
        SparseRowMatrix predicted = new SparseRowMatrix(userCount + 1, itemCount + 1);

        for (int u = 0; u < userCount; u++) {
            int[] testItems = testMatrix.getRowRef(u).indexList();

            if (testItems != null) {
                for (int t = 0; t < testItems.length; t++) {
                    int i = testItems[t];
                    double AuiEst = this.offset
                                    + innerPrediction(u, i, userDenseFeatures, itemDenseFeatures);

                    if (AuiEst > maxValue) {
                        AuiEst = maxValue;
                    } else if (AuiEst < minValue) {
                        AuiEst = minValue;
                    }

                    predicted.setValue(u, i, AuiEst);
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

    protected double innerPrediction(int u, int i, FeatureMatrix uDenseFeature,
                                     FeatureMatrix iDenseFeature) {
        double AuiEst = 0.0d;
        for (int f = 0; f < featureCount; f++) {
            AuiEst += uDenseFeature.getValue(u, f) * iDenseFeature.getValue(f, i);
        }
        return AuiEst;
    }

}
