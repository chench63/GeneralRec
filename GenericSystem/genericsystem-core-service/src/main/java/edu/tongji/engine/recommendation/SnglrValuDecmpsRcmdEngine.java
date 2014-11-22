/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import org.springframework.util.StopWatch;

import prea.util.EvaluationMetrics;
import prea.util.MatrixInformationUtil;
import edu.tongji.data.SparseRowMatrix;
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
    private SparseRowMatrix                rateMatrix;

    /** matrix with testing data*/
    private SparseRowMatrix                testMatrix;

    /** svd-based recommender*/
    private MatrixFactorizationRecommender recommender;

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#excuteInner()
     */
    @Override
    protected void excuteInner() {
        LoggerUtil.info(logger, "3. Building model. Train: " + rateMatrix.itemCount() + " Test: "
                                + testMatrix.itemCount());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        recommender.buildModel(rateMatrix);
        stopWatch.stop();
        LoggerUtil.info(logger, "3. Model completes. T: " + stopWatch.getLastTaskTimeMillis());

    }

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#evaluate()
     */
    @Override
    protected void evaluate() {
        EvaluationMetrics metrics = recommender.evaluate(testMatrix);
        LoggerUtil.info(logger, metrics.printOneLine());

        //iterate testMatrix
        LoggerUtil.info(logger,
            MatrixInformationUtil.RMSEAnalysis(testMatrix, metrics.getPrediction()));

    }

    /**
     * Setter method for property <tt>rateMatrix</tt>.
     * 
     * @param rateMatrix value to be assigned to property rateMatrix
     */
    public void setRateMatrix(SparseRowMatrix rateMatrix) {
        this.rateMatrix = rateMatrix;
    }

    /**
     * Setter method for property <tt>testingMatrix</tt>.
     * 
     * @param testingMatrix value to be assigned to property testingMatrix
     */
    public void setTestMatrix(SparseRowMatrix testMatrix) {
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
