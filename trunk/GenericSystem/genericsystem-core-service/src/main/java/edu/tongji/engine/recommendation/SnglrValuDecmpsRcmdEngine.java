/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import java.util.HashMap;
import java.util.Map;

import prea.util.SimpleEvaluationMetrics;
import edu.tongji.data.SparseMatrix;
import edu.tongji.ml.matrix.MatrixFactorizationRecommender;
import edu.tongji.util.LoggerUtil;

/**
 * Regularized SVD method
 * 
 * @author Hanke Chen
 * @version $Id: SnglrValuDecmpsRcmdEngine.java, v 0.1 2014-10-14 下午3:28:47 chench Exp $
 */
public class SnglrValuDecmpsRcmdEngine extends RcmdtnEngine {

    /** matrix with training data*/
    private SparseMatrix                   rateMatrix;

    /** matrix with testing data*/
    private SparseMatrix                   testMatrix;

    /** svd-based recommender*/
    private MatrixFactorizationRecommender recommender;

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#excuteInner()
     */
    @Override
    protected void excuteInner() {
        LoggerUtil.info(logger, "3. initializing working threads.");
        recommender.buildModel(rateMatrix);

    }

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#evaluate()
     */
    @Override
    protected void evaluate() {
        SimpleEvaluationMetrics metrics = recommender.evaluate(testMatrix);
        LoggerUtil.info(logger, metrics.printOneLine());

        //iterate testMatrix
        Map<Double, Double> MSE = new HashMap<Double, Double>();
        int[] COUNT = new int[12];
        double totalMSE = 0.0d;
        for (int u = 0; u < recommender.userCount; u++) {
            int[] testItems = testMatrix.getRowRef(u).indexList();

            if (testItems != null) {
                for (int t = 0; t < testItems.length; t++) {
                    int i = testItems[t];
                    double prediction = recommender.getU().getRowRef(u)
                        .innerProduct(recommender.getV().getColRef(testItems[t]));

                    double realVal = testMatrix.getValue(u, i);
                    Double localMse = MSE.get(realVal);
                    if (localMse == null) {
                        localMse = 0.0d;
                    }
                    localMse += Math.pow(realVal - prediction, 2.0);
                    totalMSE += Math.pow(realVal - prediction, 2.0);
                    MSE.put(realVal, localMse);
                    COUNT[Double.valueOf(realVal / 0.5).intValue()]++;
                }
            }
        }
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
    public void setRateMatrix(SparseMatrix rateMatrix) {
        this.rateMatrix = rateMatrix;
    }

    /**
     * Setter method for property <tt>testingMatrix</tt>.
     * 
     * @param testingMatrix value to be assigned to property testingMatrix
     */
    public void setTestMatrix(SparseMatrix testMatrix) {
        this.testMatrix = testMatrix;
    }

    /**
     * Setter method for property <tt>recommender</tt>.
     * 
     * @param recommender value to be assigned to property recommender
     */
    public void setRecommender(MatrixFactorizationRecommender recommender) {
        this.recommender = recommender;
    }

}
