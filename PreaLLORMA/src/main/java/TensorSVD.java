import prea.util.RankEvaluator;

import prea.data.structure.SparseMatrix;
import prea.data.structure.SparseTensor;

/**
 * This is a class implementing Regularized SVD based on tensor representation.
 * 
 * @author Joonseok Lee
 * @since 2013. 5. 28
 * @version 1.1
 */
public class TensorSVD {
    /*========================================
     * Common Variables
     *========================================*/
    /** The number of users. */
    public int             userCount;
    /** The number of items. */
    public int             itemCount;
    /** Maximum value of rating, existing in the dataset. */
    public double          maxValue;
    /** Minimum value of rating, existing in the dataset. */
    public double          minValue;

    /** The number of features. */
    public int             featureCount;
    /** Learning rate parameter. */
    public double          learningRate;
    /** Regularization factor parameter. */
    public double          regularizer;
    /** Maximum number of iteration. */
    public int             maxIter;

    /** Indicator whether to show progress of iteration. */
    public boolean         showProgress;

    /** User profile in low-rank matrix form. */
    protected SparseMatrix userFeatures;
    /** Item profile in low-rank matrix form. */
    protected SparseMatrix itemFeatures1;
    /** Item profile in low-rank matrix form. */
    //protected SparseMatrix itemFeatures2;

    /** The code of loss function which is minimized with this model. */
    private int            lossCode;

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
     * @param iter The maximum number of iterations.
     * @param verbose Indicating whether to show iteration steps and train error.
     */
    public TensorSVD(int uc, int ic, double max, double min, int fc, double lr, double r, int iter,
                     boolean verbose, int lc, SparseMatrix tr, SparseMatrix te) {
        userCount = uc;
        itemCount = ic;
        maxValue = max;
        minValue = min;

        featureCount = fc;
        learningRate = lr;
        regularizer = r;
        maxIter = iter;

        showProgress = verbose;

        lossCode = lc;
    }

    public SparseMatrix getU() {
        return userFeatures;
    }

    public SparseMatrix getV() {
        return itemFeatures1;
    }

    //	public SparseMatrix getW() {
    //		return itemFeatures2;
    //	}

    /*========================================
     * Model Builder
     *========================================*/
    /**
     * Build a model with given training set.
     * 
     * @param rateMatrix The rating matrix with train data.
     */
    public void buildModel(SparseMatrix rateMatrix) {
        initializeFeatures();
        SparseTensor rateTensor = constructTensor(rateMatrix, null);

        // Gradient Descent:
        int round = 0;
        double prevErr = 99999;
        double currErr = 9999;

        while (Math.abs(prevErr - currErr) > 0.0001 && round < maxIter) {
            double sum = 0.0;
            for (int u = 1; u <= userCount; u++) {
                for (int i = 1; i <= itemCount; i++) {
                    int[] itemIndexList = rateTensor.getRowRef(u).getRowRef(i).indexList();

                    if (itemIndexList != null) {
                        for (int j : itemIndexList) {
                            if (i > j) {
                                double TuiEst = makePrediction(u, i, j); // dF = Fui - Fuj
                                double TuiReal = rateTensor.getValue(u, i, j); // dM = Mui - Muj
                                double loss = RankEvaluator.loss(TuiReal, 0, TuiEst, 0, lossCode);
                                sum += loss;

                                if (Double.isNaN(loss)) {
                                    System.out.println();
                                }

                                for (int l = 0; l < featureCount; l++) {
                                    double Uul = userFeatures.getValue(u, l);
                                    double Vil = itemFeatures1.getValue(l, i);
                                    double Wjl = itemFeatures1.getValue(l, j); // itemFeatures2

                                    userFeatures.setValue(
                                        u,
                                        l,
                                        Uul
                                                - learningRate
                                                * (RankEvaluator.lossDiff(TuiReal, 0, TuiEst, 0,
                                                    lossCode) * Vil * Wjl + regularizer * Uul));
                                    itemFeatures1.setValue(
                                        l,
                                        i,
                                        Vil
                                                - learningRate
                                                * (RankEvaluator.lossDiff(TuiReal, 0, TuiEst, 0,
                                                    lossCode) * Uul * Wjl + regularizer * Vil));
                                    itemFeatures1.setValue(
                                        l,
                                        j,
                                        Vil
                                                - learningRate
                                                * (RankEvaluator.lossDiff(TuiReal, 0, TuiEst, 0,
                                                    lossCode) * Uul * Vil + regularizer * Wjl)); // itemFeatures2
                                }
                            }
                        }
                    }
                }
            }

            prevErr = currErr;
            currErr = sum / rateTensor.itemCount();

            round++;

            // Show progress:
            if (showProgress) {
                System.out.println(round + "\t" + Math.sqrt(currErr));
            }
        }
    }

