/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import edu.tongji.matrix.ComplicatedMatrix;
import edu.tongji.matrix.SparseMatrix;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: SnglrValuDecmpsRcmdEngine.java, v 0.1 2014-10-8 上午9:03:36 chench Exp $
 */
public class SnglrValuDecmpsRcmdEngine extends RcmdtnEngine {

    /** 目标子矩阵群*/
    public static ComplicatedMatrix rateBlockes;

    /** The rating matrix with train data.*/
    public static SparseMatrix      rateMatrix;

    /** The rating matrix with test data.*/
    public static SparseMatrix      testMatrix;

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

    }

}
