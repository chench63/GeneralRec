package edu.tongji.ml;

import prea.util.EvaluationMetrics;
import edu.tongji.data.MatlabFasionSparseMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;

/**
 * 
 * @author Hanke
 * @version $Id: Recommender.java, v 0.1 2015-6-8 下午6:59:37 Exp $
 */
public abstract class Recommender {
    /*========================================
     * Common Variables
     *========================================*/
    /** The number of users. */
    public int    userCount;
    /** The number of items. */
    public int    itemCount;
    /** Maximum value of rating, existing in the dataset. */
    public double maxValue;
    /** Minimum value of rating, existing in the dataset. */
    public double minValue;

    /*========================================
     * Model Builder
     *========================================*/
    /**
     * Build a model with given training set.
     * 
     * @param rateMatrix The rating matrix with train data.
     * @param testMatrix The rating matrix with test data.
     */
    public abstract void buildModel(SparseRowMatrix rateMatrix, SparseRowMatrix testMatrix);

    /**
     * Build a model with given training set.
     * 
     * @param rateMatrix The rating matrix with train data.
     */
    public abstract void buildModel(SparseMatrix rateMatrix);

    /**
     * Build a model with given training set.
     * 
     * @param rateMatrix
     * @param tMatrix
     */
    public abstract void buildModel(MatlabFasionSparseMatrix rateMatrix,
                                    MatlabFasionSparseMatrix tMatrix);

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
    public abstract EvaluationMetrics evaluate(SparseRowMatrix testMatrix);

    /**
     * return the predicted rating
     * 
     * @param u the given user index
     * @param i the given item index
     * @return the predicted rating
     */
    public abstract double predict(int u, int i);

    /**
     * return the weight of which the prediction
     * 
     * @param u
     * @param i
     * @param rating
     * @return
     */
    public abstract double ensnblWeight(int u, int i, double rating);

    /**
     * drop references of Objects
     */
    public abstract void dropRef();
}