    /**
     * Initialize user and item features with random assignment.
     */
    private void initializeFeatures() {
        userFeatures = new SparseMatrix(userCount + 1, featureCount);
        itemFeatures1 = new SparseMatrix(featureCount, itemCount + 1);
        //itemFeatures2 = new SparseMatrix(featureCount, itemCount+1); // itemFeatures2

        // Initialize user/item features:
        for (int u = 1; u <= userCount; u++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() - 0.5;
                userFeatures.setValue(u, f, rdm);
            }
        }
        for (int i = 1; i <= itemCount; i++) {
            for (int f = 0; f < featureCount; f++) {
                double rdm = Math.random() - 0.5;
                itemFeatures1.setValue(f, i, rdm);
            }
        }
        //		for (int i = 1; i <= itemCount; i++) { // itemFeatures2
        //			for (int f = 0; f < featureCount; f++) {
        //				double rdm = Math.random() / featureCount;
        //				itemFeatures2.setValue(f, i, rdm);
        //			}
        //		}
    }

    /**
     * Build a tensor based on the given matrix.
     * 
     * @param mainMatrix The main rating matrix. All pairs of points in this matrix are added to the tensor.
     * @param refMatrix The referenced rating matrix. A pair of one from main matrix and the other from this matrix is added to the tensor.
     * 
     * @return The tensor representation of the given matrix.
     */
    private SparseTensor constructTensor(SparseMatrix mainMatrix, SparseMatrix refMatrix) {
        SparseTensor t = new SparseTensor(userCount + 1, itemCount + 1, itemCount + 1);

        for (int u = 1; u <= userCount; u++) {
            int[] mainList = mainMatrix.getRowRef(u).indexList();
            if (mainList != null) {
                for (int i : mainList) {
                    for (int j : mainList) {
                        if (i > j) {
                            t.setValue(u, i, j,
                                mainMatrix.getValue(u, i) - mainMatrix.getValue(u, j));
                        }
                    }

                    if (refMatrix != null) {
                        int[] refList = refMatrix.getRowRef(u).indexList();
                        if (refList != null) {
                            for (int j : refList) {
                                if (i > j) {
                                    t.setValue(u, i, j,
                                        mainMatrix.getValue(u, i) - refMatrix.getValue(u, j));
                                }
                            }
                        }
                    }
                }
            }
        }

        return t;
    }

    /*========================================
     * Prediction
     *========================================*/
    /**
     * Evaluate the designated algorithm with the given test data.
     * 
     * @param testMatrix The rating matrix with test data.
     * 
     * @return The result of evaluation.
     */
    public String evaluate(SparseMatrix trainMatrix, SparseMatrix testMatrix) {
        SparseTensor testTensor = constructTensor(testMatrix, trainMatrix);

        double logLoss1 = 0.0;
        double sqrLoss = 0.0;

        // Prediction
        for (int u = 1; u <= userCount; u++) {
            int userCaseCount = 0;
            double userLogLoss1 = 0.0;
            double userSqrLoss = 0.0;

            for (int i = 1; i <= itemCount; i++) {
                int[] testItems = testTensor.getRowRef(u).getRowRef(i).indexList();

                if (testItems != null) {
                    for (int j : testItems) {
                        if (i > j) {
                            double prediction = makePrediction(u, i, j);
                            double real = testTensor.getValue(u, i, j);

                            userLogLoss1 += RankEvaluator.loss(real, 0, prediction, 0,
                                RankEvaluator.LOG_LOSS_MULT);
                            userSqrLoss += RankEvaluator.loss(real, 0, prediction, 0,
                                RankEvaluator.SQUARED_LOSS);
                            userCaseCount++;
                        }
                    }
                }
            }

            logLoss1 += (userLogLoss1 / userCaseCount);
            sqrLoss += (userSqrLoss / userCaseCount);
        }

        logLoss1 = logLoss1 / userCount;
        sqrLoss = Math.sqrt(sqrLoss / userCount);

        return String.format("%.4f\t%.4f", logLoss1, sqrLoss);
    }

    private double makePrediction(int u, int i, int j) {
        double prediction = 0.0;

        for (int l = 0; l < featureCount; l++) {
            prediction += userFeatures.getValue(u, l) * itemFeatures1.getValue(l, Math.max(i, j))
                          * itemFeatures1.getValue(l, Math.min(i, j)); // itemFeatures2

            if (Double.isNaN(prediction)) {
                System.out.println();
            }
        }

        return prediction;
    }
}
