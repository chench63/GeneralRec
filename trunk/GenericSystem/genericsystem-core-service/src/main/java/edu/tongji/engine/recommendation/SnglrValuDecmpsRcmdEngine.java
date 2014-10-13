/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import prea.util.EvaluationMetrics;
import edu.tongji.matrix.ComplicatedMatrix;
import edu.tongji.matrix.SparseMatrix;
import edu.tongji.ml.matrix.BlockRegularizedSVD;
import edu.tongji.ml.matrix.MatrixFactorizationRecommender;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: SnglrValuDecmpsRcmdEngine.java, v 0.1 2014-10-8 上午9:03:36 chench Exp $
 */
public class SnglrValuDecmpsRcmdEngine extends RcmdtnEngine {

    /** 优化行矩阵线程*/
    protected Runnable                     rowPrmutatnOptmzr;

    /** 优化列矩阵线程*/
    protected Runnable                     colPrmutatnOptmzr;

    /** 目标子矩阵群*/
    public static ComplicatedMatrix        rateBlockes;

    /** The rating matrix with train data.*/
    public static SparseMatrix             rateMatrix;

    /** The rating matrix with test data.*/
    public static SparseMatrix             testMatrix;

    /** Regularized SVD recomemder*/
    private MatrixFactorizationRecommender recommender;

    /** */
    private BlockRegularizedSVD            blockRecommender;

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#excuteInner()
     */
    @Override
    protected void excuteInner() {

        LoggerUtil.info(logger, "3. performing bussiness logics.");

        //1)Maximize Loss Function by Optimizing U^(i,j),V^(i,j)
        //  按配置划分矩阵，进行SVD    
        blockSVDInner();

    }

    /**
     * SVD，求解最优rank k的Matrix Decomposition
     */
    protected void blockSVDInner() {
        //        recommender.buildModel(rateMatrix);
        //        EvaluationMetrics metric = recommender.evaluate(testMatrix);
        //        LoggerUtil.info(logger, metric.printOneLine());

        blockRecommender.buildModel(rateBlockes);
        EvaluationMetrics metric = blockRecommender.evaluate(testMatrix, rateBlockes);
        LoggerUtil.info(logger, metric.printOneLine());
    }

    /**
     * Setter method for property <tt>rowPrmutatnOptmzr</tt>.
     * 
     * @param rowPrmutatnOptmzr value to be assigned to property rowPrmutatnOptmzr
     */
    public void setRowPrmutatnOptmzr(Runnable rowPrmutatnOptmzr) {
        this.rowPrmutatnOptmzr = rowPrmutatnOptmzr;
    }

    /**
     * Setter method for property <tt>colPrmutatnOptmzr</tt>.
     * 
     * @param colPrmutatnOptmzr value to be assigned to property colPrmutatnOptmzr
     */
    public void setColPrmutatnOptmzr(Runnable colPrmutatnOptmzr) {
        this.colPrmutatnOptmzr = colPrmutatnOptmzr;
    }

    /**
     * Setter method for property <tt>recommender</tt>.
     * 
     * @param recommender value to be assigned to property recommender
     */
    public void setRecommender(MatrixFactorizationRecommender recommender) {
        this.recommender = recommender;
    }

    /**
     * Setter method for property <tt>blockRecommender</tt>.
     * 
     * @param blockRecommender value to be assigned to property blockRecommender
     */
    public void setBlockRecommender(BlockRegularizedSVD blockRecommender) {
        this.blockRecommender = blockRecommender;
    }

}
