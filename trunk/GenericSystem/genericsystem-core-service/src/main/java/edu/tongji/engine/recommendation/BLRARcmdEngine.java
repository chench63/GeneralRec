/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import java.util.HashMap;
import java.util.Map;

import prea.util.SimpleEvaluationMetrics;
import edu.tongji.data.BlockMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.ml.matrix.MatrixFactorizationRecommender;
//import edu.tongji.ml.matrix.RegularizedSVD;
import edu.tongji.ml.matrix.WeigtedRSVD;
import edu.tongji.util.LoggerUtil;

/**
 * Block Low-Rank Approximation
 * 
 * @author Hanke Chen
 * @version $Id: BLRARcmdEngine.java, v 0.1 2014-10-15 上午8:45:33 chench Exp $
 */
public class BLRARcmdEngine extends RcmdtnEngine {

    /**  matrix with training data */
    private BlockMatrix                        rateMatrixes;

    /**  matrix with testing data */
    private BlockMatrix                        testMatrixes;

    /** matrix with testing data*/
    private SparseMatrix                       testMatrix;

    /** svd-based recommender*/
    private MatrixFactorizationRecommender[][] recommender;

    /*========================================
     * Common Variables
     *========================================*/
    /** Maximum value of rating, existing in the dataset. */
    public double                              maxValue;
    /** Minimum value of rating, existing in the dataset. */
    public double                              minValue;
    /** The number of features. */
    public int                                 featureCount;
    /** Learning rate parameter. */
    public double                              learningRate;
    /** Regularization factor parameter. */
    public double                              regularizer;
    /** Momentum parameter. */
    public double                              momentum;
    /** Maximum number of iteration. */
    public int                                 maxIter;

    /** Offset to rating estimation. Usually this is the average of ratings. */
    protected double                           offset;

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#excuteInner()
     */
    @Override
    protected void excuteInner() {
        LoggerUtil.info(logger, "3. initializing working threads.");

        int[] bound = rateMatrixes.bound();
        int rowCount = bound[0];
        int colCount = bound[1];

        recommender = new WeigtedRSVD[rowCount][colCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                SparseMatrix rateMatrix = rateMatrixes.getBlock(i, j);
                int userCount = rateMatrix.length()[0];
                int itemCount = rateMatrix.length()[1];
                recommender[i][j] = new WeigtedRSVD(userCount, itemCount, maxValue, minValue,
                    featureCount, learningRate, regularizer, momentum, maxIter);
                recommender[i][j].buildModel(rateMatrix);

                SparseMatrix testMatrix = testMatrixes.getBlock(i, j);
                LoggerUtil.info(logger, i + " ," + j + " "
                                        + recommender[i][j].evaluate(testMatrix).printOneLine());
            }
        }

    }

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#evaluate()
     */
    @Override
    protected void evaluate() {
        super.evaluate();

        SparseMatrix predicted = new SparseMatrix(testMatrix);
        int userCount = predicted.length()[0];

        //iterate testMatrix
        Map<Double, Double> MSE = new HashMap<Double, Double>();
        int[] COUNT = new int[12];
        double totalMSE = 0.0d;
        for (int indx = 0; indx < userCount; indx++) {
            int[] testItems = testMatrix.getRowRef(indx).indexList();
            if (testItems == null) {
                continue;
            }

            //iterator elements in row indx
            for (int t : testItems) {
                int[] position = rateMatrixes.locate(indx, t);
                int Ri = position[0];
                int Rj = position[1];
                int u = position[2];
                int i = position[3];

                SparseMatrix userFeatures = recommender[Ri][Rj].getU();
                SparseMatrix itemFeatures = recommender[Ri][Rj].getV();

                double prediction = this.offset
                                    + userFeatures.getRowRef(u).innerProduct(
                                        itemFeatures.getColRef(i));

                if (prediction > maxValue) {
                    prediction = maxValue;
                }
                if (prediction < minValue) {
                    prediction = minValue;
                }

                System.out.println(testMatrix.getValue(indx, t) + "\t" + prediction);
                double real = (float) testMatrix.getValue(indx, t);
                Double localMse = MSE.get(real);
                if (localMse == null) {
                    localMse = 0.0d;
                }
                localMse += Math.pow(real - prediction, 2.0);
                totalMSE += Math.pow(real - prediction, 2.0);
                MSE.put(real, localMse);
                COUNT[Double.valueOf(real / 0.5).intValue()]++;

                predicted.setValue(indx, t, prediction);
            }
        }

        SimpleEvaluationMetrics metrics = new SimpleEvaluationMetrics(testMatrix, predicted,
            maxValue, minValue);
        LoggerUtil.info(logger, metrics.printOneLine());

        //calculate error distribution
        for (int i = 1; i < 12; i++) {
            Double localMse = MSE.get(i * 0.5);
            if (localMse == null) {
                continue;
            }
            LoggerUtil.info(logger,
                i * 0.5 + "\t" + localMse / totalMSE + "\t" + Math.sqrt(localMse / COUNT[i]));
        }
    }

    /**
     * Setter method for property <tt>rateMatrix</tt>.
     * 
     * @param rateMatrix value to be assigned to property rateMatrix
     */
    public void setRateMatrixes(BlockMatrix rateMatrix) {
        this.rateMatrixes = rateMatrix;
    }

    /**
     * Setter method for property <tt>testMatrixes</tt>.
     * 
     * @param testMatrixes value to be assigned to property testMatrixes
     */
    public void setTestMatrixes(BlockMatrix testMatrixes) {
        this.testMatrixes = testMatrixes;
    }

    /**
     * Setter method for property <tt>testMatrix</tt>.
     * 
     * @param testMatrix value to be assigned to property testMatrix
     */
    public void setTestMatrix(SparseMatrix testMatrix) {
        this.testMatrix = testMatrix;
    }

    /**
     * Setter method for property <tt>maxValue</tt>.
     * 
     * @param maxValue value to be assigned to property maxValue
     */
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Setter method for property <tt>minValue</tt>.
     * 
     * @param minValue value to be assigned to property minValue
     */
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    /**
     * Setter method for property <tt>featureCount</tt>.
     * 
     * @param featureCount value to be assigned to property featureCount
     */
    public void setFeatureCount(int featureCount) {
        this.featureCount = featureCount;
    }

    /**
     * Setter method for property <tt>learningRate</tt>.
     * 
     * @param learningRate value to be assigned to property learningRate
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    /**
     * Setter method for property <tt>regularizer</tt>.
     * 
     * @param regularizer value to be assigned to property regularizer
     */
    public void setRegularizer(double regularizer) {
        this.regularizer = regularizer;
    }

    /**
     * Setter method for property <tt>momentum</tt>.
     * 
     * @param momentum value to be assigned to property momentum
     */
    public void setMomentum(double momentum) {
        this.momentum = momentum;
    }

    /**
     * Setter method for property <tt>maxIter</tt>.
     * 
     * @param maxIter value to be assigned to property maxIter
     */
    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

}
