/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ml.matrix;

import org.apache.log4j.Logger;

import prea.util.EvaluationMetrics;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.matrix.ComplicatedMatrix;
import edu.tongji.matrix.SparseMatrix;
import edu.tongji.matrix.SparseVector;

/**
 * 
 * @author Hanke Chen
 * @version $Id: BlockRegularizedSVD.java, v 0.1 2014-10-12 下午5:10:57 chench Exp $
 */
public class BlockRegularizedSVD {
    /*========================================
     * Common Variables
     *========================================*/
    /** The number of users. */
    public int                         userCount;
    /** The number of items. */
    public int                         itemCount;
    /** Maximum value of rating, existing in the dataset. */
    public double                      maxValue;
    /** Minimum value of rating, existing in the dataset. */
    public double                      minValue;

    /** The number of features. */
    public int                         featureCount;
    /** Learning rate parameter. */
    public double                      learningRate;
    /** Regularization factor parameter. */
    public double                      regularizer;
    /** Momentum parameter. */
    public double                      momentum;
    /** Maximum number of iteration. */
    public int                         maxIter;

    /** Offset to rating estimation. Usually this is the average of ratings. */
    protected double                   offset;

    /** User profile in low-rank matrix form. */
    protected SparseMatrix             userFeatures;
    /** Item profile in low-rank matrix form. */
    protected SparseMatrix             itemFeatures;

    /** the corresponding recommender w.r.t the blocks in ComplicateMatrix*/
    MatrixFactorizationRecommender[][] recommender;

    /** logger */
    protected final static Logger      logger = Logger.getLogger(LoggerDefineConstant.SERVICE_CORE);

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
    public BlockRegularizedSVD(int uc, int ic, double max, double min, int fc, double lr, double r,
                               double m, int iter) {
        userCount = uc;
        itemCount = ic;
        maxValue = max;
        minValue = min;

        featureCount = fc;
        learningRate = lr;
        regularizer = r;
        momentum = m;
        maxIter = iter;

    }

    /**
     * Build a model with given training set.
     * 
     * @param rateBlockes the matrix with training data
     */
    public void buildModel(ComplicatedMatrix rateBlockes) {

        int rowCount = rateBlockes.getRowCount();
        int colCount = rateBlockes.getColCount();
        recommender = new MatrixFactorizationRecommender[rowCount][colCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                SparseMatrix rateMatrix = rateBlockes.getBlock(i, j);
                int userCount = rateMatrix.length()[0] - 1;
                int itemCount = rateMatrix.length()[1];

                recommender[i][j] = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
                    featureCount, learningRate, regularizer, momentum, maxIter);
                recommender[i][j].buildModel(rateMatrix);
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
    public EvaluationMetrics evaluate(SparseMatrix testMatrix, ComplicatedMatrix rateBlockes) {
        SparseMatrix predicted = new SparseMatrix(userCount + 1, itemCount + 1);
        for (int u = 1; u <= userCount; u++) {
            int[] testItems = testMatrix.getRowRef(u).indexList();
            if (testItems != null) {
                for (int t = 0; t < testItems.length; t++) {
                    int i = testItems[t];

                    int[] position = rateBlockes.get(u, i);
                    int row = position[0];
                    int col = position[1];
                    int rowInner = position[2];
                    int colInner = position[3];
                    SparseVector Fu = recommender[row][col].userFeatures.getRowRef(rowInner);
                    SparseVector Gi = recommender[row][col].itemFeatures.getColRef(colInner);
                    double prediction = this.offset + Fu.innerProduct(Gi);

                    if (prediction < minValue) {
                        prediction = minValue;
                    } else if (prediction > maxValue) {
                        prediction = maxValue;
                    }

                    predicted.setValue(u, i, prediction);
                }
            }
        }

        return new EvaluationMetrics(testMatrix, predicted, maxValue, minValue);
    }
}
